package com.framework.starter.grpc.client.channelfactory;

import com.framework.starter.grpc.client.config.GrpcClientProperties;
import com.framework.starter.grpc.client.interceptor.GlobalClientInterceptorRegistry;
import io.grpc.netty.NettyChannelBuilder;

/**
 * 功能概述 This channel factory creates and manages netty based {@link GrpcChannelFactory}s.
 * className:      NettyChannelFactory
 * package:        com.framework.starter.grpc.client.channelfactory
 * author:         Gavin.Xu
 * date:           2021/6/8
 */
public class NettyChannelFactory extends AbstractChannelFactory<NettyChannelBuilder> {


    /**
     * Creates a new GrpcChannelFactory for netty with the given options.
     *
     * @param properties The properties for the channels to create.
     * @param globalClientInterceptorRegistry The interceptor registry to use.
     */
    public NettyChannelFactory(GrpcClientProperties properties, GlobalClientInterceptorRegistry globalClientInterceptorRegistry) {
        super(properties, globalClientInterceptorRegistry);
    }

    @Override
    protected NettyChannelBuilder newChannelBuilder(final String name) {
        return NettyChannelBuilder.forTarget(name);
    }
}
