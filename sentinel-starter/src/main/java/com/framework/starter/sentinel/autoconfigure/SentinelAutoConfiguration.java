package com.framework.starter.sentinel.autoconfigure;

import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRuleManager;
import com.framework.starter.sentinel.SentinelFlow;
import com.framework.starter.sentinel.config.SentinelProperties;
import com.framework.starter.sentinel.grpc.interceptors.SentinelGrpcServerFlowInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 功能概述
 * className:      SentinelAutoConfiguration
 * package:        com.framework.starter.sentinel.autoconfigure
 * author:         Gavin.Xu
 * date:           2021/6/21
 */
@Slf4j
public class SentinelAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    SentinelProperties sentinelProperties(){
        return new SentinelProperties();
    }

    @Bean
    SentinelGrpcServerFlowInterceptor sentinelGrpcServerFlowInterceptor(final SentinelProperties sentinelProperties){
        return new SentinelGrpcServerFlowInterceptor(sentinelProperties.getFlowMap());
    }

    @Bean
    public Object initSentinelFlow(SentinelProperties sentinelProperties){

        if (!CollectionUtils.isEmpty(sentinelProperties.getFlowMap())){
            log.debug("init sentinel flow config...");
            Map<String, SentinelFlow> flowMap = sentinelProperties.getFlowMap();
            List<FlowRule> rules = new ArrayList<>();
            for (String resourceName : flowMap.keySet()) {
                FlowRule flowRule = new FlowRule();
                flowRule.setResource(resourceName);
                flowRule.setCount(flowMap.get(resourceName).getCount());
                flowRule.setGrade(flowMap.get(resourceName).getGrade());
                rules.add(flowRule);
            }
            FlowRuleManager.loadRules(rules);
        }

        if (!CollectionUtils.isEmpty(sentinelProperties.getDegradeMap())){
            log.debug("init sentinel degrade config...");
        }
        return new Object();
    }

}
