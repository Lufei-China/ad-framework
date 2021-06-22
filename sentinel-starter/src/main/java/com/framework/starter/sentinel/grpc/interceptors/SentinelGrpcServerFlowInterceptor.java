package com.framework.starter.sentinel.grpc.interceptors;

import com.alibaba.csp.sentinel.Entry;
import com.alibaba.csp.sentinel.EntryType;
import com.alibaba.csp.sentinel.SphU;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.framework.starter.sentinel.SentinelFlow;
import io.grpc.*;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 功能概述
 * className:      SentinelGrpcServerFlowInterceptor
 * package:        com.framework.starter.sentinel.grpc.interceptors
 * author:         Gavin.Xu
 * date:           2021/6/21
 */
@Slf4j
public class SentinelGrpcServerFlowInterceptor implements ServerInterceptor {

    private static final Status FLOW_CONTROL_BLOCK = Status.UNAVAILABLE.withDescription(
            "Flow control limit exceeded (server side)");

    private final Map<String, SentinelFlow> flowMap;

    public SentinelGrpcServerFlowInterceptor() {
        throw new IllegalArgumentException("SentinelGrpcServerFlowInterceptor init need add annotation @EnableSentinel...");
    }

    public SentinelGrpcServerFlowInterceptor(Map<String, SentinelFlow> flowMap) {
        this.flowMap = flowMap;
    }

    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(ServerCall<ReqT, RespT> call, Metadata headers, ServerCallHandler<ReqT, RespT> next) {
        String resourceName = call.getMethodDescriptor().getServiceName();
        if (isEnable(flowMap, resourceName)) {
            Entry entry = null;
            try {
                entry = SphU.asyncEntry(resourceName, EntryType.IN);
                return next.startCall(new ForwardingServerCall.SimpleForwardingServerCall<ReqT, RespT>(call) {
                }, headers);
            } catch (BlockException e) {
                log.debug("SentinelGrpcServerFlowInterceptor flow block:{}", resourceName);
                call.close(FLOW_CONTROL_BLOCK, new Metadata());
                return new ServerCall.Listener<ReqT>() {
                };
            } finally {
                if (entry != null) {
                    entry.exit();
                }
            }
            /*Entry entry = null;
            try {
                entry = SphU.asyncEntry(resourceName, EntryType.IN);
                final AtomicReference<Entry> atomicReferenceEntry = new AtomicReference<>(entry);
                // Allow access, forward the call.
                return new ForwardingServerCallListener.SimpleForwardingServerCallListener<ReqT>(
                        next.startCall(
                                new ForwardingServerCall.SimpleForwardingServerCall<ReqT, RespT>(call) {
                                    @Override
                                    public void close(Status status, Metadata trailers) {
                                        Entry entry = atomicReferenceEntry.get();
                                        if (entry != null) {
                                            //entry exit when the call be closed
                                            entry.exit();
                                        }
                                        super.close(status, trailers);
                                    }
                                }, headers)) {
                    *//**
                     * If call was canceled, onCancel will be called. and the close will not be called
                     * so the server is encouraged to abort processing to save resources by onCancel
                     * @see ServerCall.Listener#onCancel()
                     *//*
                    @Override
                    public void onCancel() {
                        Entry entry = atomicReferenceEntry.get();
                        if (entry != null) {
                            entry.exit();
                            atomicReferenceEntry.set(null);
                        }
                        super.onCancel();
                    }
                };
            } catch (BlockException e) {
                call.close(FLOW_CONTROL_BLOCK, new Metadata());
                return new ServerCall.Listener<ReqT>() {
                };
            } catch (RuntimeException e) {
                // Catch the RuntimeException startCall throws, entry is guaranteed to exit.
                if (entry != null) {
                    entry.exit();
                }
                throw e;
            }*/
        }
        return next.startCall(new ForwardingServerCall.SimpleForwardingServerCall<ReqT, RespT>(call) {
        }, headers);
    }

    private boolean isEnable(Map<String, SentinelFlow> flowMap, String resourceName) {

        return flowMap.containsKey(resourceName);
    }
}
