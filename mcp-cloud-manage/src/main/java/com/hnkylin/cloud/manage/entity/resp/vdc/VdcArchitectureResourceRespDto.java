package com.hnkylin.cloud.manage.entity.resp.vdc;

import com.hnkylin.cloud.core.enums.ArchitectureType;
import com.hnkylin.cloud.core.enums.MemUnit;
import com.hnkylin.cloud.core.enums.StorageUnit;
import lombok.Data;


/**
 * vdc架构资源
 */
@Data
public class VdcArchitectureResourceRespDto {


    //可用cpu
    private Integer usableVcpu = 0;

    //已使用CPU=本级VDC用户直接使用cpu+已分配给下级VDC CPU
    private Integer usedCpu = 0;

    //总cpu
    private Integer totalCpu;


    //可用内存
    private Integer usableMem = 0;

    //已使用内存=本级VDC用户直接使用内存+已分配给下级VDC 内存
    private Integer usedMem = 0;

    private MemUnit memUnit = MemUnit.GB;

    //总内存
    private Integer totalMem;

    //架构
    private ArchitectureType architectureType;


}
