package com.framework.starter.discovery.service;

import lombok.*;


/**
 * 功能概述
 * className:      ServiceEntity
 * package:        com.framework.starter.discovery.service
 * author:         Gavin.Xu
 * date:           2021/6/8
 */
@Data
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ServiceEntity {

    private String endPoint;

    private String host;

    private int port;

    private int lbWeight = 10;

    private String env;
}
