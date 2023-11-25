package com.hnkylin.cloud.manage.entity.resp.index;


import lombok.Data;

/**
 * 首页统计数据
 */
@Data
public class IndexVdcData {


    //总VDC数
    private Integer totalVdc = 0;

    //已分配VDC
    private Integer alreadyAllocateVdc = 0;

    //未分配VDC
    private Integer noAllocateVdc = 0;
}
