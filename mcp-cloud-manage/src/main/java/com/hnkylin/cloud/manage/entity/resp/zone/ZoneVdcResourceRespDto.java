package com.hnkylin.cloud.manage.entity.resp.zone;

import lombok.Data;


/**
 * 可用区下已分配所有一级VDC资源
 */
@Data
public class ZoneVdcResourceRespDto {

    //分配cpu和
    private Integer totalAllocationCpu = 0;

    //分配内存和
    private Integer totalAllocationMem = 0;

    //分配存储和
    private Integer totalAllocationStorage = 0;


}
