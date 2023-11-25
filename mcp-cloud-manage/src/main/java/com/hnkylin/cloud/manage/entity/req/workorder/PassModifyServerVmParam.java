package com.hnkylin.cloud.manage.entity.req.workorder;


import com.hnkylin.cloud.core.annotation.FieldCheck;
import com.hnkylin.cloud.core.enums.McServerClusterType;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class PassModifyServerVmParam {

    @FieldCheck(notNull = true, notNullMessage = "工单ID不能为空")
    private Integer workOrderId;

    @FieldCheck(notNull = true, notNullMessage = "审核意见不能为空")
    private String auditOpinion;

    @FieldCheck(notNull = true, notNullMessage = "服务器名称不能为空")
    private String aliasName;

    private String selectCluster;

    private String selectClusterUuid;

    @FieldCheck(notNull = true, notNullMessage = "存储位置不能为空")
    private Integer storageLocationId;


    @FieldCheck(notNull = true, notNullMessage = "cpu不能为空")
    private Integer vcpus;

    @FieldCheck(notNull = true, notNullMessage = "内存不能为空")
    private Integer memory;

    private String memUnit;

    @FieldCheck(notNull = true, notNullMessage = "架构不能为空")
    private String plateformType;

    @FieldCheck(notNull = true, notNullMessage = "操作系统不能为空")
    private String operatingSystem;

    private String systemType;


    private List<PassServerVmDiskParam> diskList = new ArrayList<>();


    private List<PassServerVmNetworkParam> networkList = new ArrayList<>();

    //主机模式类型
    private McServerClusterType serverClusterType = McServerClusterType.CUSTOM;

    private String selectResourceTagId;

}
