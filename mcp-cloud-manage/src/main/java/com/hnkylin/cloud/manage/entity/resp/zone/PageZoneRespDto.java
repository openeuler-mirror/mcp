package com.hnkylin.cloud.manage.entity.resp.zone;

import com.hnkylin.cloud.core.enums.CloudClusterType;
import com.hnkylin.cloud.manage.enums.ClusterStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class PageZoneRespDto {

    //可用区ID
    private Integer zoneId;

    //可用区名称
    private String name;

    //可用区描述
    private String remark;


    //集群类型
    private CloudClusterType type;

    private String clusterNames = "--";

    //总cpu
    private Integer cpuTotal = 0;


    //总内存
    private Integer memTotal = 0;


    //总存储
    private Integer storageTotal = 0;


}
