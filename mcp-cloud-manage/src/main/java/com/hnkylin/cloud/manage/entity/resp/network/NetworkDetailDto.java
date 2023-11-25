package com.hnkylin.cloud.manage.entity.resp.network;

import com.hnkylin.cloud.core.enums.NetworkSecurityPolicy;
import lombok.Data;


@Data
public class NetworkDetailDto {


    private Integer networkId;

    private Integer clusterId;

    private String clusterName;

    private String networkName;

    private String interfaceType;

    private String addressPool;

    private Integer addressPoolId;

    private String virtualSwitch;

    private String modelType;

    private String portGroup;

    private String portGroupUuid;

    private NetworkSecurityPolicy securityPolicy;

    private String securityGroup;

    private String securityGroupUuid;

    private Integer virtualFirewallId;

    private String virtualFirewallName;


}
