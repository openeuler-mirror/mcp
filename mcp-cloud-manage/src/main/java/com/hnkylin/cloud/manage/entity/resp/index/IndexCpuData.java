package com.hnkylin.cloud.manage.entity.resp.index;


import lombok.Data;

import java.math.BigDecimal;

/**
 * 首页统计数据
 */
@Data
public class IndexCpuData {
    //总Cpu
    private BigDecimal totalCpu = BigDecimal.ZERO;
    ;

    //已使用cpu
    private BigDecimal usedCpu = BigDecimal.ZERO;
    ;

    //剩余可用CPU
    private BigDecimal usableCpu = BigDecimal.ZERO;
    ;
}
