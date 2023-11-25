package com.hnkylin.cloud.manage.entity.mc.req;

import com.hnkylin.cloud.core.annotation.FieldCheck;
import com.hnkylin.cloud.core.enums.McCloneType;
import com.hnkylin.cloud.core.enums.McServerClusterType;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kylin-ksvd on 21-6-25.
 */
@Data
public class McCreateServerVmParamReq {


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

    private Integer vmNumber;

    private String isOpenRemote = "1";

    private String remotePassword;

    private String description;


    private List<McCreateServerVmDiskParam> diskCapacity;


    private List<McCreateServerVmInterfacesParam> interfaces;

    private String serviceTemplateId;

    private List<McCreateServerVmIsoParam> isoSelect;

    private String applyUser;

    private boolean existHostIpConfig = false;

    //主机模式类型
    private Integer clusterType = McServerClusterType.CUSTOM.getValue();

    private String selectResourceTagId;

    private String cloneType = McCloneType.FULL_CLONE.getValue().toString();


}
