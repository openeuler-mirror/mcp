package com.hnkylin.cloud.selfservice.entity.mc.resp;

import lombok.Data;

import java.math.BigDecimal;

/**
 * Created by kylin-ksvd on 21-8-18.
 * 云服务器概要信息-磁盘
 */
@Data
public class McServerVmDiskInfo {

    //磁盘id
    private Integer id;
    //磁盘总大小
    private Integer total;
    //已使用
    private Integer used;
    //使用率
    private BigDecimal usedPercent;
}
