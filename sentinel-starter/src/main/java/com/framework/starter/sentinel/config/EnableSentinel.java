package com.framework.starter.sentinel.config;

import com.framework.starter.sentinel.autoconfigure.SentinelAutoConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * 功能概述
 * className:      EnableSentinel
 * package:        com.framework.starter.sentinel.config
 * author:         Gavin.Xu
 * date:           2021/6/21
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import(SentinelAutoConfiguration.class)
@Documented
public @interface EnableSentinel {
}
