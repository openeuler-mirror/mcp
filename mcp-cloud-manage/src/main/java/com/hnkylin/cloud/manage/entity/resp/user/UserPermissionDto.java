package com.hnkylin.cloud.manage.entity.resp.user;


import lombok.Data;

@Data
public class UserPermissionDto {
    private Integer permissionId;

    private String icon;

    private String routeKey;


    private Integer parentId;

    private String parentIcon;

    private String parentRouteKey;

}
