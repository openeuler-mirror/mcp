package com.hnkylin.cloud.manage.entity.req.monitor;

import com.hnkylin.cloud.core.annotation.FieldCheck;
import lombok.Data;

@Data
public class UpdateKcpAlarmSettingParam {

    private Integer kcpSettingId;
    @FieldCheck(notNull = true, notNullMessage = "一般告警不能为空")
    private Integer generalAlarm;
    @FieldCheck(notNull = true, notNullMessage = "严重告警不能为空")
    private Integer severityAlarm;
    @FieldCheck(notNull = true, notNullMessage = "紧急告警不能为空")
    private Integer urgentAlarm;

    //private Integer durationTime;


}
