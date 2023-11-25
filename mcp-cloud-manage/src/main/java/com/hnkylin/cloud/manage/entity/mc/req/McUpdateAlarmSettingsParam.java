package com.hnkylin.cloud.manage.entity.mc.req;

import lombok.Data;

/**
 * Created by kylin-ksvd on 21-6-25.
 */
@Data
public class McUpdateAlarmSettingsParam {


    private Integer clusterId;

    private Integer alarmSettingId;


    private String urgentValue;

    private String ordinaryValue;

    private String seriousValue;

    private String oper = "edit";


}
