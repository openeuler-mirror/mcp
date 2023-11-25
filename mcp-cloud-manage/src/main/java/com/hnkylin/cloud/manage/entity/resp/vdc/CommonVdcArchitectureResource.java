package com.hnkylin.cloud.manage.entity.resp.vdc;

import com.hnkylin.cloud.core.enums.ArchitectureResourceType;
import com.hnkylin.cloud.core.enums.ArchitectureType;
import lombok.Data;

@Data
public class CommonVdcArchitectureResource {


    //架构类型
    private ArchitectureType architectureType;

    //资源类型
    private ArchitectureResourceType resourceType;

    //总
    private Integer total = 0;

    //已使用（本级已使用+已分配下级）
    private Integer used = 0;

    //已分配下级
    private Integer allocationChild = 0;

    //本级用户已使用
    private Integer sameUserUsed = 0;


    //剩余
    private Integer surplus = 0;
}
