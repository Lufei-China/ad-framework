package com.framework.starter.discovery.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

/**
 * 功能概述
 * className:      EtcdProperties
 * package:        com.framework.starter.discovery.config
 * author:         Gavin.Xu
 * date:           2021/6/8
 */
@ConfigurationProperties("etcd")
@Data
public class EtcdProperties {
    /**
     * etcd endpoints, spe by ','
     */
    private String endpoints;
    /**
     * etcd key ttl
     */
    private Duration leaseTime = Duration.ofSeconds(10);
    /**
     * etcd io timeout
     */
    private Duration timeout = Duration.ofSeconds(10);
    /**
     * schedule lease key time interval
     */
    private Duration scheduleLeaseTime  = Duration.ofSeconds(9);
}
