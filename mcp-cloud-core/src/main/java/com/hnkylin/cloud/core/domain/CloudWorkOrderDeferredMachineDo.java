package com.hnkylin.cloud.core.domain;


import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName("cloud_work_order_deferred_machine")
public class CloudWorkOrderDeferredMachineDo extends BaseDo {


    private Integer workOrderId;


    private String userMachineUuid;

    private Date deadlineTime;

    private Date oldDeadlineTime;


}
