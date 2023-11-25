package com.hnkylin.cloud.core.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hnkylin.cloud.core.domain.CloudPermissionDo;

import java.util.List;


public interface CloudPermissionService extends IService<CloudPermissionDo> {


    /**
     * 所有权限
     *
     * @return
     */
    List<CloudPermissionDo> allPermissions();


    /**
     * 获取自定义的平台管理角色默认最大权限集合
     *
     * @return
     */
    List<CloudPermissionDo> customPlatformRoleMaxPermission();

}
