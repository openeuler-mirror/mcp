package com.hnkylin.cloud.selfservice.entity.mc.req;

import lombok.Data;


/**
 * Created by kylin-ksvd on 21-7-9.
 */
@Data
public class ServerVmOperateLogReq {

    private String sidx = "startTime";


    private String sord = "desc";
    private Integer page;

    private Integer rows;

    private String uuid;
}
