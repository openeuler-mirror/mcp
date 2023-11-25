package com.hnkylin.cloud.manage.entity.mc.resp;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class KcpServerVmNetworkResp {


    private String interfaceType;

    private String modeltype;

    private String virtualSwitch;

    private String portGroup;

    private String macAddress;

    private String ipAddress;

    private String pool;

    private String uploadSpend;

    private String downloadSpend;


}
