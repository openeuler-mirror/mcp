package com.hnkylin.cloud.selfservice.entity.mc.resp;

import lombok.Data;

import java.math.BigDecimal;

/**
 * Created by kylin-ksvd on 21-8-18.
 * 云服务器概要信息-内存
 */
@Data
public class McServerVmMemoryInfo {

    //内存大小
    private Integer memoryTotal;

    //内存使用率
    private BigDecimal memoryPercent;

    private Integer memoryUsed;
}
