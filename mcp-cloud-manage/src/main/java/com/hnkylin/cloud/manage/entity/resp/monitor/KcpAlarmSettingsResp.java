package com.hnkylin.cloud.manage.entity.resp.monitor;

import com.hnkylin.cloud.core.enums.AlarmResourceType;
import com.hnkylin.cloud.core.enums.ArchitectureResourceType;
import lombok.Data;

@Data
public class KcpAlarmSettingsResp {

    private Integer kcpSettingId;

    private AlarmResourceType resourceType;

    private Integer generalAlarm;

    private Integer severityAlarm;

    private Integer urgentAlarm;

    //private Integer durationTime;


}
