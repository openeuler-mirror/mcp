package com.hnkylin.cloud.manage.entity.req.workorder;


import com.hnkylin.cloud.core.annotation.FieldCheck;
import com.hnkylin.cloud.core.enums.WorkOrderStatus;
import lombok.Data;

@Data
public class CommonCheckParam {

    @FieldCheck(notNull = true, notNullMessage = "工单ID不能为空")
    private Integer workOrderId;


    @FieldCheck(notNull = true, notNullMessage = "审核意见不能为空")
    private String auditOpinion;

}
