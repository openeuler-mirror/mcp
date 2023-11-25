package com.hnkylin.cloud.manage.entity.resp.workorder;


import com.hnkylin.cloud.core.enums.StorageUnit;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;


/**
 * 申请云服务器审核通过时，响应的申请详情，包括
 */
@Data
public class ApplyModifyVdcDetailRespDto extends BaseWorkOrderDetailDto {


    private String vdcName;

    private String orgName;


    //原始存储大小
    private Integer oldStorage = 0;

    //申请存储大小
    private Integer applyStorage = 0;


    //审核后存储大小
    private Integer realStorage = 0;

    private StorageUnit storageUnit = StorageUnit.GB;

    private List<ApplyModifyVdcArchitectureResourceRespDto> applyArchitectureResourceList = new ArrayList<>();


}
