package com.hnkylin.cloud.manage.entity.resp.index;


import lombok.Data;

/**
 * 首页VDC各资源使用情况
 */
@Data
public class OrgLeaderVdcUsedData {

    //总资源
    private Integer total = 0;

    //分配下级资源
    private Integer allocateChild = 0;

    //本级已使用
    private Integer sameUsed = 0;

    //剩余可用
    private Integer usable = 0;

    private String unit;


}
