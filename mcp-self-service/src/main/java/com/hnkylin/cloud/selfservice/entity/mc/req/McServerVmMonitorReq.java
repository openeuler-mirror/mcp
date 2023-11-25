package com.hnkylin.cloud.selfservice.entity.mc.req;

import lombok.Data;

/**
 * Created by kylin-ksvd on 21-7-15.
 */
@Data
public class McServerVmMonitorReq {

    private String uuid;

    private Integer count = 12;

    private Integer timeUnit = 1;
}
