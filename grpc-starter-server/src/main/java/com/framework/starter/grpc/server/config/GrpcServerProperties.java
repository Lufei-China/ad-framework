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

package com.framework.starter.grpc.server.config;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.convert.DurationUnit;
import io.grpc.netty.shaded.io.grpc.netty.NettyServerBuilder;
import io.grpc.internal.AbstractServerImplBuilder;
import lombok.Data;

/**
 * The properties for the gRPC server that will be started as part of the application.
 *
 * @author Michael (yidongnan@gmail.com)
 * @since 5/17/16
 */
@Data
@ConfigurationProperties("grpc.server")
public class GrpcServerProperties {
    /**
     * Server version
     */
    private String version = "v1.0";
    /**
     * Server name
     */
    private String name = "default-server-name";
    /**
     * Server port to listen on.
     *
     * @param port The port the server should listen on.
     * @return The port the server will listen on.
     */
    private int port = 9090;

    /**
     * if this server use to build a reactor model project, like spring webflux
     * Default is false
     */
    private boolean reactor = false;

    /**
     * if this server not use reactor model, this param define the Grpc handle worker threads
     *
     * @see AbstractServerImplBuilder#executor(Executor executor)
     * @see AbstractServerImplBuilder#directExecutor()
     */
    private int handleWorkerThreads = 100;
    /**
     * set this server weight,number can from 1-10
     */
    private int lbWeight = 10;

    /**
     * The name of the in-process server. If not set, then the in process server won't be started.
     *
     * @param inProcessName The name of the in-process server.
     * @return The name of the in-process server or null if isn't configured.
     */
    private String inProcessName;

    /**
     * The time to wait for the server to gracefully shutdown (completing all requests after the server started to
     * shutdown). If set to a negative value, the server waits forever. If set to {@code 0} the server will force
     * shutdown immediately. Defaults to {@code 30s}.
     *
     * @param gracefullShutdownTimeout The time to wait for a graceful shutdown.
     * @return The time to wait for a graceful shutdown.
     */
    @DurationUnit(ChronoUnit.SECONDS)
    private Duration shutdownGracePeriod = Duration.of(30, ChronoUnit.SECONDS);

    /**
     * Setting to enable keepAlive. Default to {@code false}.
     *
     * @param enableKeepAlive Whether keep alive should be enabled.
     * @return True, if keep alive should be enabled. False otherwise.
     */
    private boolean enableKeepAlive = true;

    /**
     * The default delay before we send a keepAlives. Defaults to {@code 60s}. Default unit {@link ChronoUnit#SECONDS
     * SECONDS}.
     *
     * @param keepAliveTime The new default delay before sending keepAlives.
     * @return The default delay before sending keepAlives.
     * @see #setEnableKeepAlive(boolean)
     */
    @DurationUnit(ChronoUnit.MINUTES)
    private Duration keepAliveTime = Duration.of(5, ChronoUnit.MINUTES);

    /**
     * The default timeout for a keepAlives ping request. Defaults to {@code 20s}. Default unit
     * {@link ChronoUnit#SECONDS SECONDS}.
     *
     * @param keepAliveTimeout Sets the default timeout for a keepAlives ping request.
     * @return The default timeout for a keepAlives ping request.
     * @see #setEnableKeepAlive(boolean)
     * @see NettyServerBuilder#keepAliveTimeout(long, TimeUnit)
     */
    @DurationUnit(ChronoUnit.SECONDS)
    private Duration keepAliveTimeout = Duration.of(2, ChronoUnit.MINUTES);

    /**
     * Specify the most aggressive keep-alive time clients are permitted to configure. Defaults to {@code 5min}. Default
     * unit {@link ChronoUnit#SECONDS SECONDS}.
     *
     * @param permitKeepAliveTime The most aggressive keep-alive time clients are permitted to configure.
     * @return The most aggressive keep-alive time clients are permitted to configure.
     * @see NettyServerBuilder#permitKeepAliveTime(long, TimeUnit)
     */
    @DurationUnit(ChronoUnit.MINUTES)
    private Duration permitKeepAliveTime = Duration.of(10, ChronoUnit.MINUTES);

    /**
     * Whether clients are allowed to send keep-alive HTTP/2 PINGs even if there are no outstanding RPCs on the
     * connection. Defaults to {@code false}.
     *
     * @param permitKeepAliveWithoutCalls Whether to allow clients to send keep-alive requests without calls.
     * @return True, if clients are allowed to send keep-alive requests without calls. False otherwise.
     * @see NettyServerBuilder#permitKeepAliveWithoutCalls(boolean)
     */
    private boolean permitKeepAliveWithoutCalls = true;

    public int getLbWeight() {
        if (this.lbWeight > 10){
            return 10;
        }
        if (this.lbWeight < 1){
            return 1;
        }
        return lbWeight;
    }
}
