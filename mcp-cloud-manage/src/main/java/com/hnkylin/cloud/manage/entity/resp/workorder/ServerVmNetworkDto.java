package com.hnkylin.cloud.manage.entity.resp.workorder;

import com.hnkylin.cloud.core.enums.ModifyType;
import lombok.Data;

@Data
public class ServerVmNetworkDto {
    //网卡用途
    private String purpose;

    private ModifyType modifyType;

    private Boolean setIpInfo;

    private Boolean ipBindMac;

    private Boolean manualSetIp;

    private Boolean automaticAcqIp;

    private String ip;
    private String mask;

    private String gw;
    private String dns1;
    private String dns2;
}
