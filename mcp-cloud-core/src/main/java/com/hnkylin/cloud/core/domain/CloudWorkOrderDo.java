package com.hnkylin.cloud.core.domain;


import com.baomidou.mybatisplus.annotation.TableName;
import com.hnkylin.cloud.core.enums.WorkOrderStatus;
import com.hnkylin.cloud.core.enums.WorkOrderType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName("cloud_work_order")
public class CloudWorkOrderDo extends BaseDo {


    private Integer userId;

    private WorkOrderType type;

    private String target;

    private String applyReason;

    private WorkOrderStatus status;

    private Integer auditBy;

    private Date auditTime;

    private String auditOpinion;


}
