package com.hnkylin.cloud.manage.entity.mc.resp;

import lombok.Data;

import java.util.List;

@Data
public class NetworkConfigResp {

    private List<NetworkVirtualSwitchResp> virtualSwitchList;

    private List<NetworkAddressPoolResp> addressPoolList;

    private List<NetworkSecurityGroupResp> securityGroupList;

    private List<NetworkVirtualFirewallResp> virtualFirewallList;
}
