package com.hnkylin.cloud.selfservice.entity.mc.req;

import com.hnkylin.cloud.core.annotation.FieldCheck;
import lombok.Data;

@Data
public class QueryOperateSystem {

    @FieldCheck(notNull = true, notNullMessage = "架构不能为空")
    private String plateformtype;

    private Integer clusterId;
}
