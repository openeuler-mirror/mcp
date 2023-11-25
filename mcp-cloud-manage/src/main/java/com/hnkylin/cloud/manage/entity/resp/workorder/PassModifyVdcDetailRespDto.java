package com.hnkylin.cloud.manage.entity.resp.workorder;


import com.hnkylin.cloud.core.enums.StorageUnit;
import com.hnkylin.cloud.manage.entity.resp.vdc.VdcModifyResourceRespDto;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;


/**
 * 申请云服务器审核通过时，响应的申请详情，包括
 */
@Data
public class PassModifyVdcDetailRespDto {


    private Integer workOrderId;

    private String vdcName;

    private String orgName;

    private boolean firstVdc = false;

    //当前存储
    private Integer currentStorage = 0;

    private StorageUnit storageUnit = StorageUnit.GB;

    //已使用存储
    private Integer usedStorage = 0;

    //上级可用存储
    private Integer parentUsableStorage = 0;

    private Integer applyStorage;

    private List<ModifyVdcArchitectureResourceRespDto> applyArchitectureResourceList = new ArrayList<>();


}
