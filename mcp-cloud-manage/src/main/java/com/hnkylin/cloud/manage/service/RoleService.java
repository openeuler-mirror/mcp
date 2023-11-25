package com.hnkylin.cloud.manage.service;

import com.hnkylin.cloud.core.common.PageData;
import com.hnkylin.cloud.core.domain.CloudRoleDo;
import com.hnkylin.cloud.manage.entity.LoginUserVo;
import com.hnkylin.cloud.manage.entity.req.org.BaseOrgParam;
import com.hnkylin.cloud.manage.entity.req.role.*;
import com.hnkylin.cloud.manage.entity.resp.role.CommonRoleDto;
import com.hnkylin.cloud.manage.entity.resp.role.PageRoleRespDto;
import com.hnkylin.cloud.manage.entity.resp.role.RoleDetailDto;
import com.hnkylin.cloud.manage.entity.resp.role.RoleInfoRespDto;

import java.util.List;

public interface RoleService {


    /**
     * 创建平台管理角色
     *
     * @param createRoleParam
     * @param loginUserVo
     */
    void createPlatformRole(CreateRoleParam createRoleParam, LoginUserVo loginUserVo);


    /**
     * 删除角色
     *
     * @param baseRoleParam
     * @param loginUserVo
     */
    void deleteRole(BaseRoleParam baseRoleParam, LoginUserVo loginUserVo);


    /**
     * 编辑角色
     *
     * @param modifyRoleParam
     * @param loginUserVo
     */
    void modifyRole(ModifyRoleParam modifyRoleParam, LoginUserVo loginUserVo);


    /**
     * 编辑角色时获取详情
     */
    RoleDetailDto modifyRoleDetail(BaseRoleParam baseRoleParam);

    /**
     * 角色详情
     */
    RoleInfoRespDto roleInfo(BaseRoleParam baseRoleParam);


    /**
     * 分页获取角色列表
     */
    PageData<PageRoleRespDto> pageRole(RolePageParam rolePageParam);


    /**
     * 角色列表
     *
     * @param loginUserVo
     * @return
     */
    List<CommonRoleDto> listRole(SearchRoleParam searchRoleParam, LoginUserVo loginUserVo);


    /**
     * 获取模板的自服务角色
     *
     * @return
     */
    CloudRoleDo getSelfServiceRole();

    /**
     * 分配角色权限
     *
     * @param allocateRolePermissionParam
     * @param loginUserVo
     */
    void allocateRolePermission(AllocateRolePermissionParam allocateRolePermissionParam, LoginUserVo loginUserVo);


    /**
     * 获取用户的角色
     *
     * @param userId
     * @return
     */
    CloudRoleDo getUserRole(Integer userId);

}
