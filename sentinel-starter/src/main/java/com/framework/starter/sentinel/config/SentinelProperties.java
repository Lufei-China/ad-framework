package com.framework.starter.sentinel.config;

import com.framework.starter.sentinel.SentinelDegrade;
import com.framework.starter.sentinel.SentinelFlow;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

/**
 * 功能概述
 * className:      SentinelProperties
 * package:        com.framework.starter.sentinel.config
 * author:         Gavin.Xu
 * date:           2021/6/21
 */
@ConfigurationProperties(prefix = "sentinel")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SentinelProperties {

    private Map<String,SentinelFlow> flowMap;
    private Map<String,SentinelDegrade> degradeMap;

}
