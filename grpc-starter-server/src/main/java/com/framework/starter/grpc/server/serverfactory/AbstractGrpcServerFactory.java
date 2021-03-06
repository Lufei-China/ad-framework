/*
 * Copyright (c) 2016-2021 Michael Zhang <yidongnan@gmail.com>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without limitation the
 * rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the
 * Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
 * OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.framework.starter.grpc.server.serverfactory;

import static java.util.Objects.requireNonNull;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import com.framework.starter.grpc.server.config.GrpcServerProperties;
import com.framework.starter.grpc.server.service.GrpcServiceDefinition;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.unit.DataSize;

import com.google.common.collect.Lists;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import lombok.extern.slf4j.Slf4j;

/**
 * Abstract factory for grpc servers.
 *
 * @param <T> The type of builder used by this factory.
 * @author Michael (yidongnan@gmail.com)
 * @author Daniel Theuke (daniel.theuke@heuboe.de)
 * @since 5/17/16
 */
@Slf4j
public abstract class AbstractGrpcServerFactory<T extends ServerBuilder<T>> implements GrpcServerFactory {

    private final List<GrpcServiceDefinition> serviceList = Lists.newLinkedList();

    protected final GrpcServerProperties properties;

    /**
     * Creates a new server factory with the given properties.
     *
     * @param properties The properties used to configure the server.
     */
    protected AbstractGrpcServerFactory(final GrpcServerProperties properties) {
        this.properties = requireNonNull(properties, "properties");
    }

    @Override
    public Server createServer() throws UnknownHostException {
        final T builder = newServerBuilder();
        configure(builder);
        return builder.build();
    }

    /**
     * Creates a new server builder.
     *
     * @return The newly created server builder.
     */
    protected abstract T newServerBuilder() throws UnknownHostException;

    /**
     * Configures the given server builder.
     *
     * @param builder The server builder to configure.
     */
    protected void configure(final T builder) {
        configureServices(builder);
        configureKeepAlive(builder);
    }

    /**
     * Configures the services that should be served by the server.
     *
     * @param builder The server builder to configure.
     */
    protected void configureServices(final T builder) {
        final Set<String> serviceNames = new LinkedHashSet<>();

        for (final GrpcServiceDefinition service : this.serviceList) {
            final String serviceName = service.getDefinition().getServiceDescriptor().getName();
            if (!serviceNames.add(serviceName)) {
                throw new IllegalStateException("Found duplicate service implementation: " + serviceName);
            }
            log.info("Start gRPC service: " + serviceName + ", class: "
                    + service.getBeanClazz().getName());
            builder.addService(service.getDefinition());
        }
    }

    /**
     * Configures the keep alive options that should be used by the server.
     *
     * @param builder The server builder to configure.
     */
    protected void configureKeepAlive(final T builder) {
        if (this.properties.isEnableKeepAlive()) {
            throw new IllegalStateException("KeepAlive is enabled but this implementation does not support keepAlive!");
        }
    }

    @Override
    public int getPort() {
        return this.properties.getPort();
    }

    @Override
    public void addService(final GrpcServiceDefinition service) {
        this.serviceList.add(service);
    }

}
