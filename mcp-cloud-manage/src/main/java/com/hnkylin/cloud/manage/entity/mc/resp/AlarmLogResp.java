package com.hnkylin.cloud.manage.entity.mc.resp;

import lombok.Data;

@Data
public class AlarmLogResp {


    private String severity;

    private String objectName;

    private String info;

    private String objectType;


    private String date;

    private String type;
}
