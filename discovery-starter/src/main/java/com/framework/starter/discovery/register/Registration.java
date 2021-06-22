package com.framework.starter.discovery.register;

import com.framework.starter.discovery.client.DiscoveryClient;

/**
 * 功能概述
 * className:      Registration
 * package:        com.framework.starter.discovery.register
 * author:         Gavin.Xu
 * date:           2021/6/13
 */
public class Registration implements Register{

    private DiscoveryClient discoveryClient;

    public Registration(DiscoveryClient discoveryClient){
        this.discoveryClient = discoveryClient;
    }

    @Override
    public void register(String key, String endPoint) {
        this.discoveryClient.addAndKeep(key,endPoint);
    }
}
