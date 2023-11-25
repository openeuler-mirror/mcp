package com.hnkylin.cloud.manage.entity.resp.vdc;


import com.hnkylin.cloud.core.enums.StorageUnit;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class VdcModifyResourceRespDto {

    //当前存储
    private Integer currentStorage = 0;

    private StorageUnit storageUnit = StorageUnit.GB;

    //已使用存储
    private Integer usedStorage = 0;

    //上级可用存储
    private Integer parentUsableStorage = 0;


    //架构资源
    List<VdcModifyArchitectureResourceRespDto> architectureResourceList = new ArrayList<>();

    private Boolean modifyChild = false;


}
