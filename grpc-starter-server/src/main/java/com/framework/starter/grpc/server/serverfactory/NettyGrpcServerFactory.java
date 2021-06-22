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

import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import com.framework.starter.discovery.util.DiscoveryUtil;
import com.framework.starter.grpc.server.config.GrpcServerProperties;
import com.google.common.net.InetAddresses;

import io.grpc.netty.NettyServerBuilder;
import io.grpc.netty.shaded.io.netty.util.concurrent.DefaultThreadFactory;

import static io.grpc.netty.shaded.io.netty.util.concurrent.DefaultThreadFactory.toPoolName;
import static java.lang.Thread.NORM_PRIORITY;

/**
 * Factory for netty based grpc servers.
 *
 * @author Michael (yidongnan@gmail.com)
 * @since 5/17/16
 */
public class NettyGrpcServerFactory extends AbstractGrpcServerFactory<NettyServerBuilder> {

    /**
     * Creates a new netty server factory with the given properties.
     *
     * @param properties The properties used to configure the server.
     */
    public NettyGrpcServerFactory(final GrpcServerProperties properties) {
        super(properties);
    }

    @Override
    protected NettyServerBuilder newServerBuilder() throws UnknownHostException {
        final String address = DiscoveryUtil.getHostIp();
        final int port = getPort();
        return NettyServerBuilder.forAddress(new InetSocketAddress(InetAddresses.forString(address), port));
    }

    @Override
    // Keep this in sync with ShadedNettyGrpcServerFactory#configureKeepAlive
    protected void configureKeepAlive(final NettyServerBuilder builder) {
        if (this.properties.isEnableKeepAlive()) {
            builder.keepAliveTime(this.properties.getKeepAliveTime().toNanos(), TimeUnit.NANOSECONDS)
                    .keepAliveTimeout(this.properties.getKeepAliveTimeout().toNanos(), TimeUnit.NANOSECONDS);
        }
        /**
         * if your server run as reactor model,use directExecutor
         */
        if (this.properties.isReactor()) {
            builder.directExecutor();
        }
        /**
         * if your server run as not reactor model, and want to use a fixed thread pool
         */
        if (!this.properties.isReactor() && this.properties.getHandleWorkerThreads() > 0) {
            builder.executor(buildExecutor(this.properties.getHandleWorkerThreads()));
        }
        builder.permitKeepAliveTime(this.properties.getPermitKeepAliveTime().toNanos(), TimeUnit.NANOSECONDS)
                .permitKeepAliveWithoutCalls(this.properties.isPermitKeepAliveWithoutCalls());
    }

    private ExecutorService buildExecutor(int workThreads) {
        ThreadFactory factory = new DefaultThreadFactory(toPoolName(NettyGrpcServerFactory.class), false, NORM_PRIORITY,
                new ThreadGroup("grpc-worker-group-" + workThreads));
        return Executors.newFixedThreadPool(workThreads, factory);
    }

}
