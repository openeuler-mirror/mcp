package com.hnkylin.cloud.manage.entity.mc.req;

import com.hnkylin.cloud.core.enums.McAlarmSettingsType;
import lombok.Data;

/**
 * Created by kylin-ksvd on 21-6-25.
 */
@Data
public class QueryMcAlarmSettingsParam {


    private Integer clusterId;

    private McAlarmSettingsType alarmSettingType;


}
