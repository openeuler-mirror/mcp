package com.hnkylin.cloud.manage.entity.mc.resp;


import lombok.Data;

import java.math.BigDecimal;

/**
 * 首页统计数据
 */
@Data
public class McServerVmUseRatioData {


    //名称
    private String serverVmName;

    //cpu利用率
    private BigDecimal cpuUseRatio;

    //cpu利用率
    private BigDecimal memUseRatio;

    private String clusterName;

}
