package com.hnkylin.cloud.manage.entity.req.vdc;

import com.hnkylin.cloud.core.annotation.FieldCheck;
import com.hnkylin.cloud.core.enums.ArchitectureType;
import com.hnkylin.cloud.core.enums.MemUnit;
import lombok.Data;


/**
 * vdc架构资源
 */
@Data
public class ApplyModifyArchitectureResourceParam {

    @FieldCheck(minNum = 1, minNumMessage = "分配CPU不能小于1")
    private Integer applyCpu = 0;

    @FieldCheck(minNum = 1, minNumMessage = "分配内存不能小于1")
    private Integer applyMem = 0;

    private MemUnit memUnit = MemUnit.GB;

    private ArchitectureType architectureType;


}
