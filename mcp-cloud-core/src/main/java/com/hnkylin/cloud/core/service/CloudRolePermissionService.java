package com.hnkylin.cloud.core.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hnkylin.cloud.core.domain.CloudRolePermissionDo;

import java.util.Date;
import java.util.List;


public interface CloudRolePermissionService extends IService<CloudRolePermissionDo> {

    /**
     * 根据角色ID查询角色具有的权限列表
     *
     * @param roleId
     * @return
     */
    List<CloudRolePermissionDo> listRolePermissionByRoleId(Integer roleId);

    /**
     * 根据角色删除角色已有权限
     *
     * @param roleId
     * @param deleteBy
     * @param deleteTime
     */
    void deleteRolePermissionByRole(Integer roleId, Integer deleteBy, Date deleteTime);


    /**
     * 插入加上拥有的权限
     *
     * @param roleId
     * @param permissionIds
     * @param createBy
     * @param createTime
     */
    void insertRolePermission(Integer roleId, List<Integer> permissionIds, Integer createBy, Date createTime);


}
