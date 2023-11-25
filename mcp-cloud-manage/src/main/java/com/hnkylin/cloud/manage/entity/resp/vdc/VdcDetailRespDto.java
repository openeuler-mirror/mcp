package com.hnkylin.cloud.manage.entity.resp.vdc;

import lombok.Data;


@Data
public class VdcDetailRespDto {
    private Integer vdcId;
    private String vdcName;
    //分配cpu
    private Integer allocationCpu;

    //分配内存
    private Integer allocationMem;

    //分配磁盘
    private Integer allocationDisk;
}
