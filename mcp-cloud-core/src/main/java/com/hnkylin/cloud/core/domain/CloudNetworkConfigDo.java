package com.hnkylin.cloud.core.domain;


import com.baomidou.mybatisplus.annotation.TableName;
import com.hnkylin.cloud.core.enums.NetworkSecurityPolicy;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName("cloud_network_config")
public class CloudNetworkConfigDo extends BaseDo {


    private Integer vdcId;

    private Integer clusterId;

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
