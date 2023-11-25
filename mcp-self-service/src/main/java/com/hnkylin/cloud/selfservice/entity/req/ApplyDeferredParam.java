package com.hnkylin.cloud.selfservice.entity.req;

import com.hnkylin.cloud.core.annotation.FieldCheck;
import lombok.Data;

@Data
public class ApplyDeferredParam {

    @FieldCheck(notNull = true, notNullMessage = "用户服务器uuid不能为空")
    private String userMachineUuid;

    @FieldCheck(notNull = true, notNullMessage = "申请原因不能为空")
    private String applyReason;

    private String machineName;

    @FieldCheck(notNull = true, notNullMessage = "延期时间不能为空")
    private String deadlineTime;


}
