package com.hnkylin.cloud.manage.entity.resp.monitor;

import com.hnkylin.cloud.core.enums.AlarmLevel;
import com.hnkylin.cloud.core.enums.AlarmResourceType;
import com.hnkylin.cloud.core.enums.AlarmTargetType;
import lombok.Data;

@Data
public class PageKcpAlarmLogResp {

    //告警类型
    private AlarmResourceType resourceType;

    //告警等级
    private AlarmLevel alarmLevel;

    //告警对象
    private String alarmTarget;

    //对象类型
    private AlarmTargetType targetType;

    //告警详情
    private String alarmDetail;


    //告警时间
    private String alarmTime;

    //可用区名称
    private String zoneName;

    //组织名称
    private String orgName;

}
