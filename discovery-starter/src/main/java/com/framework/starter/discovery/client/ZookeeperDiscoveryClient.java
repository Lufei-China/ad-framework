package com.framework.starter.discovery.client;

import com.framework.starter.discovery.config.ZookeeperProperties;
import com.framework.starter.discovery.service.ServiceEntity;

import java.util.Set;

/**
 * 功能概述
 * className:      ZookeeperClient
 * package:        com.framework.starter.discovery.client
 * author:         Gavin.Xu
 * date:           2021/6/8
 */
public class ZookeeperDiscoveryClient extends DiscoveryClient{

    private ZookeeperProperties zookeeperProperties;

    public ZookeeperDiscoveryClient(ZookeeperProperties zookeeperProperties) {
        this.zookeeperProperties = zookeeperProperties;
    }

    @Override
    public void addAndKeep(String key, String address) {

    }

    @Override
    public Set<ServiceEntity> findServices(String key) {
        return null;
    }

    @Override
    public void watch(String key) {

    }
}
