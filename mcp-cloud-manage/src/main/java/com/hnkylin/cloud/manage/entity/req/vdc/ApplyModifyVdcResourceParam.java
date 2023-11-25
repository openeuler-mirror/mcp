package com.hnkylin.cloud.manage.entity.req.vdc;

import com.hnkylin.cloud.core.annotation.FieldCheck;
import com.hnkylin.cloud.core.enums.StorageUnit;
import lombok.Data;

import java.util.List;

/**
 * 申请变更-vdc资源
 */
@Data
public class ApplyModifyVdcResourceParam {


    private Integer vdcId;

    private List<ApplyModifyArchitectureResourceParam> architectureResourceList;

    @FieldCheck(notNull = true, notNullMessage = "存储不能为空")
    private Integer applyStorage;

    private StorageUnit storageUnit = StorageUnit.GB;

    @FieldCheck(notNull = true, notNullMessage = "申请原因不能为空")
    private String applyReason;


}
