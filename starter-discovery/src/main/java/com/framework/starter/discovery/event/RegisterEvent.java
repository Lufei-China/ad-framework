package com.framework.starter.discovery.event;

import org.springframework.context.ApplicationEvent;

/**
 * 功能概述
 * className:      RegisterEvent
 * package:        com.framework.starter.discovery.event
 * author:         Gavin.Xu
 * date:           2021/6/8
 */
public class RegisterEvent extends ApplicationEvent {
    /**
     * Create a new {@code ApplicationEvent}.
     *
     * @param source the object on which the event initially occurred or with
     *               which the event is associated (never {@code null})
     */
    public RegisterEvent(Object source) {
        super(source);
    }
}
