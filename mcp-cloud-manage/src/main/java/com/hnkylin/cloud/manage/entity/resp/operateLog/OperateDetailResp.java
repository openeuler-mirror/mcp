package com.hnkylin.cloud.manage.entity.resp.operateLog;

import com.hnkylin.cloud.core.enums.OperateLogStatus;
import lombok.Data;

@Data
public class OperateDetailResp {


    private Integer operateLogId;

    private String type;

    private String typeEnum;


    private String action;

    private String actionEnum;

    private String status;

    private OperateLogStatus enumStatus;

    private String objName;

    private String percent;

    private String startTime;

    private String endTime;

    private String operateUser;

    private String clientIp;

    private String orgName;

    private String zoneName;

    //操作详情
    private String detail;

    //操作结果
    private String result;

    private boolean hasChildOperateLog;


}
