package com.framework.starter.grpc.client.nameresolver;

import com.framework.starter.discovery.client.DiscoveryClient;
import com.framework.starter.discovery.event.RegisterEvent;
import com.google.common.collect.Sets;
import io.grpc.NameResolver;
import io.grpc.NameResolverProvider;
import io.netty.util.internal.SuppressJava6Requirement;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;

import java.net.URI;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 功能概述
 * className:      EtcdNameResolverProvider
 * package:        com.framework.starter.grpc.client.nameresolver
 * author:         Gavin.Xu
 * date:           2021/6/8
 */
@Slf4j
public class DiscoveryNameResolverProvider extends NameResolverProvider {

    private final Set<DiscoveryNameResolver> discoveryNameResolver = Sets.newConcurrentHashSet();
    public static final String DISCOVERY_SCHEME = "/services";
    private final DiscoveryClient client;

    public DiscoveryNameResolverProvider(final DiscoveryClient client) {
        this.client = client;
    }

    @Override
    public NameResolver newNameResolver(URI targetUri, NameResolver.Args args) {
        if (targetUri.getPath().startsWith(DISCOVERY_SCHEME)) {
            final String key = targetUri.getPath();
            if (key == null || key.length() <= 1) {
                throw new IllegalArgumentException("key can't be blank!");
            }
            log.debug("start to find services, key {}", key);
            final AtomicReference<DiscoveryNameResolver> reference = new AtomicReference<>();
            final DiscoveryNameResolver discoveryNameResolver =
                    new DiscoveryNameResolver(key, this.client, args);
            reference.set(discoveryNameResolver);
            this.discoveryNameResolver.add(discoveryNameResolver);
            return discoveryNameResolver;
        }
        return null;
    }

    /**
     * Triggers a refresh of the registered name resolvers.
     *
     * @param event The event that triggered the update.
     */
    @EventListener(RegisterEvent.class)
    private void listener(RegisterEvent event) {
        log.debug("listener happen thread:{}", Thread.currentThread().getName());
        for (DiscoveryNameResolver nameResolver : discoveryNameResolver) {
            nameResolver.resolvingAddress();
        }
    }

    @Override
    protected boolean isAvailable() {
        return true;
    }

    @Override
    protected int priority() {
        return 6;
    }

    @Override
    public String getDefaultScheme() {
        return DISCOVERY_SCHEME;
    }
}
