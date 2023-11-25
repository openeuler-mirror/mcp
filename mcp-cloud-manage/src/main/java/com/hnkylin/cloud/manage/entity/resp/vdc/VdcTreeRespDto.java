package com.hnkylin.cloud.manage.entity.resp.vdc;

import com.hnkylin.cloud.core.enums.MemUnit;
import com.hnkylin.cloud.core.enums.StorageUnit;
import lombok.Data;

import java.util.List;

@Data
public class VdcTreeRespDto extends VdcInfoRespDto {


    private Integer vdcId;

    private String vdcName;

    private Integer parentId;

    private String parentName;

    private String remark;

    private String orgName;


    private Integer networkNum;

    private String zoneName;


    //总cpu
    private Integer totalCpu;

    //已使用CPU(本级已使用+已分配下级)
    private Integer usedCpu;


    //总内存
    private Integer totalMem;

    //已使用内存（本级已使用+已分配下级）
    private Integer usedMem;


    private MemUnit memUnit;

    //总存储
    private Integer totalStorage;

    //已使用存储(本级已使用+已分配下级)
    private Integer usedStorage;


    //剩余存储
    private Integer surplusStorage;

    private StorageUnit storageUnit;


    //子组织列表
    private List<VdcTreeRespDto> children;
}
