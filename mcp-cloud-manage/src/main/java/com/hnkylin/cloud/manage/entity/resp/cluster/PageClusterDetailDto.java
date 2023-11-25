package com.hnkylin.cloud.manage.entity.resp.cluster;

import com.hnkylin.cloud.core.enums.CloudClusterType;
import com.hnkylin.cloud.core.enums.RoleType;
import com.hnkylin.cloud.manage.enums.ClusterStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class PageClusterDetailDto {

    //集群ID
    private Integer clusterId;

    //集群名称
    private String name;

    //描述信息
    private String remark;

    //可用区ID
    private Integer zoneId = 1;

    //可用区名称
    private String zoneName = "";

    //集群URL
    private String clusterUrl;


    //集群类型
    private CloudClusterType type;

    private ClusterStatus status = ClusterStatus.OFFLINE;

    //cpu已使用
    private BigDecimal cpuUsed;
    //cpu总大小
    private BigDecimal cpuTotal;
    //内存已使用
    private BigDecimal memUsed;
    //内存总大小
    private BigDecimal memTotal;
    //存储已使用
    private BigDecimal storageUsed;
    //存储总大小
    private BigDecimal storageTotal;

    private String quickLoginUrl;


    private List<String> mcNodeList;

    private boolean selected = false;


}
