package com.framework.starter.grpc;

import ad.framework.pb.ExampleServiceGrpc;
import ad.framework.pb.Request;
import ad.framework.pb.Response;
import com.framework.starter.grpc.server.service.GrpcService;
import com.framework.starter.sentinel.grpc.interceptors.SentinelGrpcServerFlowInterceptor;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;

@GrpcService(interceptors = {SentinelGrpcServerFlowInterceptor.class})
@Slf4j
public class Example extends ExampleServiceGrpc.ExampleServiceImplBase {

    @Override
    public void test(Request request, StreamObserver<Response> responseObserver) {
        log.info("request id:{}", request.getId());
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        responseObserver.onNext(Response.newBuilder().setResult("ok").build());
        responseObserver.onCompleted();
    }
}
