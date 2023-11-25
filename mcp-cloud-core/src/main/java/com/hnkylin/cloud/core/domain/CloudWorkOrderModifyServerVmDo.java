package com.hnkylin.cloud.core.domain;


import com.baomidou.mybatisplus.annotation.TableName;
import com.hnkylin.cloud.core.enums.MemUnit;
import com.hnkylin.cloud.core.enums.ServerVmDeadlineType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName("cloud_work_order_modify_servervm")
public class CloudWorkOrderModifyServerVmDo extends BaseDo {


    private Integer workOrderId;

    //虚拟机名称
    private String servervmName;

    //云服务器uuid
    private String machineUuid;

    //到期处理策略
    private ServerVmDeadlineType deadlineType;
    //操作系统
    private String osMachine;
    //架构
    private String architecture;

    private String systemType;

    //cpu
    private Integer cpu;

    //变更前cpu
    private Integer originalCpu;

    //内存
    private Integer mem;

    //变更前内存
    private Integer originalMem;

    //内存单位
    private MemUnit memUnit;

    private Date deadlineTime;


}
