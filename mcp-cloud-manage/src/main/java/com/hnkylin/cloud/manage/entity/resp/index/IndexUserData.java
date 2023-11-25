package com.hnkylin.cloud.manage.entity.resp.index;


import lombok.Data;

/**
 * 首页统计数据
 */
@Data
public class IndexUserData {

    //平台管理用户
    private Integer platformManageUserCount = 0;

    //组织管理员账号
    private Integer orgManageUserCount = 0;

    //自服务用户
    private Integer selfServiceUserCount = 0;
}
