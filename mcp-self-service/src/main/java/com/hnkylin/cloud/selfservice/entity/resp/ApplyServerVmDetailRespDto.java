package com.hnkylin.cloud.selfservice.entity.resp;


import com.hnkylin.cloud.core.enums.ApplyServerVmType;
import com.hnkylin.cloud.core.enums.MemUnit;
import com.hnkylin.cloud.core.enums.ServerVmDeadlineType;
import lombok.Data;

import java.util.List;

@Data
public class ApplyServerVmDetailRespDto extends BaseWorkOrderDetailDto {


    private ApplyServerVmType applyServerVmType;

    private String templateName;

    //操作系统
    private String osMachine;

    private String architecture;

    //使用时间
    private Integer useMonth;

    //申请个数
    private Integer applyNum;


    //变更后申请个数(审核时管理员可能修改)
    private Integer modifyApplyNum;

    //审核时是否变更了申请个数
    private boolean ifModifyApplyNum;


    //到期处理策略
    private ServerVmDeadlineType deadlineType;

    private String deadlineTypeDesc;

    //cpu
    private Integer cpu;

    //变更后cpu(审核时管理员可能修改)
    private Integer modifyCpu;

    //审核时是否变更了CPU
    private boolean ifModifyCpu;


    //内存
    private Integer mem;

    //变更后(审核时管理员可能修改)
    private Integer modifyMem;

    //审核时是否变更了内存
    private boolean ifModifyMem;

    private MemUnit menUtil;

    //申请时间
    private String applyTime;

    private String description;

    //硬盘信息
    private List<ServerVmDiskDto> disks;

    //网卡信息
    private List<ServerVmNetworkDto> networks;

    //iso光驱列表
    private List<ServerVmIsoDto> isoList;

    private String clusterName;


}
