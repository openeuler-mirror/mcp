package com.hnkylin.cloud.manage.entity.mc.resp;

import lombok.Data;

import java.sql.Timestamp;
import java.util.Date;

@Data
public class McClusterResp {


    private String serverAddr;

    private String serverArch;

    private String serverType;

    private String cpuUtil;

    private String memUtil;

    private Integer currentSessions;

    private String loadaverage;

    private String serverId;

    private String cpuModelName;


}
