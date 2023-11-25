package com.hnkylin.cloud.manage.entity.req.workorder;

import com.hnkylin.cloud.core.annotation.FieldCheck;
import com.hnkylin.cloud.core.enums.StorageUnit;
import com.hnkylin.cloud.manage.entity.req.vdc.ApplyModifyArchitectureResourceParam;
import lombok.Data;

import java.util.List;

/**
 * 申请变更-vdc资源
 */
@Data
public class PassModifyVdcResourceParam {


    private Integer workOrderId;

    private List<PassModifyArchitectureResourceParam> architectureResourceList;

    @FieldCheck(notNull = true, notNullMessage = "存储不能为空")
    private Integer realStorage;

    private StorageUnit storageUnit = StorageUnit.GB;

    @FieldCheck(notNull = true, notNullMessage = "审核意见不能为空")
    private String auditOpinion;


}
