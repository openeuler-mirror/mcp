package com.hnkylin.cloud.manage.entity.resp.network;


import com.hnkylin.cloud.core.enums.NetworkSecurityPolicy;
import lombok.Data;

@Data
public class NetworkConfigRespDto {

    private Integer networkId;

    private String networkName;

    private String interfaceType;

    private String addressPool;

    private String virtualSwitch;

    private String modelType;

    private String portGroup;

    private String portGroupUuid;

    private NetworkSecurityPolicy securityPolicy;

    private String securityGroup;

    private String securityGroupUuid;

    private Integer virtualFirewallId = 0;

    private String virtualFirewallName;

    private String createTime;

    private String vdcName;

    private String clusterName;

    private Integer clusterId;

    private Integer vdcId;


}
