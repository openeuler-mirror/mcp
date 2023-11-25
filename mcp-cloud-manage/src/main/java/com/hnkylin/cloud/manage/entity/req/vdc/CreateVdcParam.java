package com.hnkylin.cloud.manage.entity.req.vdc;

import com.hnkylin.cloud.core.annotation.FieldCheck;
import com.hnkylin.cloud.core.enums.StorageUnit;
import lombok.Data;

import java.util.List;

/**
 * 创建VDC时检查是否能创建
 */
@Data
public class CreateVdcParam {

    @FieldCheck(notNull = true, notNullMessage = "可用区不能为空")
    private Integer zoneId;

    @FieldCheck(notNull = true, notNullMessage = "上级VDC不能为空")
    private Integer parentVdcId;


    @FieldCheck(notNull = true, notNullMessage = "上级VDC不能为空")
    private String vdcName;

    private String remark;


    @FieldCheck(minNum = 1, minNumMessage = "分配存储不能小于1")
    private Integer allocationStorage;


    private StorageUnit storageUnit;


    private List<VdcArchitectureResourceParam> architectureResourceList;


    private List<VdcNetworkParam> networkList;


}
