package com.hnkylin.cloud.manage.entity.mc.resp;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class McDataStoreInfoResp {


    private String name;

    private BigDecimal totalSize;

    private BigDecimal usedSize;

    private BigDecimal availSize;

    private BigDecimal limitAvailSize;
}
