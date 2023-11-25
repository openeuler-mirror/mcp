package com.hnkylin.cloud.manage.entity.req.vdc;

import com.hnkylin.cloud.core.annotation.FieldCheck;
import com.hnkylin.cloud.core.enums.ArchitectureType;
import com.hnkylin.cloud.core.enums.MemUnit;
import lombok.Data;


/**
 * vdc架构资源
 */
@Data
public class VdcArchitectureResourceParam {

    @FieldCheck(minNum = 1, minNumMessage = "分配CPU不能小于1")
    private Integer allocationCpu = 0;

    @FieldCheck(minNum = 1, minNumMessage = "分配内存不能小于1")
    private Integer allocationMem = 0;

    private MemUnit memUnit = MemUnit.GB;

    private ArchitectureType architectureType;


}
