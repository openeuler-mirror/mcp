package com.hnkylin.cloud.selfservice.entity.mc.resp;

import lombok.Data;

import java.util.List;

@Data
public class McTemplateDetailResp {

    private String alisname;

    private String osMachine;

    private String architecture;

    private String systemType;

    private Integer cpu;

    private Integer mem;

    private String memUnit;

    private String selectCluster;

    private String selectClusterUuid;

    private Integer storageLocationId;

    private List<McTemplateDiskDetailResp> disks;

    private List<McTemplateNetworkDetailResp> interfaceList;
}
