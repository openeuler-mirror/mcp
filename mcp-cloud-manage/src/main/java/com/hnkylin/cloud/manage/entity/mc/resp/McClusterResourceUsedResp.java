package com.hnkylin.cloud.manage.entity.mc.resp;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 集群管理-资源使用情况
 */
@Data
public class McClusterResourceUsedResp {

    //cpu已使用
    private BigDecimal cpuUsed = BigDecimal.ZERO;
    //cpu总大小
    private BigDecimal cpuTotal = BigDecimal.ZERO;

    private BigDecimal cpuSurplus = BigDecimal.ZERO;
    //内存已使用
    private BigDecimal memUsed = BigDecimal.ZERO;
    //内存总大小
    private BigDecimal memTotal = BigDecimal.ZERO;

    private BigDecimal memSurplus = BigDecimal.ZERO;
    //存储已使用
    private BigDecimal storageUsed = BigDecimal.ZERO;
    //存储总大小
    private BigDecimal storageTotal = BigDecimal.ZERO;

    private BigDecimal storageSurplus = BigDecimal.ZERO;


    public void reCalculate() {
        this.cpuTotal = this.cpuTotal.setScale(2, BigDecimal.ROUND_HALF_UP);
        this.cpuUsed = this.cpuUsed.setScale(2, BigDecimal.ROUND_HALF_UP);
        this.cpuSurplus = this.cpuTotal.subtract(this.cpuUsed).setScale(2, BigDecimal.ROUND_HALF_UP);

        this.memTotal = this.memTotal.setScale(2, BigDecimal.ROUND_HALF_UP);
        this.memUsed = this.memUsed.setScale(2, BigDecimal.ROUND_HALF_UP);
        this.memSurplus = this.memTotal.subtract(this.memUsed).setScale(2, BigDecimal.ROUND_HALF_UP);


        this.storageTotal = this.storageTotal.setScale(2, BigDecimal.ROUND_HALF_UP);
        this.storageUsed = this.storageUsed.setScale(2, BigDecimal.ROUND_HALF_UP);
        this.storageSurplus = this.storageTotal.subtract(this.storageUsed).setScale(2, BigDecimal.ROUND_HALF_UP);
    }

}
