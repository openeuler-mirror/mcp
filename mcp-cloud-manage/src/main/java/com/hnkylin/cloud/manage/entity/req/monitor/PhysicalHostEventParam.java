package com.hnkylin.cloud.manage.entity.req.monitor;

import com.hnkylin.cloud.core.common.BasePageParam;
import lombok.Data;

@Data
public class PhysicalHostEventParam extends BasePageParam {

    private Integer clusterId;

    private String sidx;

    private String sord;

    private String startDate;

    private String endDate;

    private String search;

    private String severity;

    private String eventType;


}
