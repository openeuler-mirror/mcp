package com.hnkylin.cloud.selfservice.entity.resp;

import com.hnkylin.cloud.core.enums.WorkOrderStatus;
import com.hnkylin.cloud.core.enums.WorkOrderType;
import lombok.Data;

@Data
public class BaseWorkOrderDetailDto {

    //工单类型
    private WorkOrderType workOrderType;

    private String workOrderTypeDesc;

    //工单对象
    private String workOrderTarget;

    //工单状态
    private WorkOrderStatus status;

    private String statusDesc;

    //申请时间
    private String applyTime;

    //申请原因
    private String applyReason;


    //审核意见
    private String auditOpinion;

    //审核时间
    private String auditionTime;

    //审核者
    private String auditionUser;

    //用户Id
    private Integer userId;
}
