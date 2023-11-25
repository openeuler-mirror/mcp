package com.hnkylin.cloud.manage.entity.resp.cluster;

import com.hnkylin.cloud.core.enums.CloudClusterType;
import lombok.Data;

import java.util.List;

@Data
public class ClusterDetailDto {


    private Integer clusterId;

    private String name;


    private String remark;

    private CloudClusterType type;


    private String clusterAdminName;


    private String clusterAdminPassword;

    private List<ClusterNodeInfoDto> clusterNodeList;


}


