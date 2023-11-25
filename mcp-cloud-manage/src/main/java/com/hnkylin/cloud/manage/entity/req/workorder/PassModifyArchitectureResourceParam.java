package com.hnkylin.cloud.manage.entity.req.workorder;

import com.hnkylin.cloud.core.annotation.FieldCheck;
import com.hnkylin.cloud.core.enums.ArchitectureType;
import com.hnkylin.cloud.core.enums.MemUnit;
import lombok.Data;


/**
 * vdc架构资源
 */
@Data
public class PassModifyArchitectureResourceParam {

    @FieldCheck(minNum = 1, minNumMessage = "CPU不能小于1")
    private Integer realCpu = 0;

    @FieldCheck(minNum = 1, minNumMessage = "内存不能小于1")
    private Integer realMem = 0;

    private MemUnit memUnit = MemUnit.GB;

    private ArchitectureType architectureType;


}
