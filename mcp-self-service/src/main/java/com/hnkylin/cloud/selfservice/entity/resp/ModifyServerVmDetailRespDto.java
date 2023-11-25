package com.hnkylin.cloud.selfservice.entity.resp;


import com.hnkylin.cloud.core.enums.MemUnit;
import com.hnkylin.cloud.core.enums.ServerVmDeadlineType;
import lombok.Data;

import java.util.List;

@Data
public class ModifyServerVmDetailRespDto extends BaseWorkOrderDetailDto {


    //操作系统
    private String osMachine;


    private String architecture;


    //到期处理策略
    private ServerVmDeadlineType deadlineType;

    private String deadlineTypeDesc;


    private String deadLineTime;

    //cpu
    private Integer cpu;

    //变更后cpu(审核时管理员可能修改)
    private Integer originalCpu;

    //审核时是否变更了CPU
    private boolean ifModifyCpu;

    //内存
    private Integer mem;

    //变更前(审核时管理员可能修改)
    private Integer originalMem;

    //审核时是否变更了内存
    private boolean ifModifyMem;

    private MemUnit menUtil;

    //申请时间
    private String applyTime;

    //硬盘信息
    private List<ServerVmDiskDto> disks;

    //网卡信息
    private List<ServerVmNetworkDto> networks;


}
