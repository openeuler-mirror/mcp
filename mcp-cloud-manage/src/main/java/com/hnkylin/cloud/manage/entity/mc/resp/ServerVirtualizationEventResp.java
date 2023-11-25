package com.hnkylin.cloud.manage.entity.mc.resp;

import lombok.Data;

@Data
public class ServerVirtualizationEventResp {


    private String deploymentMode;

    private String desktopName;

    private String goldMaster;

    private String hostname;


    private String info;

    private String organization;

    private String severity;

    private String timestamp;

    private String type;

    private String username;
}
