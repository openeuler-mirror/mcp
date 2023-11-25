package com.hnkylin.cloud.manage.entity.mc.req;

import lombok.Data;

/**
 * Created by kylin-ksvd on 21-6-25.
 */
@Data
public class McServerVirtualizationEventParamReq {


    private Integer page;

    private Integer rows;

    private String sidx;

    private String sord;

    private String startDate;

    private String endDate;

    private String search;

    private String serverType;

    private String severity;

    private String eventType;


}
