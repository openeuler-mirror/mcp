package com.hnkylin.cloud.manage.entity.mc.resp;

import lombok.Data;

@Data
public class KcpServerVmDiskResp {


    private String diskId;

    private String deviceName;

    private String device;

    private String targetBus;

    private String diskCapacity;

    private String sourceFile;
}
