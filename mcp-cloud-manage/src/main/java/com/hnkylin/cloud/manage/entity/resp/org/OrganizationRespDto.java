package com.hnkylin.cloud.manage.entity.resp.org;

import com.hnkylin.cloud.core.enums.MemUnit;
import com.hnkylin.cloud.core.enums.StorageUnit;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class OrganizationRespDto extends CommonOrgTreeRespDto {


    //vdcId
    private Integer vdcId = 0;

    //vdc名称
    private String vdcName = "-";

    //用户数
    private Integer userNum;

    //云服务器数量
    private Integer serverVmNum;

    //分配cpu
    private Integer allocationCpu = 0;

    //已使用Cpu
    private Integer usedCpu = 0;

    //分配内存
    private BigDecimal allocationMem = BigDecimal.ZERO;

    //已使用内存
    private BigDecimal usedMem = BigDecimal.ZERO;

    private MemUnit memUnit = MemUnit.GB;

    //分配磁盘
    private BigDecimal allocationDisk = BigDecimal.ZERO;

    //已使用磁盘
    private BigDecimal usedDisk = BigDecimal.ZERO;

    private StorageUnit storageUnit = StorageUnit.GB;

//    //组织管理员ID
//    private Integer orgLeaderUserId;
//
//    //组织管理员名称
//    private String orgLeaderUserName;

    //子组织列表
    private List<OrganizationRespDto> children;
}
