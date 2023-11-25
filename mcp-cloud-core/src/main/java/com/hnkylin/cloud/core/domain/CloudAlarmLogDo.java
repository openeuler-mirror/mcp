package com.hnkylin.cloud.core.domain;


import com.baomidou.mybatisplus.annotation.TableName;
import com.hnkylin.cloud.core.enums.AlarmLevel;
import com.hnkylin.cloud.core.enums.AlarmResourceType;
import com.hnkylin.cloud.core.enums.AlarmTargetType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName("cloud_alarm_log")
public class CloudAlarmLogDo extends BaseDo {


    private AlarmResourceType resourceType;

    private AlarmLevel alarmLevel;

    private String alarmTarget;

    private AlarmTargetType targetType;

    private String alarmDetail;

    private Integer alarmTargetId;

}
