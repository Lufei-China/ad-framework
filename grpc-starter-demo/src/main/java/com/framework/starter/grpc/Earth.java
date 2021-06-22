package com.framework.starter.grpc;

import ad.framework.pb.ExampleServiceGrpc;
import ad.framework.pb.Request;
import ad.framework.pb.Response;
import com.framework.starter.grpc.client.GrpcClient;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;


@Service
@EnableScheduling
public class Earth  {

    @GrpcClient(name = "example")
    ExampleServiceGrpc.ExampleServiceBlockingStub exampleServiceStub;

    private Executor executor = Executors.newFixedThreadPool(4);

    @Scheduled(cron = "0/5 * * * * *")
    public void test(){
        for (int i = 0; i <4 ; i++) {
            executor.execute(() ->{
                Response response = exampleServiceStub.test(Request.newBuilder().setId(1).build());
                System.out.println(response.getResult());
            });
        }
    }
}
