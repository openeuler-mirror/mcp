package com.hnkylin.cloud.manage.entity.req.operateLog;

import com.hnkylin.cloud.core.common.BasePageParam;
import lombok.Data;

@Data
public class PageOperateLogParam extends BasePageParam {


    private String type;


    private String action;

    private String status;


    private String startDate;


    private String endDate;


}
