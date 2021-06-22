package com.framework.starter.sentinel;

import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRule;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 功能概述
 * className:      SentinelDegrade
 * package:        com.framework.starter.sentinel
 * author:         Gavin.Xu
 * date:           2021/6/21
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SentinelDegrade extends DegradeRule {
    private String resourceName;
}
