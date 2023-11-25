package com.hnkylin.cloud.manage.entity.mc.req;

import com.hnkylin.cloud.core.enums.ApplyMcServerVmType;
import com.hnkylin.cloud.core.enums.NetworkSecurityPolicy;
import lombok.Data;

import java.util.List;

/**
 * Created by kylin-ksvd on 21-6-25.
 */
@Data
public class McCreateServerVmInterfacesParam {

    //网络类型
    private String interfaceType;

    //虚拟交换机
    private String virtualSwitch;

    //端口组
    private String portGroup;

    //网卡类型
    private String modeltype;

    //mac地址
    private String macAddress;

    //安全策略
    private String securityStrategy = NetworkSecurityPolicy.NONE.getValue();

    //安全组
    private List<String> securityGroup;

    //虚拟防火墙
    private Integer virtualFirewall;

    private String lastUpdateType;

    private Boolean lsbind = false;

    private Boolean manualSetIP = false;

    private Boolean automaticAcqIp = false;

    private String ip;
    private String mask;

    private String gw;
    private String dns1;
    private String dns2;

    private String queueCount = "0";

    private String mtuCount = "1500";


}
