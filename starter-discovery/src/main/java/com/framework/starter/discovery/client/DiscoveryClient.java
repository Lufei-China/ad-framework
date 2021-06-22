package com.framework.starter.discovery.client;

import com.framework.starter.discovery.service.ServiceEntity;

import java.net.InetSocketAddress;
import java.util.Set;

/**
 * 功能概述
 * className:      Client
 * package:        com.framework.starter.discovery.client
 * author:         Gavin.Xu
 * date:           2021/6/8
 */
public abstract class DiscoveryClient {
    /**
     *
     * @param key
     * @param value
     */
    public abstract void addAndKeep(String key,String value);

    /**
     * get values by key or key prefix
     * @param key
     * @return
     */
    public abstract Set<ServiceEntity> findServices(String key);

    public abstract void watch(String key);
}
