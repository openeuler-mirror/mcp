package com.hnkylin.cloud.manage.entity.resp.index;


import lombok.Data;

import java.math.BigDecimal;

/**
 * 首页统计数据
 */
@Data
public class IndexStorageData {

    //总存储
    private BigDecimal totalStorage = BigDecimal.ZERO;
    ;

    //已使用存储
    private BigDecimal usedStorage = BigDecimal.ZERO;
    ;

    //剩余存储
    private BigDecimal usableStorage = BigDecimal.ZERO;
    ;
}
