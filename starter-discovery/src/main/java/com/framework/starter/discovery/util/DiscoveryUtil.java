package com.framework.starter.discovery.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * 功能概述
 * className:      HostUtil
 * package:        com.framework.starter.discovery.util
 * author:         Gavin.Xu
 * date:           2021/6/13
 */
@Slf4j
public class DiscoveryUtil {

    public final static String SEP = "/";

    public final static String SERVICE_PREFIX = "/services";

    /**
     * get local host Ip from property or localhost
     * @return
     */
    public static String getHostIp() {
        String hostMachineIp = System.getProperty("HOST_MACHINE_IP");
        if (StringUtils.isNotBlank(hostMachineIp)){
            return hostMachineIp;
        }
        InetAddress inetAddress = null;
        try {
            inetAddress = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            log.error("getAddress error", e);
        }
        if (inetAddress != null) {
            return StringUtils.substringAfterLast(inetAddress.toString(), "/");
        }
        return "0.0.0.0";
    }

    /**
     *
     * @param prefix eg:/services
     * @param name eg:event-server
     * @param version eg:v1.0
     * @param protocol eg:grpc/http
     * @return
     */
    public static String getKey(String prefix,String name,String version,String protocol){
        return new StringBuilder(prefix).append(SEP)
                .append(name).append(SEP)
                .append(version).append(SEP)
                .append(protocol).append(SEP)
                .toString();
    }


}
