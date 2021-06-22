package com.framework.starter;

import com.framework.starter.sentinel.config.EnableSentinel;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 功能概述
 * className:      Application
 * package:        com.framework.starter
 * author:         Gavin.Xu
 * date:           2021/6/8
 */
@SpringBootApplication
@EnableSentinel
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
