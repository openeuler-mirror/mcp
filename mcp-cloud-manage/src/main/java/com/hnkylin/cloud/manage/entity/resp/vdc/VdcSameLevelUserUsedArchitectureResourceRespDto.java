package com.hnkylin.cloud.manage.entity.resp.vdc;

import com.hnkylin.cloud.core.enums.ArchitectureType;
import com.hnkylin.cloud.core.enums.MemUnit;
import lombok.Data;


/**
 * vdc架构资源
 */
@Data
public class VdcSameLevelUserUsedArchitectureResourceRespDto {


    //已使用cpu
    private Integer usedVcpu = 0;

    //已使用
    private Integer usedMem = 0;

    //架构
    private ArchitectureType architectureType;


}
