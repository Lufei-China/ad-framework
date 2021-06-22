package com.framework.starter.discovery.client;

import com.framework.starter.discovery.config.EtcdProperties;
import com.framework.starter.discovery.event.RegisterEvent;
import com.framework.starter.discovery.service.ServiceEntity;
import com.framework.util.json.ObjectMapperUtils;
import com.google.common.collect.Sets;
import io.etcd.jetcd.*;
import io.etcd.jetcd.kv.GetResponse;
import io.etcd.jetcd.lease.LeaseGrantResponse;
import io.etcd.jetcd.lease.LeaseKeepAliveResponse;
import io.etcd.jetcd.options.GetOption;
import io.etcd.jetcd.options.PutOption;
import io.etcd.jetcd.options.WatchOption;
import io.etcd.jetcd.watch.WatchResponse;
import io.netty.util.concurrent.DefaultThreadFactory;
import lombok.Data;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.Set;
import java.util.concurrent.*;

/**
 * 功能概述
 * className:      EtcdClient
 * package:        com.framework.starter.discovery.client
 * author:         Gavin.Xu
 * date:           2021/6/8
 */
@Data
@Slf4j
public class EtcdDiscoveryClient extends DiscoveryClient implements ApplicationContextAware {

    public final Client client;
    private final EtcdProperties properties;
    private ApplicationContext context;
    private static final int MAX_RETRY = 5;
    private volatile boolean isRegister;

    public EtcdDiscoveryClient(@NonNull EtcdProperties etcdProperties) {
        this.properties = etcdProperties;
        String endpoints = etcdProperties.getEndpoints();
        client = Client.builder().endpoints(endpoints).build();
    }

    @Override
    public void addAndKeep(String key, String value) {
        long leaseId = 0;
        Lease leaseClient = this.client.getLeaseClient();
        for (int i = 0; i < MAX_RETRY; i++) {
            CompletableFuture<LeaseGrantResponse> grant = leaseClient.grant(this.properties.getLeaseTime().toMillis() / 1000, this.properties.getTimeout().toNanos(), TimeUnit.NANOSECONDS);
            try {
                LeaseGrantResponse leaseGrantResponse = grant.get(5, TimeUnit.SECONDS);
                leaseId = leaseGrantResponse.getID();
                break;
            } catch (Exception e) {
                log.error("etcd get leaseId error", e);
            }
        }
        if (leaseId == 0) {
            log.warn("addAndKeep key:{},value:{} can't get a leaseId, check etcd connection...");
        }
        KV kvClient = this.client.getKVClient();
        ByteSequence putKey = ByteSequence.from(key.getBytes());
        ByteSequence putValue = ByteSequence.from(value.getBytes());
        kvClient.put(putKey, putValue, PutOption.newBuilder().withLeaseId(leaseId).build());
        final long finalId = leaseId;
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1,
                new DefaultThreadFactory("etcd-keep-")
        );
        executor.scheduleAtFixedRate(() -> {
            try {
                LeaseKeepAliveResponse response = leaseClient.keepAliveOnce(finalId).get();
                if (response.getTTL() == 0){
                    executor.shutdown();
                    addAndKeep(key,value);
                }
            } catch (Exception e) {
                log.error("etcd keepAliveOnce error", e);
            }
        }, 0, this.properties.getScheduleLeaseTime().toNanos(), TimeUnit.NANOSECONDS);

    }

    @Override
    public Set<ServiceEntity> findServices(String key) {
        KV kvClient = client.getKVClient();
        ByteSequence keyBytes = ByteSequence.from(key.getBytes());
        Set<ServiceEntity> serviceEntities = Sets.newHashSet();
        try {
            CompletableFuture<GetResponse> future = kvClient.get(keyBytes, GetOption.newBuilder().withPrefix(keyBytes).build());
            GetResponse response = future.get();
            for (KeyValue kv : response.getKvs()) {
                String value = new String(kv.getValue().getBytes());
                ServiceEntity serviceEntity = ObjectMapperUtils.fromJSON(value, ServiceEntity.class);
                String[] hostPort = serviceEntity.getEndPoint().split(":");
                serviceEntity.setHost(hostPort[0]);
                serviceEntity.setPort(Integer.parseInt(hostPort[1]));
                log.debug("find service info:{}", serviceEntity);
                serviceEntities.add(serviceEntity);
            }
        } catch (Exception e) {
            log.warn("EtcdClient getValues error!", e);
        }
        return serviceEntities;
    }

    @Override
    public void watch(String key) {
        log.debug("etcd start watch key:{} ", key);
        CountDownLatch latch = new CountDownLatch(1);
        Watch watchClient = this.client.getWatchClient();
        WatchOption watchOption = WatchOption.newBuilder()
                .withPrefix(ByteSequence.from(key.getBytes()))
                .build();
        watchClient.watch(ByteSequence.from(key.getBytes()), watchOption, new EtcdListener(context, latch));
        log.debug("etcd end watch key:{} ", key);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.context = applicationContext;
    }

    static class EtcdListener implements Watch.Listener {

        private final ApplicationContext context;
        private final CountDownLatch latch;

        public EtcdListener(ApplicationContext context, CountDownLatch latch) {
            this.context = context;
            this.latch = latch;
        }

        @Override
        public void onNext(WatchResponse response) {
            latch.countDown();
            log.debug("etcd event trigger, thread:{}", Thread.currentThread().getName());
            context.publishEvent(new RegisterEvent(this));
        }

        @Override
        public void onError(Throwable throwable) {

        }

        @Override
        public void onCompleted() {

        }
    }

}
