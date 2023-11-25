package com.hnkylin.cloud.manage.entity.resp.workorder;

import com.hnkylin.cloud.core.enums.ArchitectureType;
import com.hnkylin.cloud.core.enums.MemUnit;
import lombok.Data;


/**
 * vdc架构资源
 */
@Data
public class ModifyVdcArchitectureResourceRespDto {


    //当前cpu
    private Integer currentVcpu = 0;

    //已使用CPU=本级VDC用户直接使用cpu+已分配给下级VDC CPU
    private Integer usedCpu = 0;

    //上级可用cpu
    private Integer parentUsableCpu = 0;

    private Integer applyCpu = 0;


    //当前内存
    private Integer currentMem = 0;

    //已使用内存=本级VDC用户直接使用内存+已分配给下级VDC 内存
    private Integer usedMem = 0;

    //上级可用内存
    private Integer parentUsableMem = 0;

    private MemUnit memUnit = MemUnit.GB;


    private Integer applyMem = 0;


    //架构
    private ArchitectureType architectureType;


}
