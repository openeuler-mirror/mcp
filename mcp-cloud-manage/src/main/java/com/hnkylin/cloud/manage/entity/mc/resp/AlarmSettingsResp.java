package com.hnkylin.cloud.manage.entity.mc.resp;

import lombok.Data;

import java.util.List;

@Data
public class AlarmSettingsResp {

    private Integer alarmSettingId;

    private String type;

    private String urgentValue;

    private String ordinaryValue;

    private String seriousValue;


}
