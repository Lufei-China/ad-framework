package com.framework.starter.grpc.client.interceptor;

import com.framework.starter.grpc.client.config.GrpcClientProperties;
import io.grpc.*;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

/**
 * 功能概述
 * className:      GrpcTimeoutClientInterceptor
 * package:        com.framework.starter.grpc.client.interceptor
 * author:         Gavin.Xu
 * date:           2021/6/16
 */
@GrpcGlobalClientInterceptor
public class GrpcTimeoutClientInterceptor implements ClientInterceptor {

    private GrpcClientProperties grpcClientProperties;

    public GrpcTimeoutClientInterceptor(GrpcClientProperties grpcClientProperties) {
        this.grpcClientProperties = grpcClientProperties;
    }

    @Override
    public <ReqT, RespT> ClientCall<ReqT, RespT> interceptCall(MethodDescriptor<ReqT, RespT> method, CallOptions callOptions, Channel next) {
        Duration timeout = grpcClientProperties.getTimeout();
        callOptions = callOptions.withDeadlineAfter(timeout.toNanos(), TimeUnit.NANOSECONDS);
        return next.newCall(method,callOptions);
    }
}
