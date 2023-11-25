package com.hnkylin.cloud.manage.entity.resp.index;


import lombok.Data;

/**
 * 首页统计数据
 */
@Data
public class IndexServerVmData {
    //总云服务器数
    private Integer totalServerVm = 0;

    //在线云服务器数
    private Integer onlineServerVm = 0;

    //离线云服务器数
    private Integer offlineServerVm = 0;


}
