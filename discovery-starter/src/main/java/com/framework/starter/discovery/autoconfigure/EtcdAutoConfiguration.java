package com.framework.starter.discovery.autoconfigure;

import com.framework.starter.discovery.config.EtcdProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 功能概述
 * className:      PropertiesAutoConfiguration
 * package:        com.framework.starter.discovery.autoconfigure
 * author:         Gavin.Xu
 * date:           2021/6/8
 */
@Configuration
@ConditionalOnProperty(prefix = "discovery", name = "client", havingValue = "etcd")
@Slf4j
public class EtcdAutoConfiguration {

    @Bean
    EtcdProperties etcdProperties(){
        log.debug("try to create bean {}", "EtcdProperties");
        return new EtcdProperties();
    }
}
