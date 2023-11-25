package com.hnkylin.cloud.manage.entity.req.cluster;


import lombok.Data;

@Data
public class ClusterNodeParams {

    private String httpType;

    private String ipAddress;

    private Integer port;


}
