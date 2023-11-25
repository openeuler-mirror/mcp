package com.hnkylin.cloud.manage.entity.resp.cluster;

import lombok.Data;

@Data
public class ClusterNodeInfoDto {


    private String httpType;

    private String ipAddress;

    private Integer port;


}


