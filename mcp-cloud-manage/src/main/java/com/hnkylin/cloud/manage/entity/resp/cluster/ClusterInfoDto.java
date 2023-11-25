package com.hnkylin.cloud.manage.entity.resp.cluster;

import com.hnkylin.cloud.core.enums.CloudClusterType;
import com.hnkylin.cloud.manage.entity.mc.resp.McClusterResourceUsedResp;
import com.hnkylin.cloud.manage.enums.ClusterStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class ClusterInfoDto {


    private String name;

    private ClusterStatus status;

    private String remark;

    private CloudClusterType type;

    private String clusterVersion;

    private String clusterUrl;

    private Integer zoneId;

    private String zoneName;

    private String clusterAdminUser;

    private String createTime;


    private McClusterResourceUsedResp resourceUsedInfo;

    private String quickLoginUrl;


}


