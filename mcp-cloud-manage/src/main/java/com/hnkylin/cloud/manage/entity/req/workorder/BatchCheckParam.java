package com.hnkylin.cloud.manage.entity.req.workorder;


import com.hnkylin.cloud.core.annotation.FieldCheck;
import com.hnkylin.cloud.core.enums.WorkOrderStatus;
import lombok.Data;

import java.util.List;

@Data
public class BatchCheckParam {

    @FieldCheck(notNull = true, notNullMessage = "工单ID不能为空")
    private List<Integer> workOrderIds;

    @FieldCheck(notNull = true, notNullMessage = "审核状态不能为空")
    private WorkOrderStatus checkStatus;


    @FieldCheck(notNull = true, notNullMessage = "审核意见不能为空")
    private String auditOpinion;

}
