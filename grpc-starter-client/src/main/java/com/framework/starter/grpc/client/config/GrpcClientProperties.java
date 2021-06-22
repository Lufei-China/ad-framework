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

package com.framework.starter.grpc.client.config;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.convert.DurationUnit;
import io.grpc.LoadBalancerRegistry;
import io.grpc.ManagedChannelBuilder;
import io.grpc.netty.shaded.io.grpc.netty.NettyChannelBuilder;
import io.grpc.netty.shaded.io.grpc.netty.NettyServerBuilder;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * The channel properties for a single named gRPC channel or service reference.
 *
 * @author Michael (yidongnan@gmail.com)
 * @author Daniel Theuke (daniel.theuke@heuboe.de)
 * @since 5/17/16
 */
@ToString
@EqualsAndHashCode
@ConfigurationProperties(prefix = "grpc.client")
public class GrpcClientProperties {

    @DurationUnit(ChronoUnit.MILLIS)
    private Duration timeout ;
    private static final Duration DEFAULT_TIMEOUT = Duration.of(5000, ChronoUnit.MILLIS);

    /**
     * get client timeout, will use in client interceptor
     * @return
     */
    public Duration getTimeout() {
        return this.timeout == null ? DEFAULT_TIMEOUT
                : this.timeout;
    }

    public void setTimeout(Duration timeout) {
        this.timeout = timeout;
    }
    // --------------------------------------------------
    // defaultLoadBalancingPolicy
    // --------------------------------------------------

    private String defaultLoadBalancingPolicy;
    private static final String DEFAULT_DEFAULT_LOAD_BALANCING_POLICY = "round_robin";

    /**
     * Gets the default load balancing policy this channel should use.
     *
     * @return The default load balancing policy.
     * @see ManagedChannelBuilder#defaultLoadBalancingPolicy(String)
     */
    public String getDefaultLoadBalancingPolicy() {
        return this.defaultLoadBalancingPolicy == null ? DEFAULT_DEFAULT_LOAD_BALANCING_POLICY
                : this.defaultLoadBalancingPolicy;
    }

    /**
     * Sets the default load balancing policy for this channel. This config might be overwritten by the service config
     * received from the target address. The names have to be resolvable from the {@link LoadBalancerRegistry}. By
     * default this the {@code round_robin} policy. Please note that this policy is different from the normal grpc-java
     * default policy {@code pick_first}.
     *
     * @param defaultLoadBalancingPolicy The default load balancing policy to use or null to use the fallback.
     */
    public void setDefaultLoadBalancingPolicy(final String defaultLoadBalancingPolicy) {
        this.defaultLoadBalancingPolicy = defaultLoadBalancingPolicy;
    }

    // --------------------------------------------------
    // KeepAlive
    // --------------------------------------------------

    private Boolean enableKeepAlive;
    private static final boolean DEFAULT_ENABLE_KEEP_ALIVE = false;

    /**
     * Gets whether keepAlive is enabled.
     *
     * @return True, if keep alive should be enabled. False otherwise.
     * @see #setEnableKeepAlive(Boolean)
     */
    public boolean isEnableKeepAlive() {
        return this.enableKeepAlive == null ? DEFAULT_ENABLE_KEEP_ALIVE : this.enableKeepAlive;
    }

    /**
     * Sets whether keepAlive should be enabled. Defaults to false.
     *
     * @param enableKeepAlive True, to enable. False, to disable. Null, to use the fallback.
     */
    public void setEnableKeepAlive(final Boolean enableKeepAlive) {
        this.enableKeepAlive = enableKeepAlive;
    }

    // --------------------------------------------------

    @DurationUnit(ChronoUnit.SECONDS)
    private Duration keepAliveTime;
    private static final Duration DEFAULT_KEEP_ALIVE_TIME = Duration.of(300, ChronoUnit.SECONDS);

    /**
     * Gets the default delay before we send a keepAlive.
     *
     * @return The default delay before sending keepAlives.
     * @see #setKeepAliveTime(Duration)
     */
    public Duration getKeepAliveTime() {
        return this.keepAliveTime == null ? DEFAULT_KEEP_ALIVE_TIME : this.keepAliveTime;
    }

    /**
     * The default delay before we send a keepAlives. Defaults to {@code 300s}. Default unit {@link ChronoUnit#SECONDS
     * SECONDS}. Please note that shorter intervals increase the network burden for the server.
     *
     * @param keepAliveTime The new default delay before sending keepAlives, or null to use the fallback.
     * @see #setEnableKeepAlive(Boolean)
     * @see NettyServerBuilder#keepAliveTime(long, TimeUnit)
     */
    public void setKeepAliveTime(final Duration keepAliveTime) {
        this.keepAliveTime = keepAliveTime;
    }

    // --------------------------------------------------

    @DurationUnit(ChronoUnit.SECONDS)
    private Duration keepAliveTimeout;
    private static final Duration DEFAULT_KEEP_ALIVE_TIMEOUT = Duration.of(5, ChronoUnit.SECONDS);

    /**
     * The default timeout for a keepAlives ping request.
     *
     * @return The default timeout for a keepAlives ping request.
     * @see #setKeepAliveTimeout(Duration)
     */
    public Duration getKeepAliveTimeout() {
        return this.keepAliveTimeout == null ? DEFAULT_KEEP_ALIVE_TIMEOUT : this.keepAliveTimeout;
    }

    /**
     * The default timeout for a keepAlives ping request. Defaults to {@code 5s}. Default unit
     * {@link ChronoUnit#SECONDS SECONDS}.
     *
     * @param keepAliveTimeout The default timeout for a keepAlives ping request.
     * @see #setEnableKeepAlive(Boolean)
     * @see NettyServerBuilder#keepAliveTimeout(long, TimeUnit)
     */
    public void setKeepAliveTimeout(final Duration keepAliveTimeout) {
        this.keepAliveTimeout = keepAliveTimeout;
    }

    // --------------------------------------------------

    private Boolean keepAliveWithoutCalls;
    private static final boolean DEFAULT_KEEP_ALIVE_WITHOUT_CALLS = false;

    /**
     * Gets whether keepAlive will be performed when there are no outstanding RPC on a connection.
     *
     * @return True, if keepAlives should be performed even when there are no RPCs. False otherwise.
     * @see #setKeepAliveWithoutCalls(Boolean)
     */
    public boolean isKeepAliveWithoutCalls() {
        return this.keepAliveWithoutCalls == null ? DEFAULT_KEEP_ALIVE_WITHOUT_CALLS : this.keepAliveWithoutCalls;
    }

    /**
     * Sets whether keepAlive will be performed when there are no outstanding RPC on a connection. Defaults to
     * {@code false}.
     *
     * @param keepAliveWithoutCalls whether keepAlive will be performed when there are no outstanding RPC on a
     *                              connection.
     * @see #setEnableKeepAlive(Boolean)
     * @see NettyChannelBuilder#keepAliveWithoutCalls(boolean)
     */
    public void setKeepAliveWithoutCalls(final Boolean keepAliveWithoutCalls) {
        this.keepAliveWithoutCalls = keepAliveWithoutCalls;
    }

    // --------------------------------------------------

    @DurationUnit(ChronoUnit.SECONDS)
    private Duration shutdownGracePeriod;
    private static final Duration DEFAULT_SHUTDOWN_GRACE_PERIOD = Duration.ofSeconds(30);

    /**
     * Gets the time to wait for the channel to gracefully shutdown. If set to a negative value, the channel waits
     * forever. If set to {@code 0} the channel will force shutdown immediately. Defaults to {@code 30s}.
     *
     * @return The time to wait for a graceful shutdown.
     */
    public Duration getShutdownGracePeriod() {
        return this.shutdownGracePeriod == null ? DEFAULT_SHUTDOWN_GRACE_PERIOD : this.shutdownGracePeriod;
    }

    /**
     * Sets the time to wait for the channel to gracefully shutdown (completing all requests). If set to a negative
     * value, the channel waits forever. If set to {@code 0} the channel will force shutdown immediately. Defaults to
     * {@code 30s}.
     *
     * @param shutdownGracePeriod The time to wait for a graceful shutdown.
     */
    public void setShutdownGracePeriod(final Duration shutdownGracePeriod) {
        this.shutdownGracePeriod = shutdownGracePeriod;
    }
}
