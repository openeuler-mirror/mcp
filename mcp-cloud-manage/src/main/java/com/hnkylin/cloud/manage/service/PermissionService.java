package com.hnkylin.cloud.manage.service;


import com.hnkylin.cloud.core.domain.CloudPermissionDo;
import com.hnkylin.cloud.manage.entity.resp.role.PermissionTreeDto;

import java.util.List;

public interface PermissionService {

    List<CloudPermissionDo> listPermissionByRoleId(Integer roleId);


    /**
     * 获取自定义平台管理最大权限树
     */
    List<PermissionTreeDto> customPlatformRoleMaxPermission();


    /**
     * 根据权限路由查询权限
     *
     * @param routeKey
     * @return
     */
    CloudPermissionDo getPermissionByRouteKey(String routeKey);


}
