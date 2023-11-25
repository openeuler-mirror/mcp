package com.hnkylin.cloud.selfservice.entity.mc.resp;

import lombok.Data;

/**
 * Created by kylin-ksvd on 21-8-18.
 * 云服务器概要信息-网络
 */
@Data
public class McServerVmNetworkInfo {

    //虚拟交换机名称
    private String virtualSwitch;
    //端口组
    private String portGroup;
    //mac地址
    private String macAddress;

    private String interfaceType;

    //地址池
    private String macAddressPool;

    //安全组
    private String securityGroup;

    private Integer id;

    private Boolean ipBindMac;

    private Boolean manualSetIp;

    private Boolean automaticAcqIp;

    private String ip;
    private String mask;

    private String gw;
    private String dns1;
    private String dns2;
}
