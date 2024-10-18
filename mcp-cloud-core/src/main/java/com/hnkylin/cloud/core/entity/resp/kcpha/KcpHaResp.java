package com.hnkylin.cloud.core.entity.resp.kcpha;

import lombok.Data;

@Data
public class KcpHaResp {

    private String nodeRole;

    private String masterIp;

    private String slaveIp;

    private String status;

}
