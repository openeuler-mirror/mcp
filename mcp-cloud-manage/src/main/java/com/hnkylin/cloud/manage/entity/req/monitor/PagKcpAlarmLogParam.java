package com.hnkylin.cloud.manage.entity.req.monitor;

import com.hnkylin.cloud.core.common.BasePageParam;
import lombok.Data;

@Data
public class PagKcpAlarmLogParam extends BasePageParam {


    private String startDate;

    private String endDate;

    private Integer zoneId = 0;

    private Integer orgId = 0;


}
