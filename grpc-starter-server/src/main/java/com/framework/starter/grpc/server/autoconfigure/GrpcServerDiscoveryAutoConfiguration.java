package com.framework.starter.grpc.server.autoconfigure;

import com.framework.starter.discovery.autoconfigure.DiscoveryAutoConfiguration;
import com.framework.starter.discovery.client.DiscoveryClient;
import com.framework.starter.discovery.enums.Protocol;
import com.framework.starter.discovery.register.Registration;
import com.framework.starter.discovery.service.ServiceEntity;
import com.framework.starter.discovery.util.DiscoveryUtil;
import com.framework.starter.grpc.server.config.GrpcServerProperties;
import com.framework.util.json.ObjectMapperUtils;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.List;

/**
 * 功能概述
 * className:      GrpcServerDiscoveryAutoConfiguration
 * package:        com.framework.starter.grpc.server.autoconfigure
 * author:         Gavin.Xu
 * date:           2021/6/13
 */
@Configuration
@AutoConfigureAfter(DiscoveryAutoConfiguration.class)
@Slf4j
public class GrpcServerDiscoveryAutoConfiguration {


    @ConditionalOnMissingBean
    @Bean
    public GrpcServerProperties grpcServerProperties() {
        log.info("try to create bean grpcServerProperties...");
        return new GrpcServerProperties();
    }

    @ConditionalOnMissingBean
    @ConditionalOnBean({DiscoveryClient.class, GrpcServerProperties.class})
    @Bean
    public List<Registration> registration(final List<DiscoveryClient> discoveryClients, final GrpcServerProperties grpcServerProperties) {
        log.info("try to create bean registration...");
        List<Registration> registrationList = Lists.newArrayList();
        for (DiscoveryClient discoveryClient : discoveryClients) {
            int port = grpcServerProperties.getPort();
            String version = grpcServerProperties.getVersion();
            String name = grpcServerProperties.getName();
            int lbWeight = grpcServerProperties.getLbWeight();
            String host = DiscoveryUtil.getHostIp();
            String key = DiscoveryUtil.getKey(DiscoveryUtil.SERVICE_PREFIX, name, version, Protocol.GRPC.of());
            String value = ObjectMapperUtils.toJSON(ServiceEntity.builder()
                    .endPoint(DiscoveryUtil.getHostIp() + ":" + port)
                    .host(host)
                    .port(port)
                    .lbWeight(lbWeight)
                    .env(System.getProperty("env"))
                    .build());
            Registration registration = new Registration(discoveryClient);
            registration.register(key, value);
            registrationList.add(registration);
        }
        return registrationList;
    }

}
