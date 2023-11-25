package com.hnkylin.cloud.selfservice.entity.req;

import com.hnkylin.cloud.core.annotation.FieldCheck;
import com.hnkylin.cloud.core.enums.ApplyServerVmType;
import com.hnkylin.cloud.core.enums.ServerVmDeadlineType;
import lombok.Data;

import java.util.List;

@Data
public class ApplyServerVmParam {


    private ApplyServerVmType applyServerVmType = ApplyServerVmType.TEMPLATE;


    //模板ID
    @FieldCheck(notNull = true, notNullMessage = "模板不能为空")
    private Integer templateId;

    //操作系统
    private String osMachine;

    //架构
    private String architecture;

    //系统类型
    private String systemType;


    //虚拟机名称
    @FieldCheck(notNull = true, notNullMessage = "虚拟机名称不能为空")
    private String servervmName;

    //申请数量
    @FieldCheck(notNull = true, notNullMessage = "申请数量不能为空")
    private Integer applyNum;

    //使用月数
    private Integer useMonth;

    //到期处理策略
    private ServerVmDeadlineType deadlineType;

    //cpu
    @FieldCheck(notNull = true, notNullMessage = "cpu不能为空")
    private Integer cpu;
    //内存
    @FieldCheck(notNull = true, notNullMessage = "内存不能为空")
    private Integer mem;

    //申请原因
    @FieldCheck(notNull = true, notNullMessage = "申请原因不能为空")
    private String applyReason;


    private String description;


    //已选择的iso文件
    private List<String> isoList;

    //硬盘信息
    private List<ServerVmDiskParam> diskList;


    //网卡信息
    private List<ServerVmNetworkParam> networkList;

    private Integer clusterId;


}
