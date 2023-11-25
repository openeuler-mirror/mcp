package com.hnkylin.cloud.manage.entity.resp.index;


import lombok.Data;

import java.math.BigDecimal;

/**
 * 首页统计数据
 */
@Data
public class IndexTopPhysicalHostUsedRatioData {

    private String serverIp;

    private BigDecimal usedRatio;
}
