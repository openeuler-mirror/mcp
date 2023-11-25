package com.hnkylin.cloud.manage.entity.req.workorder;

import com.hnkylin.cloud.core.annotation.FieldCheck;
import lombok.Data;

/**
 * Created by kylin-ksvd on 21-6-24.
 */
@Data
public class ServerVmNetworkParam {

    @FieldCheck(notNull = true, notNullMessage = "工单ID不能为空", minNum = 1, minNumMessage = "工单ID不能为空")
    private Integer workOrderId;

}
