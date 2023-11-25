package com.hnkylin.cloud.manage.entity.mc.req;

import com.hnkylin.cloud.core.enums.McServerClusterType;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kylin-ksvd on 21-6-25.
 */
@Data
public class McModifyServerVmParamReq {


    private String uuid;

    private String aliasName;

    private String selectCluster;


    private String selectClusterUuid;

    private Integer storageLocationId;


    private Integer vcpus;

    private Integer memory;

    private String memUnit;

    private String plateformtype;

    private String systemVersion;

    private String operatingSystem;

    private String systemType;


    private List<McModifyServerVmDiskParam> diskCapacity;


    private List<McModifyServerVmInterfacesParam> interfaces;

    private boolean existHostIpConfig = false;

    //主机模式类型
    private Integer clusterType = McServerClusterType.CUSTOM.getValue();

    private String selectResourceTagId;


}
