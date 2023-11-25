package com.hnkylin.cloud.manage.entity.resp.index;


import lombok.Data;

import java.math.BigDecimal;

/**
 * 首页统计数据
 */
@Data
public class IndexMemData {

    //总内存
    private BigDecimal totalMem = BigDecimal.ZERO;

    //已使用内存
    private BigDecimal usedMem = BigDecimal.ZERO;
    ;

    //已使用内存
    private BigDecimal usableMem = BigDecimal.ZERO;
    ;
}
