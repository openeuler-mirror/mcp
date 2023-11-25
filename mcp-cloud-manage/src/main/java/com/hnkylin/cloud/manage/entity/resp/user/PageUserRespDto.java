package com.hnkylin.cloud.manage.entity.resp.user;

import com.hnkylin.cloud.core.enums.CloudUserStatus;
import com.hnkylin.cloud.core.enums.RoleType;
import lombok.Data;

@Data
public class PageUserRespDto {

    //用户ID
    private Integer userId;

    //用户名称
    private String userName;

    //角色名称
    private String roleName;

    private String realName;

    //角色类型
    private RoleType roleType;

    //手机号
    private String mobile;

    //状态
    private CloudUserStatus status;

    //创建时间
    private String createTime;


    private Boolean canModifyUser = true;

    private Boolean canDeleteUser = true;

    private Boolean defaultUser = false;


}
