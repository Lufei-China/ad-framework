package com.framework.starter.grpc.client;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface GrpcClient {
    /**
     * service name
     * @return
     */
    String name();

    /**
     * service version
     *
     * @return
     */
    String version() default "v1.0";

}
