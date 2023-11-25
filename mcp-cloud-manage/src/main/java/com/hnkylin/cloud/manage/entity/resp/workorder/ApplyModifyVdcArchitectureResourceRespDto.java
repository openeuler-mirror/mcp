package com.hnkylin.cloud.manage.entity.resp.workorder;

import com.hnkylin.cloud.core.enums.ArchitectureType;
import com.hnkylin.cloud.core.enums.MemUnit;
import lombok.Data;


/**
 * vdc架构资源
 */
@Data
public class ApplyModifyVdcArchitectureResourceRespDto {


    //原始cpu大小
    private Integer oldCpu = 0;

    //申请存储大小
    private Integer applyCpu = 0;

    //审核后存储大小
    private Integer realCpu = 0;


    //原始内存
    private Integer oldMem = 0;

    //申请内存大小
    private Integer applyMem = 0;

    //审核后存储大小
    private Integer realMem = 0;

    private MemUnit memUnit = MemUnit.GB;


    //架构
    private ArchitectureType architectureType;


}
