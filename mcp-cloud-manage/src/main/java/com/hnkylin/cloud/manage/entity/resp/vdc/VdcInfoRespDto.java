package com.hnkylin.cloud.manage.entity.resp.vdc;


import com.hnkylin.cloud.core.enums.MemUnit;
import com.hnkylin.cloud.core.enums.StorageUnit;
import com.hnkylin.cloud.manage.entity.resp.network.NetworkDetailDto;
import lombok.Data;

import java.util.List;

@Data
public class VdcInfoRespDto {

    private Integer vdcId;

    private String vdcName;

    private Integer parentId;

    private String parentName;

    private String remark;

    private String orgName;


    private Integer networkNum;

    private String zoneName;


    private String createTime;


    //总cpu
    private Integer totalCpu;

    //已使用CPU(本级已使用+已分配下级)
    private Integer usedCpu;

    //已分配下级
    private Integer allocationCpu;

    //本级用户已使用
    private Integer sameUserUsedCpu;

    //剩余cpu
    private Integer surplusCpu;

    //总内存
    private Integer totalMem;

    //已使用内存（本级已使用+已分配下级）
    private Integer usedMem;

    //已分配下级
    private Integer allocationMem;

    //本级用户已使用
    private Integer sameUserUsedMem;

    //剩余内存
    private Integer surplusMem;


    private MemUnit memUnit;

    //总存储
    private Integer totalStorage;

    //已使用存储(本级已使用+已分配下级)
    private Integer usedStorage;

    //已分配下级
    private Integer allocationStorage;

    //本级用户已使用
    private Integer sameUserUsedStorage;

    //剩余存储
    private Integer surplusStorage;

    private StorageUnit storageUnit;


    private List<NetworkDetailDto> networkList;

    //各个架构资源使用情况
    private List<CommonVdcArchitectureResource> architectureUsedResourceList;

}
