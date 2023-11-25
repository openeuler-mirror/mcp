package com.hnkylin.cloud.manage.entity.resp.org;

import com.hnkylin.cloud.core.enums.MemUnit;
import com.hnkylin.cloud.core.enums.StorageUnit;
import lombok.Data;


@Data
public class OrgSummaryRespDto {

    private String orgName;


    private String vdcName;

    private String parentOrgName;

    private String remark;

    private String createTime;


    private Integer totalCpu;

    private Integer allocationCpu;

    private Integer surplusCpu;

    private Integer totalMem;

    private Integer allocationMem;

    private Integer surplusMem;

    private MemUnit memUnit;

    private Integer totalStorage;

    private Integer allocationStorage;

    private Integer surplusStorage;

    private StorageUnit storageUnit;


    private Integer totalUser;

    private Integer activeUser;

    private Integer noActiveUser;


    //总云服务器个数
    private Integer machineTotal;
    //在线云服务器
    private Integer machineOnline;
    //离线云服务器
    private Integer machineOffline;


}
