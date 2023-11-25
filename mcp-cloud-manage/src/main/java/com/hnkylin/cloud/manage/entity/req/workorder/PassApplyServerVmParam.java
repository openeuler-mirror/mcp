package com.hnkylin.cloud.manage.entity.req.workorder;


import com.hnkylin.cloud.core.annotation.FieldCheck;
import com.hnkylin.cloud.core.enums.McCloneType;
import com.hnkylin.cloud.core.enums.McServerClusterType;
import lombok.Data;

import java.util.List;

@Data
public class PassApplyServerVmParam {

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

    @FieldCheck(notNull = true, notNullMessage = "申请个数不能为空")
    private Integer vmNumber;

    private String remotePassword;


    //磁盘列表
    private List<PassServerVmDiskParam> diskList;

    //网卡信息
    private List<PassServerVmNetworkParam> networkList;

    //iso信息
    private List<PassServerVmIsoParam> isoList;

    //主机模式类型
    private McServerClusterType serverClusterType = McServerClusterType.CUSTOM;

    //主机绑定资源
    private String selectResourceTagId;

    //创建方式  链接克隆  完整克隆
    private McCloneType cloneType = McCloneType.FULL_CLONE;


}
