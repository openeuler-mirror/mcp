package com.hnkylin.cloud.manage.entity.mc.resp;

import lombok.Data;

import java.util.List;

@Data
public class McServerVmDetailResp {

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


    private Integer serverClusterType;

    private String selectTagIds;

    private String selectTagNames;

    private List<McServerVmDiskDetailResp> disks;

    private List<McServerVmNetworkDetailResp> interfaceList;

}
