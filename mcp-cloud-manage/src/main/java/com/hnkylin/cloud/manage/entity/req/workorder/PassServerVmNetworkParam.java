package com.hnkylin.cloud.manage.entity.req.workorder;

import com.hnkylin.cloud.core.enums.ApplyMcServerVmType;
import com.hnkylin.cloud.core.enums.LastUpdateType;
import com.hnkylin.cloud.core.enums.ModifyType;
import com.hnkylin.cloud.core.enums.NetworkSecurityPolicy;
import lombok.Data;

@Data
public class PassServerVmNetworkParam {

    //网络类型
    private String interfaceType;

    //虚拟交换机
    private String virtualSwitch;

    //端口组
    private String portGroup;

    private String portGroupUuid;

    //网卡类型
    private String modelType;

    //地址池
    private String macAddressPool;

    //网卡多队列
    private Integer queueCount = 0;

    //mtu值
    private Integer mtuCount = 1500;

    //安全策略
    private String securityStrategy = NetworkSecurityPolicy.NONE.getValue();

    //安全组UUID
    private String securityGroupUuid;

    //虚拟防火墙id
    private Integer virtualFirewall = 0;


    //自定义网卡ID
    private Integer networkId;

    private ApplyMcServerVmType type = ApplyMcServerVmType.custom;

    private Integer id;


    private Integer applyId;

    //变更类型
    private ModifyType modifyType;


    //是否绑定mac地址
    private Boolean ipBindMac = false;

    //是否手动设置ip
    private Boolean manualSetIp = false;

    //是否自动设置ip
    private Boolean automaticAcqIp = false;

    private String ip;
    private String mask;

    private String gw;
    private String dns1;
    private String dns2;
}
