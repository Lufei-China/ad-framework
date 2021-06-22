package com.framework.starter.grpc.client.autoconfigure;

import com.framework.starter.discovery.autoconfigure.DiscoveryAutoConfiguration;
import com.framework.starter.discovery.client.DiscoveryClient;
import com.framework.starter.discovery.client.EtcdDiscoveryClient;
import com.framework.starter.discovery.client.ZookeeperDiscoveryClient;
import com.framework.starter.discovery.util.DiscoveryUtil;
import com.framework.starter.grpc.client.channelfactory.GrpcChannelFactory;
import com.framework.starter.grpc.client.channelfactory.NettyChannelFactory;
import com.framework.starter.grpc.client.config.GrpcClientProperties;
import com.framework.starter.grpc.client.inject.GrpcClientBeanPostProcessor;
import com.framework.starter.grpc.client.interceptor.AnnotationGlobalClientInterceptorConfigurer;
import com.framework.starter.grpc.client.interceptor.GlobalClientInterceptorRegistry;
import com.framework.starter.grpc.client.interceptor.GrpcTimeoutClientInterceptor;
import com.framework.starter.grpc.client.nameresolver.DiscoveryNameResolverProvider;
import com.framework.starter.grpc.client.nameresolver.NameResolverRegistration;
import io.grpc.NameResolverProvider;
import io.grpc.NameResolverRegistry;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

import java.util.List;

/**
 * 功能概述
 * className:      GrpcClientAutoConfiguration
 * package:        com.framework.starter.grpc.client.autoconfigure
 * author:         Gavin.Xu
 * date:           2021/6/8
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(name = "grpc.client.enable", havingValue = "true")
@AutoConfigureAfter(DiscoveryAutoConfiguration.class)
@EnableConfigurationProperties
@Slf4j
public class GrpcClientAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    GrpcClientBeanPostProcessor grpcClientBeanPostProcessor(final ApplicationContext applicationContext) {
        log.debug("try to create bean {}", "GrpcClientBeanPostProcessor");
        return new GrpcClientBeanPostProcessor(applicationContext);
    }

    @Bean
    GrpcClientProperties grpcClientProperties() {
        return new GrpcClientProperties();
    }

//    @Bean
//    GrpcTimeoutClientInterceptor grpcTimeoutClientInterceptor(final GrpcClientProperties grpcClientProperties) {
//        return new GrpcTimeoutClientInterceptor(grpcClientProperties);
//    }

    @ConditionalOnMissingBean
    @Bean
    GlobalClientInterceptorRegistry globalClientInterceptorRegistry(final ApplicationContext applicationContext) {
        return new GlobalClientInterceptorRegistry(applicationContext);
    }

    @Bean
    @Lazy
    AnnotationGlobalClientInterceptorConfigurer annotationGlobalClientInterceptorConfigurer(
            final ApplicationContext applicationContext) {
        return new AnnotationGlobalClientInterceptorConfigurer(applicationContext);
    }

    @Bean
    @ConditionalOnBean(DiscoveryClient.class)
    NameResolverProvider nameResolverProvider(final DiscoveryClient discoveryClient) {
        log.debug("try to create bean {}", "NameResolverProvider");
        return new DiscoveryNameResolverProvider(discoveryClient);
    }

    @Bean
    @ConditionalOnMissingBean
    NameResolverRegistration nameResolverRegistration(@Autowired(required = false) final List<NameResolverProvider> nameResolverProviders) {
        log.debug("try to create bean {}", "NameResolverRegistration");
        final NameResolverRegistration nameResolverRegistration = new NameResolverRegistration(nameResolverProviders);
        nameResolverRegistration.register(NameResolverRegistry.getDefaultRegistry());
        return nameResolverRegistration;
    }


    @Bean
    @ConditionalOnMissingBean
    GrpcChannelFactory grpcChannelFactory(final GrpcClientProperties grpcClientProperties, final GlobalClientInterceptorRegistry globalClientInterceptorRegistry) {
        log.debug("try to create bean {}", "GrpcChannelFactory");
        return new NettyChannelFactory(grpcClientProperties, globalClientInterceptorRegistry);
    }

}
