package com.framework.starter.grpc.server.autoconfigure;

import com.framework.starter.grpc.server.config.GrpcServerProperties;
import com.framework.starter.grpc.server.serverfactory.GrpcServerFactory;
import com.framework.starter.grpc.server.serverfactory.GrpcServerLifecycle;
import com.framework.starter.grpc.server.serverfactory.NettyGrpcServerFactory;
import com.framework.starter.grpc.server.service.AnnotationGrpcServiceDiscoverer;
import com.framework.starter.grpc.server.service.GrpcServiceDefinition;
import com.framework.starter.grpc.server.service.GrpcServiceDiscoverer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 功能概述
 * className:      GrpcServerAutoConfiguration
 * package:        com.framework.starter.grpc.server.autoconfigure
 * author:         Gavin.Xu
 * date:           2021/6/10
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(name = "grpc.server.enable", havingValue = "true")
@EnableConfigurationProperties
@AutoConfigureAfter(GrpcServerDiscoveryAutoConfiguration.class)
@Slf4j
public class GrpcServerAutoConfiguration {

    @ConditionalOnMissingBean
    @Bean
    public GrpcServiceDiscoverer defaultGrpcServiceDiscoverer() {
        log.info("try to create bean grpcServiceDiscoverer...");
        return new AnnotationGrpcServiceDiscoverer();
    }

    @ConditionalOnMissingBean
    @Bean
    public GrpcServerProperties grpcServerProperties(){
        return new GrpcServerProperties();
    }

    @ConditionalOnMissingBean(GrpcServerFactory.class)
    @ConditionalOnClass(name = {"io.netty.channel.Channel", "io.grpc.netty.NettyServerBuilder"})
    @Bean
    public NettyGrpcServerFactory nettyGrpcServerFactory(
            final GrpcServerProperties properties,
            final GrpcServiceDiscoverer serviceDiscoverer) {
        log.info("try to create bean nettyGrpcServerFactory...");
        final NettyGrpcServerFactory factory = new NettyGrpcServerFactory(properties);
        for (final GrpcServiceDefinition service : serviceDiscoverer.findGrpcServices()) {
            factory.addService(service);
        }
        return factory;
    }

    /**
     * The server lifecycle bean for netty based server.
     *
     * @param factory The factory used to create the lifecycle.
     * @param properties The server properties to use.
     * @return The inter-process server lifecycle bean.
     */
    @ConditionalOnBean(NettyGrpcServerFactory.class)
    @Bean
    public GrpcServerLifecycle nettyGrpcServerLifecycle(
            final NettyGrpcServerFactory factory,
            final GrpcServerProperties properties) {
        log.info("try to create bean nettyGrpcServerLifecycle...");
        return new GrpcServerLifecycle(factory, properties.getShutdownGracePeriod());
    }

    @ConditionalOnMissingBean
    @ConditionalOnBean(GrpcServerFactory.class)
    @Bean
    public GrpcServerLifecycle grpcServerLifecycle(
            final GrpcServerFactory factory,
            final GrpcServerProperties properties) {
        log.info("try to create bean grpcServerLifecycle...");
        return new GrpcServerLifecycle(factory, properties.getShutdownGracePeriod());
    }


}
