package com.hnkylin.cloud.manage.service;

import com.hnkylin.cloud.core.common.PageData;
import com.hnkylin.cloud.core.domain.CloudUserDo;
import com.hnkylin.cloud.core.enums.CloudUserStatus;
import com.hnkylin.cloud.manage.entity.LoginUserVo;
import com.hnkylin.cloud.manage.entity.req.user.*;
import com.hnkylin.cloud.manage.entity.req.workorder.LoginUserParam;
import com.hnkylin.cloud.manage.entity.resp.user.ModifyUserInfoRespDto;
import com.hnkylin.cloud.manage.entity.resp.user.PageUserRespDto;
import com.hnkylin.cloud.manage.entity.resp.TokenRespDto;
import com.hnkylin.cloud.manage.entity.resp.user.UserInfoRespDto;

import java.util.List;

/**
 * Created by kylin-ksvd on 21-6-22.
 */
public interface UserService {

    /**
     * 登录
     */
    TokenRespDto login(LoginUserParam loginUserParam);

    /**
     * 用户信息
     */
    UserInfoRespDto userInfo(LoginUserVo loginUserVo);

    /**
     * 登出
     */
    void loginOut(LoginUserVo loginUserVo);

    /**
     * 重置密码
     *
     * @param loginUserVo
     * @param resetPasswordParam
     */
    void resetPassword(LoginUserVo loginUserVo, ResetPasswordParam resetPasswordParam);

    /**
     * 根据组织ID统计用户数量(不包含子组织)
     */
    Integer statisticOrgUserNumByOrgId(Integer orgId);

    /**
     * 根据组织ID统计用户数量(包含子组织)
     */
    Integer statisticOrgUserNumByOrgIdList(List<Integer> orgIdList);


    /**
     * 创建用户
     */
    void createUser(CreateUserParam createUserParam, LoginUserVo loginUserVo);

    /**
     * 编辑用户
     */
    void modifyUser(ModifyUserParam modifyUserParam, LoginUserVo loginUserVo);

    /**
     * 获取编辑用户详情
     *
     * @param baseUserParam
     * @return
     */
    ModifyUserInfoRespDto modifyUserInfo(BaseUserParam baseUserParam);

    /**
     * 删除用户
     *
     * @param baseUserParam
     * @param loginUserVo
     */
    void deleteUser(BaseUserParam baseUserParam, LoginUserVo loginUserVo);


    /**
     * 批量删除用户
     */
    void batchDeleteUser(BatchDeleteUserParam batchDeleteUserParam, LoginUserVo loginUserVo);

    /**
     * 分页获取用户列表
     *
     * @param pageUserParam
     * @return
     */
    PageData<PageUserRespDto> pageUser(PageUserParam pageUserParam, LoginUserVo loginUserVo);


//    /**
//     * 判断用户是否拥有最高权限
//     * 如果是系统管理员及顶级组织的组织管理员
//     *
//     * @param userId
//     * @return
//     */
//    boolean judgeUserHasTopPermission(Integer userId);

    /**
     * 判断用户是否是平台管理用户
     *
     * @param userId
     * @return
     */
    boolean judgeIfPlatformUser(Integer userId);


    /**
     * 根据组织ID获取用户列表
     *
     * @param orgId
     * @return
     */
    List<CloudUserDo> listUserByOrgId(Integer orgId);

    /**
     * 判断用户是否具有管理用户权限
     *
     * @param userId
     * @return
     */
    boolean userHasManageUserPermission(Integer userId);


    /**
     * 判断用户是否拥有routekey的权限
     *
     * @param userId
     * @param routeKey
     * @return
     */
    boolean judgeUserHasRouteKeyPermission(Integer userId, String routeKey);


    /**
     * 根据组织ID获取用户列表(包含下级组织ID)
     *
     * @param orgId
     * @return
     */
    List<CloudUserDo> listUserByOrgIdIncludeChildOrg(Integer orgId);


    /**
     * 根据组织ID获取用户列表
     *
     * @param orgIdList
     * @return
     */
    List<CloudUserDo> listUserByOrgList(List<Integer> orgIdList, CloudUserStatus userStatus);


    /**
     * 根据用户id获取该用户可见的用户列表
     *
     * @param userId
     * @return
     */
    List<CloudUserDo> userVisibleUserList(Integer userId);


    /**
     * 获取组织自服务器用户数
     *
     * @param orgIdList
     * @return
     */
    Integer countSelfServiceUserByOrgList(List<Integer> orgIdList);


    /**
     * 获取组织管理员
     *
     * @param orgId
     * @return
     */
    CloudUserDo orgLeaderUser(Integer orgId);

}
