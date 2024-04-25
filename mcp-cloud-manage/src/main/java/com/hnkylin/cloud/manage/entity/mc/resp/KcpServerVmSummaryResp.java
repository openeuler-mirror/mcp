package com.hnkylin.cloud.manage.entity.mc.resp;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class KcpServerVmSummaryResp {


    private BigDecimal cpuCount;


    private BigDecimal cpuPercent;

    private BigDecimal memoryPercent;

    private BigDecimal memoryTotal;

    private BigDecimal memoryUsed;

    private BigDecimal memorySurplus;

    private BigDecimal diskTotal;

    private BigDecimal diskTotalUsed;

    private BigDecimal diskSurplus;

    private BigDecimal diskPercent;

    private String plateformType;

}
