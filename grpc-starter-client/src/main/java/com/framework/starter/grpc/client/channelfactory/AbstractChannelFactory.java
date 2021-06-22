package com.framework.starter.grpc.client.channelfactory;

import com.framework.starter.discovery.enums.Protocol;
import com.framework.starter.discovery.util.DiscoveryUtil;
import com.framework.starter.grpc.client.config.GrpcClientProperties;
import com.framework.starter.grpc.client.interceptor.GlobalClientInterceptorRegistry;
import com.google.common.collect.Lists;
import io.grpc.*;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.concurrent.GuardedBy;
import java.sql.Time;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import static java.util.Objects.requireNonNull;

/**
 * 功能概述    This abstract channel factory contains some shared code for other {@link GrpcChannelFactory}s.
 * className:      AbstractChannelFactory
 * package:        com.framework.starter.grpc.client.channelfactory
 * author:         Gavin.Xu
 * date:           2021/6/8
 */
@Slf4j
public abstract class AbstractChannelFactory<T extends ManagedChannelBuilder<T>> implements GrpcChannelFactory {

    @GuardedBy("this")
    private final Map<String, ManagedChannel> channels = new ConcurrentHashMap<>();
    private boolean shutdown = false;
    private GrpcClientProperties properties;
    private GlobalClientInterceptorRegistry globalClientInterceptorRegistry;

    /**
     * 50M
     */
    private static final int MAX_INBOUND_MESSAGE_SIZE = 50 * 1024 * 1024;
    private static final String DEFAULT_LOAD_BALANCE_POLICY = "weighted_round_robin";
    private static final String SERVICE_PREFIX = DiscoveryUtil.SERVICE_PREFIX;


    public AbstractChannelFactory(final GrpcClientProperties properties,
                                  final GlobalClientInterceptorRegistry globalClientInterceptorRegistry
    ) {
        this.properties = requireNonNull(properties, "properties");
        this.globalClientInterceptorRegistry =
                requireNonNull(globalClientInterceptorRegistry, "globalClientInterceptorRegistry");
    }

    @Override
    public Channel createChannel(String name, String version) {
        final Channel channel;
        synchronized (this) {
            if (this.shutdown) {
                throw new IllegalStateException("GrpcChannelFactory is already closed!");
            }
            String serviceKey = String.valueOf(DiscoveryUtil.getKey(SERVICE_PREFIX, name, version, Protocol.GRPC.of()));
            channel = this.channels.computeIfAbsent(serviceKey, this::newManagedChannel);
        }
        final List<ClientInterceptor> interceptors =
                Lists.newArrayList(this.globalClientInterceptorRegistry.getClientInterceptors());
        return ClientInterceptors.interceptForward(channel, interceptors);
    }

    protected abstract T newChannelBuilder(String name);

    protected ManagedChannel newManagedChannel(final String name) {
        final T builder = newChannelBuilder(name);
        configure(builder);
        return builder.build();
    }

    /**
     * Configures the given channel builder. This method can be overwritten to add features that are not yet supported
     * by this library.
     *
     * @param builder The channel builder to configure.
     */
    protected void configure(T builder) {
        /**
         * keepalive
         */
        if (properties.isEnableKeepAlive()) {
            builder.keepAliveTime(properties.getKeepAliveTime().toNanos(), TimeUnit.NANOSECONDS)
                    .keepAliveWithoutCalls(properties.isKeepAliveWithoutCalls())
                    .keepAliveTimeout(properties.getKeepAliveTimeout().toNanos(), TimeUnit.NANOSECONDS);
        }
        /**
         * limits
         */
        builder.maxInboundMessageSize(MAX_INBOUND_MESSAGE_SIZE);

        /**
         * load balance
         */
        builder.defaultLoadBalancingPolicy(properties.getDefaultLoadBalancingPolicy());
        builder.usePlaintext();
    }


    @Override
    public void close() {
        if (this.shutdown) {
            return;
        }
        this.shutdown = true;
        for (final ManagedChannel channel : this.channels.values()) {
            channel.shutdown();
        }
        try {
            final long waitLimit = System.currentTimeMillis() + 60_000; // wait 60 seconds at max
            for (final ManagedChannel channel : this.channels.values()) {
                int i = 0;
                do {
                    log.debug("Awaiting channel shutdown: {} ({}s)", channel, i++);
                } while (System.currentTimeMillis() < waitLimit && !channel.awaitTermination(1, TimeUnit.SECONDS));
            }
        } catch (final InterruptedException e) {
            Thread.currentThread().interrupt();
            log.debug("We got interrupted - Speeding up shutdown process");
        } finally {
            for (final ManagedChannel channel : this.channels.values()) {
                if (!channel.isTerminated()) {
                    log.debug("Channel not terminated yet - force shutdown now: {} ", channel);
                    channel.shutdownNow();
                }
            }
        }
        final int channelCount = this.channels.size();
        this.channels.clear();
        log.debug("GrpcCannelFactory closed (including {} channels)", channelCount);
    }
}
