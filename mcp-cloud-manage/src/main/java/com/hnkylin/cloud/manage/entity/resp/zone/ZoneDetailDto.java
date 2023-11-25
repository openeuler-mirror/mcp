package com.hnkylin.cloud.manage.entity.resp.zone;

import com.hnkylin.cloud.core.enums.CloudClusterType;
import lombok.Data;

@Data
public class ZoneDetailDto {

    //可用区ID
    private Integer zoneId;

    //可用区名称
    private String name;

    //可用区描述
    private String remark;


    //资源类型
    private CloudClusterType type;

    //创建时间
    private String createTime;

    //总cpu
    private Integer cpuTotal = 0;
    //已使用cpu
    private Integer cpuUsed = 0;


    //总内存
    private Integer memTotal = 0;
    //已使用内存
    private Integer memUsed = 0;


    //总存储
    private Integer storageTotal = 0;
    //已使用存储
    private Integer storageUsed = 0;


    //集群个数
    private Integer clusterTotal = 0;
    //在线集群个数
    private Integer clusterOnline = 0;
    //离线集群个数
    private Integer clusterOffline = 0;


    //物理主机个数
    private Integer physicalHostTotal = 0;
    //在线物理主机个数
    private Integer physicalHostOnline = 0;
    //离线物理主机个数
    private Integer physicalHostOffline = 0;


    //总云服务器个数
    private Integer machineTotal = 0;
    //在线云服务器
    private Integer machineOnline = 0;
    //离线云服务器
    private Integer machineOffline = 0;


}
