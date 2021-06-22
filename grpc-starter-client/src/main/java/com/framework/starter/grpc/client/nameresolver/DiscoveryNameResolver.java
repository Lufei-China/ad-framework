package com.framework.starter.grpc.client.nameresolver;

import com.framework.starter.discovery.client.DiscoveryClient;
import com.framework.starter.discovery.enums.Protocol;
import com.framework.starter.discovery.service.ServiceEntity;
import com.google.common.collect.Lists;
import io.grpc.Attributes;
import io.grpc.EquivalentAddressGroup;
import io.grpc.NameResolver;
import io.grpc.SynchronizationContext;
import io.netty.util.concurrent.DefaultThreadFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.stream.Collectors;

import static java.util.Objects.requireNonNull;
import static lombok.Lombok.checkNotNull;

/**
 * 功能概述
 * className:      DiscoveryNameResolver
 * package:        com.framework.starter.grpc.client.nameresolver
 * author:         Gavin.Xu
 * date:           2021/6/8
 */
@Slf4j
public class DiscoveryNameResolver extends NameResolver {

    private String key;
    private DiscoveryClient client;
    private Listener listener;
    private final SynchronizationContext syncContext;
    private volatile boolean resolving;
    private ExecutorService executorService = Executors.newSingleThreadExecutor(new DefaultThreadFactory("discovery-watch-thread", false));
    public static final Attributes.Key<Integer> LB_WEIGHT_INFO = Attributes.Key.create("lb-weight-info");


    public DiscoveryNameResolver(String key, DiscoveryClient client, final Args args) {
        this.key = key;
        this.client = client;
        this.syncContext = requireNonNull(args.getSynchronizationContext(), "syncContext");
    }


    @Override
    public void refresh() {
        /*this.syncContext.execute(() -> {
            this.resolving = false;
            if (this.listener != null) {
                resolve();
            }
        });*/
    }

    @Override
    public void start(Listener listener) {
        this.listener = checkNotNull(listener, "listener");
        resolve();
    }

    private void resolve() {
        log.debug("Scheduled resolve for {}", this.key);
        if (this.resolving) {
            return;
        }
        this.resolving = true;
        resolvingAddress();
        executorService.submit(() -> this.client.watch(key));
    }

    protected void resolvingAddress() {
        final List<EquivalentAddressGroup> targets = Lists.newArrayList();
        Set<ServiceEntity> services = this.client.findServices(key);
        if (!CollectionUtils.isEmpty(services)) {
            log.info("find service {} , hosts size {}", key, services.size());
            Map<Integer, List<ServiceEntity>> serviceMap = services.stream().collect(
                    Collectors.groupingBy(ServiceEntity::getLbWeight)
            );
            for (Integer lbWeight : serviceMap.keySet()) {
                Attributes attributes = Attributes.newBuilder()
                        .set(LB_WEIGHT_INFO, lbWeight)
                        .build();
                List<ServiceEntity> serviceEntities = serviceMap.get(lbWeight);
                List<SocketAddress> address = serviceEntities.stream().map(p -> new InetSocketAddress(p.getHost(), p.getPort())).collect(Collectors.toList());
                EquivalentAddressGroup addressGroup = new EquivalentAddressGroup(address, attributes);
                targets.add(addressGroup);
            }
        } else {
            log.warn("find no services {}", key);
        }
        this.listener.onAddresses(targets, Attributes.EMPTY);
    }


    @Override
    public String getServiceAuthority() {
        return this.key;
    }

    @Override
    public void shutdown() {

    }
}
