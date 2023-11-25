package com.hnkylin.cloud.manage.entity.resp.index;


import lombok.Data;

/**
 * 首页统计数据
 */
@Data
public class IndexClusterData {


    //集群总数
    private Integer totalCluster = 0;

    //在线集群数
    private Integer onlineCluster = 0;

    //离线集群数
    private Integer offlineCluster = 0;
}
