package com.hnkylin.cloud.core.entity.resp.operateLog;

import com.hnkylin.cloud.core.enums.OperateLogAction;
import com.hnkylin.cloud.core.enums.OperateLogStatus;
import com.hnkylin.cloud.core.enums.OperateLogType;
import lombok.Data;

@Data
public class OperateLogResp {
    //日志ID
    private Integer operateLogId;
    //类型
    private OperateLogType type;

    //动作
    private OperateLogAction action;


    //类型
    private String typeDesc;

    //动作
    private String actionDesc;


    //状态
    private OperateLogStatus status;

    //执行进度
    private String percent;

    //对象id
    private Integer objId;

    //对象名称
    private String objName;

    //操作详情
    private String detail;

    //操作结果
    private String result;

    private String startTime;

    //结束时间
    private String endTime;

    //是否有子任务
    private boolean hasChildOperateLog;

    private String operateUser;

    private String orgName;

    private String zoneName;

    private String clientIp;
}
