package com.hnkylin.cloud.core.domain;


import com.baomidou.mybatisplus.annotation.TableName;
import com.hnkylin.cloud.core.enums.ApplyServerVmType;
import com.hnkylin.cloud.core.enums.McCloneType;
import com.hnkylin.cloud.core.enums.MemUnit;
import com.hnkylin.cloud.core.enums.ServerVmDeadlineType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName("cloud_work_order_servervm")
public class CloudWorkOrderServerVmDo extends BaseDo {


    private Integer workOrderId;

    private ApplyServerVmType applyServervmType;

    //克隆类型
    private McCloneType cloneType;

    //虚拟机名称
    private String servervmName;
    //申请个数
    private Integer applyNum;


    //变更后个数
    private Integer modifyApplyNum;


    //使用月数
    private Integer useMonth;
    //到期处理策略
    private ServerVmDeadlineType deadlineType;
    //操作系统
    private String osMachine;
    //架构
    private String architecture;

    private String systemType;
    //模板ID
    private Integer templateId;

    //集群ID
    private Integer clusterId;

    //cpu
    private Integer cpu;

    //变更后CPU
    private Integer modifyCpu;
    //内存
    private Integer mem;

    //变更后内存
    private Integer modifyMem;
    //内存单位
    private MemUnit memUnit;

    private String description;


}
