package com.framework.starter.discovery.autoconfigure;

import com.framework.starter.discovery.client.DiscoveryClient;
import com.framework.starter.discovery.client.EtcdDiscoveryClient;
import com.framework.starter.discovery.client.ZookeeperDiscoveryClient;
import com.framework.starter.discovery.config.EtcdProperties;
import com.framework.starter.discovery.config.ZookeeperProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 功能概述
 * className:      DiscoveryAutoConfiguration
 * package:        com.framework.starter.discovery.autoconfigure
 * author:         Gavin.Xu
 * date:           2021/6/8
 */
@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
@Configuration
@ConditionalOnProperty(name = "discovery.enable", havingValue = "true")
@AutoConfigureAfter({EtcdAutoConfiguration.class,ZookeeperAutoConfiguration.class})
@Slf4j
public class DiscoveryAutoConfiguration {

    @Bean
    @ConditionalOnBean(EtcdProperties.class)
    EtcdDiscoveryClient discoveryClient(final EtcdProperties etcdProperties){
        log.debug("try to create bean {}", "DiscoveryClient-etcd");
        return new EtcdDiscoveryClient(etcdProperties);
    }

    @Bean
    @ConditionalOnMissingBean(DiscoveryClient.class)
    @ConditionalOnBean(ZookeeperProperties.class)
    ZookeeperDiscoveryClient discoveryClient(final ZookeeperProperties zookeeperProperties){
        log.debug("try to create bean {}", "DiscoveryClient-zookeeper");
        return new ZookeeperDiscoveryClient(zookeeperProperties);
    }
}
