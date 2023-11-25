package com.hnkylin.cloud.selfservice.entity.mc.resp;

import lombok.Data;

import java.math.BigDecimal;

/**
 * Created by kylin-ksvd on 21-8-18.
 * 云服务器概要信息-cpu
 */
@Data
public class McServerVmCpuInfo {

    //cpu核数
    private Integer cpus;

    //cpu使用率
    private BigDecimal usedPercent;
}
