package com.hnkylin.cloud.manage.entity.resp.index;


import lombok.Data;

/**
 * 首页统计数据
 */
@Data
public class IndexPhysicalHostData {

    //总物理主机数
    private Integer totalPhysicalHost = 0;

    //在线物理主机数
    private Integer onlinePhysicalHost = 0;


    //离线物理主机数
    private Integer offlinePhysicalHost = 0;
}
