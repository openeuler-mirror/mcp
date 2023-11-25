package com.hnkylin.cloud.manage.entity.resp.vdc;

import com.hnkylin.cloud.core.enums.ArchitectureType;
import com.hnkylin.cloud.core.enums.MemUnit;
import com.hnkylin.cloud.core.enums.StorageUnit;
import lombok.Data;

/**
 * Vdc-架构已使用资源
 */
@Data
public class VdcArchitectureUsedResourceDto {

    //总cpu
    private Integer totalCpu;

    //已使用CPU(本级已使用+已分配下级)
    private Integer usedCpu;

    //已分配下级
    private Integer allocationCpu;

    //本级用户已使用
    private Integer sameUserUsedCpu;

    //剩余cpu
    private Integer surplusCpu;


    //总内存
    private Integer totalMem;

    //已使用内存（本级已使用+已分配下级）
    private Integer usedMem;

    //已分配下级
    private Integer allocationMem;

    //本级用户已使用
    private Integer sameUserUsedMem;

    private MemUnit memUnit;

    //剩余内存
    private Integer surplusMem;

    private ArchitectureType architectureType;


}
