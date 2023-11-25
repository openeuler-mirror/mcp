package com.hnkylin.cloud.selfservice.entity.req;

import com.hnkylin.cloud.core.annotation.FieldCheck;
import com.hnkylin.cloud.core.enums.MemUnit;
import com.hnkylin.cloud.core.enums.ServerVmDeadlineType;
import lombok.Data;

import java.util.List;

@Data
public class ModifyServerVmParam {


    //操作系统
    private String osMachine;

    //架构
    private String architecture;

    private String systemType;


    //虚拟机名称
    @FieldCheck(notNull = true, notNullMessage = "虚拟机名称不能为空")
    private String servervmName;


    @FieldCheck(notNull = true, notNullMessage = "uuid")
    private String machineUuid;


    //是否新添加过期时间
    private boolean addDeadTimeChecked;

    //新添加过期时间
    private Integer addNewDeadTime;

    //新添加过期时间单位
    private String addNewDeadTimeUnit;

    //到期处理策略
    private ServerVmDeadlineType deadlineType;

    //cpu
    @FieldCheck(notNull = true, notNullMessage = "cpu不能为空")
    private Integer cpu;
    //内存
    @FieldCheck(notNull = true, notNullMessage = "内存不能为空")
    private Integer mem;


    //变更前cpu
    private Integer originalCpu;

    //变更前内存
    private Integer originalMem;

    //内存单位
    private MemUnit memUnit;


    //申请原因
    @FieldCheck(notNull = true, notNullMessage = "申请原因不能为空")
    private String applyReason;

    //硬盘信息
    private List<ServerVmDiskParam> diskList;

    //原来的硬盘信息
    private List<ServerVmDiskParam> oldDiskList;


    //网卡信息
    private List<ServerVmNetworkParam> networkList;

    private List<ServerVmNetworkParam> oldNetworkList;

    private Boolean onlyModifyName;
}
