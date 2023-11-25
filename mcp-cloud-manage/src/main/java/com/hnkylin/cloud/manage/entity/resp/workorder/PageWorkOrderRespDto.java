package com.hnkylin.cloud.manage.entity.resp.workorder;

import com.hnkylin.cloud.core.enums.WorkOrderStatus;
import com.hnkylin.cloud.core.enums.WorkOrderType;
import lombok.Data;

@Data
public class PageWorkOrderRespDto {

    private String applyUser;

//    private String organizationName;

    private Integer workOrderId;

    private WorkOrderType type;

    private String typeDesc;

    private String target;

    private WorkOrderStatus status;

    private String statusDesc;

    private String applyReason;

    private String auditOpinion;

    private String applyTime;


    private String auditTime;

    private Integer applyUserId;

    private String userName;


}
