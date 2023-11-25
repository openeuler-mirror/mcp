package com.hnkylin.cloud.manage.entity.resp.vdc;

import com.hnkylin.cloud.core.enums.StorageUnit;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;


@Data
public class VdcResourceRespDto {


    //可用存储
    private Integer usableStorage = 0;


    //已使用存储=本级VDC用户直接使用存储资源+已分配给下级VDC存储
    private Integer usedStorage = 0;

    private Integer totalStorage;


    //存储单位
    private StorageUnit storageUnit = StorageUnit.TB;


    private List<VdcArchitectureResourceRespDto> architectureResourceList = new ArrayList<>();


}
