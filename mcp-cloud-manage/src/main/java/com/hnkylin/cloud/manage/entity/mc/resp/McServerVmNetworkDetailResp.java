package com.hnkylin.cloud.manage.entity.mc.resp;

import com.hnkylin.cloud.core.enums.ApplyMcServerVmType;
import com.hnkylin.cloud.core.enums.LastUpdateType;
import com.hnkylin.cloud.core.enums.ModifyType;
import lombok.Data;

/**
 * mc中网卡实体信息
 */
@Data
public class McServerVmNetworkDetailResp {

    //虚拟交换机
    private String virtualSwitch;

    //端口组名称
    private String portGroup;

    //端口组uuid
    private String portGroupUuid;
    //mac地址
    private String macAddress;

    //地址池
    private String macAddressPool;

    //网络类型
    private String interfaceType;

    //网卡类型
    private String modelType;

    //网络id
    private Long id;

    //安全策略
    private Integer securityStrategy;

    //安全组uuid
    private String securityGroupUuid;

    //虚拟防火墙id
    private Integer virtualFirewall;

    //网卡多队列
    private Integer queueCount = 0;

    //mtu值
    private Integer mtuCount = 1500;


    //自定义网络ID
    private Integer networkId = 0;

    private String networkName = "";
    //申请时填写的用途
    private String purpose;

    private ApplyMcServerVmType type = ApplyMcServerVmType.original;

    private ModifyType modifyType;

    private ModifyType applyModifyType;


    //申请ID
    private Integer applyId = 0;


    private Boolean ipBindMac = false;

    private Boolean manualSetIp = false;

    private Boolean automaticAcqIp = false;

    private String ip;
    private String mask;

    private String gw;
    private String dns1;
    private String dns2;


}
