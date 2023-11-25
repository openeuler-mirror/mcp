package com.hnkylin.cloud.manage.entity.mc.resp;

import com.hnkylin.cloud.manage.enums.DataStoreStatus;
import com.hnkylin.cloud.manage.enums.DataStoreType;
import com.hnkylin.cloud.manage.enums.DataStoreUsage;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 集群管理-数据存储
 */
@Data
public class McClusterStorageResp {


    //存储ID
    private Integer id;

    //存储名称
    private String name;

    //存储用途
    private DataStoreUsage usage;

    //存储类型
    private DataStoreType type;

    //总大小
    private BigDecimal totalSize = BigDecimal.ZERO;

    //已使用
    private BigDecimal usedSize = BigDecimal.ZERO;

    //状态
    private DataStoreStatus status;

}
