package com.hnkylin.cloud.selfservice.entity.resp;

import com.hnkylin.cloud.core.enums.WorkOrderStatus;
import com.hnkylin.cloud.core.enums.WorkOrderType;
import lombok.Data;

@Data
public class PageWorkOrderRespDto {

    private Integer workOrderId;

    private WorkOrderType type;

    private String typeDesc;

    private String target;

    private WorkOrderStatus status;

    private String statusDesc;

    private String applyReason;

    private String auditOpinion;

    private String applyTime;


}
