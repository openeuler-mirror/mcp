package com.hnkylin.cloud.manage.entity.mc.resp;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class KcpServerVmAlarmEventResp {


    private String severity;

    private String objectType;

    private String objectName;

    private String info;

    private String date;

    private String type;

}
