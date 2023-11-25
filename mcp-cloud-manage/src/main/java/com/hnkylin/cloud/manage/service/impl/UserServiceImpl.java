package com.hnkylin.cloud.manage.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.hnkylin.cloud.core.common.JwtUtil;
import com.hnkylin.cloud.core.common.PageData;
import com.hnkylin.cloud.core.config.exception.KylinException;
import com.hnkylin.cloud.core.domain.*;
import com.hnkylin.cloud.core.enums.CloudUserStatus;
import com.hnkylin.cloud.core.enums.RoleType;
import com.hnkylin.cloud.core.enums.UserType;
import com.hnkylin.cloud.core.service.*;
import com.hnkylin.cloud.manage.constant.CloudManageRedisConstant;
import com.hnkylin.cloud.manage.constant.KylinCloudManageConstants;
import com.hnkylin.cloud.manage.constant.KylinHttpResponseUserConstants;
import com.hnkylin.cloud.manage.entity.LoginUserVo;
import com.hnkylin.cloud.manage.entity.req.user.*;
import com.hnkylin.cloud.manage.entity.req.workorder.LoginUserParam;
import com.hnkylin.cloud.manage.entity.resp.*;
import com.hnkylin.cloud.manage.entity.resp.user.*;
import com.hnkylin.cloud.manage.mapper.PermissionMapper;
import com.hnkylin.cloud.manage.mapper.RoleMapper;
import com.hnkylin.cloud.manage.mapper.UserMachineMapper;
import com.hnkylin.cloud.manage.mapper.UserMapper;
import com.hnkylin.cloud.manage.service.*;
import com.hnkylin.cloud.manage.service.cache.LoginUserCacheService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Created by kylin-ksvd on 21-6-22.
 */
@Service
public class UserServiceImpl implements UserService {
    @Resource
    private CloudUserService cloudUserService;

    @Resource
    private LoginUserCacheService loginUserCacheService;

    @Resource
    private CloudOrganizationService cloudOrganizationService;

    @Resource
    private PermissionMapper permissionMapper;

    @Resource
    private CloudUserRoleService cloudUserRoleService;

    @Resource
    private CloudRoleService cloudRoleService;

    @Resource
    private UserMachineService userMachineService;

    @Resource
    private UserMachineMapper userMachineMapper;

    @Resource
    private UserMapper userMapper;

    @Resource
    private RoleService roleService;

    @Resource
    private OrgService orgService;

    @Resource
    private RoleMapper roleMapper;

    @Resource
    private PermissionService permissionService;

    @Resource
    private CloudRolePermissionService cloudRolePermissionService;

    @Override
    public TokenRespDto login(LoginUserParam loginUserParam) {
        CloudUserDo cloudUserDo = cloudUserService.queryUserByUserName(loginUserParam.getUserName());
        if (Objects.isNull(cloudUserDo)) {
            throw new KylinException(KylinHttpResponseUserConstants.NOT_EXIST_USER);
        }
        if (!Objects.equals(loginUserParam.getPassword(), cloudUserDo.getPassword())) {
            throw new KylinException(KylinHttpResponseUserConstants.PASSWORD_ERR);
        }
        if (Objects.equals(cloudUserDo.getStatus(), CloudUserStatus.NO_ACTIVATE)) {
            throw new KylinException(KylinHttpResponseUserConstants.USER_NOT_ACTIVATE);
        }
        if (Objects.equals(cloudUserDo.getUserType(), UserType.selfServiceUser)) {
            throw new KylinException(KylinHttpResponseUserConstants.SELF_SERVICE_USER_NOT_LOGIN);
        }
        //登录成功，生成token
        String token =
                JwtUtil.sign(cloudUserDo.getUserName(), cloudUserDo.getId().toString(), KylinCloudManageConstants
                        .KYLIN_TOKEN_SECRET);

        //同一用户互斥登录
        Object cacheToken = loginUserCacheService.vGet(CloudManageRedisConstant.UID + cloudUserDo.getId());
        if (Objects.nonNull(cacheToken)) {
            loginUserCacheService.vDelete(CloudManageRedisConstant.UID + cloudUserDo.getId());
            loginUserCacheService.vDelete(cacheToken.toString());
        }

        //设置token缓存有效时间
        loginUserCacheService.vSet(token, cloudUserDo.getId(), CloudManageRedisConstant
                        .CLOUD_MANAGE_LOGIN_USER_CACHE_EXPIRE,
                TimeUnit.MILLISECONDS);
        loginUserCacheService.vSet(CloudManageRedisConstant.UID + cloudUserDo.getId(), token,
                CloudManageRedisConstant.CLOUD_MANAGE_LOGIN_USER_CACHE_EXPIRE, TimeUnit.MILLISECONDS);


        TokenRespDto tokenRespDto = new TokenRespDto();
        tokenRespDto.setToken(token);
        return tokenRespDto;
    }

    @Override
    public void loginOut(LoginUserVo loginUserVo) {
        Object cacheToken = loginUserCacheService.vGet(CloudManageRedisConstant.UID + loginUserVo.getUserId());
        if (Objects.nonNull(cacheToken)) {
            loginUserCacheService.vDelete(CloudManageRedisConstant.UID + loginUserVo.getUserId());
            loginUserCacheService.vDelete(cacheToken.toString());
        }
    }

    @Override
    public UserInfoRespDto userInfo(LoginUserVo loginUserVo) {
        UserInfoRespDto userInfoRespDto = new UserInfoRespDto();
        CloudUserDo cloudUserDo = cloudUserService.getById(loginUserVo.getUserId());
        userInfoRespDto.setUserName(cloudUserDo.getUserName());
        userInfoRespDto.setRealName(cloudUserDo.getRealName());
        userInfoRespDto.setMobile(cloudUserDo.getMobile());
        userInfoRespDto.setRemark(cloudUserDo.getRemark());
        CloudOrganizationDo cloudOrganizationDo = cloudOrganizationService.getById(cloudUserDo.getOrganizationId());
        userInfoRespDto.setOrganizationName(cloudOrganizationDo.getOrganizationName());
        userInfoRespDto.setOrganizationId(cloudOrganizationDo.getId());
        userInfoRespDto.setUserId(cloudUserDo.getId());
        //获取用户的角色
        CloudUserRoleDo cloudUserRoleDo = cloudUserRoleService.getUserRoleByUserId(loginUserVo.getUserId());

        List<UserPermissionDto> userPermissions =
                permissionMapper.rolePermissionByRoleId(cloudUserRoleDo.getRoleId());
        userInfoRespDto.setPermissions(userPermissions);
        userInfoRespDto.setTopUser(judgeIfPlatformUser(loginUserVo.getUserId()));
        userInfoRespDto.setSuperUser(cloudUserDo.getSuperUser());
        return userInfoRespDto;
    }


    @Override
    public void resetPassword(LoginUserVo loginUserVo, ResetPasswordParam resetPasswordParam) {
        CloudUserDo cloudUserDo = cloudUserService.getById(loginUserVo.getUserId());
        if (!Objects.equals(cloudUserDo.getPassword(), resetPasswordParam.getOldPassword())) {
            throw new KylinException(KylinCloudManageConstants.OLD_PASSWORD_ERR);
        }
        cloudUserDo.setPassword(resetPasswordParam.getNewPassword());
        cloudUserService.updateById(cloudUserDo);
    }

    @Override
    public Integer statisticOrgUserNumByOrgId(Integer orgId) {
        CloudUserDo userDo = new CloudUserDo();
        userDo.setOrganizationId(orgId);
        userDo.setDeleteFlag(false);
        userDo.setStatus(CloudUserStatus.ACTIVATE);
        Wrapper<CloudUserDo> wrapper = new QueryWrapper<>(userDo);
        Integer userCount = cloudUserService.getBaseMapper().selectCount(wrapper);
        return userCount;
    }

    @Override
    public Integer statisticOrgUserNumByOrgIdList(List<Integer> orgIdList) {
        CloudUserDo userDo = new CloudUserDo();
        userDo.setDeleteFlag(false);
        QueryWrapper<CloudUserDo> wrapper = new QueryWrapper<>(userDo);
        wrapper.in("organization_id", orgIdList);
        Integer userCount = cloudUserService.getBaseMapper().selectCount(wrapper);
        return userCount;
    }


    /**
     * 查询真实姓名，组织查询用户
     */
    private CloudUserDo getUserByRealNameAndOrg(Integer orgId, String realName) {
        CloudUserDo userDo = new CloudUserDo();
        userDo.setRealName(realName);
        userDo.setDeleteFlag(false);
        Wrapper<CloudUserDo> wrapper = new QueryWrapper<>(userDo);
        List<CloudUserDo> cloudUserDoList = cloudUserService.getBaseMapper().selectList(wrapper);
        return cloudUserDoList.isEmpty() ? null : cloudUserDoList.get(0);
    }


    @Override
    @Transactional
    public void createUser(CreateUserParam createUserParam, LoginUserVo loginUserVo) {
        //验证用户名是否重复
        CloudUserDo userDo = new CloudUserDo();
        userDo.setUserName(createUserParam.getUserName());
        userDo.setDeleteFlag(false);
        Wrapper<CloudUserDo> wrapper = new QueryWrapper<>(userDo);
        int existUserCount = cloudUserService.getBaseMapper().selectCount(wrapper);
        if (existUserCount > 0) {
            throw new KylinException(KylinHttpResponseUserConstants.EXIST_USER);
        }
        //验证通组织下真实姓名是否重复
        CloudUserDo existUser = getUserByRealNameAndOrg(createUserParam.getOrganizationId(),
                createUserParam.getRealName());
        if (Objects.nonNull(existUser)) {
            throw new KylinException(KylinHttpResponseUserConstants.EXIST_REAI_NAME);
        }
        CloudRoleDo roleDo = cloudRoleService.getById(createUserParam.getRoleId());
        //验证组织管理员是否已经存在
        if (Objects.equals(roleDo.getRoleType(), RoleType.ORG)) {
            CloudUserDo orgLeaderUser = orgLeaderUser(createUserParam.getOrganizationId());
            if (Objects.nonNull(orgLeaderUser)) {
                throw new KylinException(KylinHttpResponseUserConstants.ORG_EXIST_LEADER_USER);
            }
        }
        Date now = new Date();
        CloudUserDo createUserDo = CloudUserDo.builder().
                userName(createUserParam.getUserName()).realName(createUserParam.getRealName())
                .password(createUserParam.getPassword()).mobile(createUserParam.getMobile()).defaultUser(false).superUser(false)
                .remark(createUserParam.getRemark()).status(createUserParam.getStatus()).userType(UserType.cloudUser).
                        organizationId(createUserParam.getOrganizationId())
                .build();

        if (Objects.equals(roleDo.getRoleType(), RoleType.SELF_SERVICE)) {
            createUserDo.setUserType(UserType.selfServiceUser);
        }
        createUserDo.setCreateBy(loginUserVo.getUserId());
        createUserDo.setCreateTime(now);
        cloudUserService.save(createUserDo);

        //插入用户角色关系
        CloudUserRoleDo cloudUserRoleDo = CloudUserRoleDo.builder().userId(createUserDo.getId())
                .roleId(createUserParam.getRoleId()).build();
        cloudUserRoleDo.setCreateBy(loginUserVo.getUserId());
        cloudUserRoleDo.setCreateTime(now);
        cloudUserRoleService.save(cloudUserRoleDo);
    }


    @Override
    @Transactional
    public void modifyUser(ModifyUserParam modifyUserParam, LoginUserVo loginUserVo) {

        CloudUserDo cloudUserDo = cloudUserService.getById(modifyUserParam.getUserId());
        if (!Objects.equals(modifyUserParam.getRealName(), cloudUserDo.getRealName())) {
            //验证通组织下真实姓名是否重复
            CloudUserDo existUser = getUserByRealNameAndOrg(modifyUserParam.getOrganizationId(),
                    modifyUserParam.getRealName());
            if (Objects.nonNull(existUser)) {
                throw new KylinException(KylinHttpResponseUserConstants.EXIST_REAI_NAME);
            }
        }
        Date updateTime = new Date();
        cloudUserDo.setRealName(modifyUserParam.getRealName());
        cloudUserDo.setRemark(modifyUserParam.getRemark());
        cloudUserDo.setMobile(modifyUserParam.getMobile());
        cloudUserDo.setPassword(modifyUserParam.getPassword());
        cloudUserDo.setOrganizationId(modifyUserParam.getOrganizationId());
        cloudUserDo.setStatus(modifyUserParam.getStatus());
        cloudUserDo.setUpdateBy(loginUserVo.getUserId());
        cloudUserDo.setUpdateTime(updateTime);
        cloudUserService.updateById(cloudUserDo);

        //查询用户角色
        CloudUserRoleDo userRoleDo = cloudUserRoleService.getUserRoleByUserId(cloudUserDo.getId());
        //如果角色不一样，则更新用户拥有的角色
        if (!Objects.equals(userRoleDo.getRoleId(), modifyUserParam.getRoleId())) {
            userRoleDo.setRoleId(modifyUserParam.getRoleId());
            userRoleDo.setUpdateBy(loginUserVo.getUserId());
            userRoleDo.setUpdateTime(updateTime);
            cloudUserRoleService.updateById(userRoleDo);
        }
    }

    @Override
    public ModifyUserInfoRespDto modifyUserInfo(BaseUserParam baseUserParam) {
        ModifyUserInfoRespDto modifyUserInfoRespDto = new ModifyUserInfoRespDto();
        CloudUserDo cloudUserDo = cloudUserService.getById(baseUserParam.getUserId());
        modifyUserInfoRespDto.setUserId(cloudUserDo.getId());
        modifyUserInfoRespDto.setUserName(cloudUserDo.getUserName());
        modifyUserInfoRespDto.setRealName(cloudUserDo.getRealName());
        modifyUserInfoRespDto.setPassword(cloudUserDo.getPassword());
        modifyUserInfoRespDto.setRemark(cloudUserDo.getRemark());
        modifyUserInfoRespDto.setMobile(cloudUserDo.getMobile());

        modifyUserInfoRespDto.setOrganizationId(cloudUserDo.getOrganizationId());
        CloudOrganizationDo cloudOrganizationDo = cloudOrganizationService.getById(cloudUserDo.getOrganizationId());
        modifyUserInfoRespDto.setOrganizationName(cloudOrganizationDo.getOrganizationName());
        modifyUserInfoRespDto.setStatus(cloudUserDo.getStatus());

        //获取用户角色
        CloudUserRoleDo userRoleDo = cloudUserRoleService.getUserRoleByUserId(cloudUserDo.getId());
        CloudRoleDo roleDo = cloudRoleService.getById(userRoleDo.getRoleId());
        modifyUserInfoRespDto.setRoleName(roleDo.getRoleName());
        modifyUserInfoRespDto.setRoleId(roleDo.getId());
        modifyUserInfoRespDto.setCanModifyOrg(false);
        modifyUserInfoRespDto.setCanModifyRole(false);
        modifyUserInfoRespDto.setRoleType(roleDo.getRoleType());
        if (Objects.equals(roleDo.getRoleType(), RoleType.ORG)) {
            modifyUserInfoRespDto.setCanModifyOrg(true);
            modifyUserInfoRespDto.setCanModifyRole(true);
        } else if (Objects.equals(roleDo.getRoleType(), RoleType.SELF_SERVICE)) {
            boolean userHasMachine = userMachineService.userHasMachine(baseUserParam.getUserId());
            modifyUserInfoRespDto.setCanModifyOrg(!userHasMachine);
            modifyUserInfoRespDto.setCanModifyRole(false);
        }


        return modifyUserInfoRespDto;
    }


    /**
     * 删除用户
     * 限制条件:用户名下有云服务器，
     *
     * @param baseUserParam
     * @param loginUserVo
     */
    @Override
    @Transactional
    public void deleteUser(BaseUserParam baseUserParam, LoginUserVo loginUserVo) {
        boolean userHasMachine = userMachineService.userHasMachine(baseUserParam.getUserId());
        if (userHasMachine) {
            throw new KylinException(KylinHttpResponseUserConstants.USER_HAS_MACHINE_NO_DELETE);
        }

        Date deleteTime = new Date();
        CloudUserDo cloudUserDo = cloudUserService.getById(baseUserParam.getUserId());
        cloudUserDo.setDeleteFlag(true);
        cloudUserDo.setDeleteBy(loginUserVo.getUserId());
        cloudUserDo.setDeleteTime(deleteTime);
        cloudUserService.updateById(cloudUserDo);

        //查询用户角色
        CloudUserRoleDo userRoleDo = cloudUserRoleService.getUserRoleByUserId(cloudUserDo.getId());
        //如果角色不一样，则更新用户拥有的角色
        if (Objects.nonNull(userRoleDo)) {
            userRoleDo.setDeleteFlag(true);
            userRoleDo.setDeleteBy(loginUserVo.getUserId());
            userRoleDo.setDeleteTime(deleteTime);
            cloudUserRoleService.updateById(userRoleDo);
        }
    }


    @Override
    @Transactional
    public void batchDeleteUser(BatchDeleteUserParam batchDeleteUserParam, LoginUserVo loginUserVo) {

        //根据用户ID，获取用户具有的虚拟机给个数
        List<UserMachineCountDto> userMachineCounts =
                userMachineMapper.userMachineCountGroupByUserId(batchDeleteUserParam.getUserIds());
        if (!userMachineCounts.isEmpty()) {
            List<String> realNames =
                    userMachineCounts.stream().map(UserMachineCountDto::getRealName).collect(Collectors.toList());
            String realNameStr = String.join(",", realNames);
            throw new KylinException(realNameStr + "," + KylinHttpResponseUserConstants.USER_HAS_MACHINE_NO_DELETE);
        }

        Date deleteTime = new Date();
        CloudUserDo userDo = new CloudUserDo();
        userDo.setDeleteFlag(false);
        QueryWrapper<CloudUserDo> wrapper = new QueryWrapper<>(userDo);
        wrapper.in("id", batchDeleteUserParam.getUserIds());
        List<CloudUserDo> userList = cloudUserService.getBaseMapper().selectList(wrapper);
        userList.forEach(item -> {
            item.setDeleteFlag(true);
            item.setDeleteBy(loginUserVo.getUserId());
            item.setDeleteTime(deleteTime);
        });
        if (!userList.isEmpty()) {
            cloudUserService.updateBatchById(userList);
        }

        //删除用户拥有的角色
        CloudUserRoleDo userRoleDo = new CloudUserRoleDo();
        userRoleDo.setDeleteFlag(false);
        QueryWrapper<CloudUserRoleDo> userRoleWrapper = new QueryWrapper<>(userRoleDo);
        userRoleWrapper.in("user_id", batchDeleteUserParam.getUserIds());
        List<CloudUserRoleDo> userRoleDoList = cloudUserRoleService.getBaseMapper().selectList(userRoleWrapper);

        userRoleDoList.forEach(item -> {
            item.setDeleteFlag(true);
            item.setDeleteBy(loginUserVo.getUserId());
            item.setDeleteTime(deleteTime);
        });
        if (!userRoleDoList.isEmpty()) {
            cloudUserRoleService.updateBatchById(userRoleDoList);
        }
    }

    @Override
    public PageData<PageUserRespDto> pageUser(PageUserParam pageUserParam, LoginUserVo loginUserVo) {
        //List<Integer> orgIdList = orgService.getOrgChildIdList(pageUserParam.getOrgId());
        List<Integer> orgIdList = new ArrayList<>();
        orgIdList.add(pageUserParam.getOrgId());
        PageHelper.startPage(pageUserParam.getPageNo(), pageUserParam.getPageSize());
        List<PageUserRespDto> list = userMapper.pageUser(orgIdList, pageUserParam.getSearchKey());
        PageInfo<PageUserRespDto> pageInfo = new PageInfo<>(list);
        list.forEach(item -> {
            //内置用户不能编辑不能删除
            if (item.getDefaultUser()) {
                item.setCanDeleteUser(false);
                item.setCanModifyUser(false);
            }
            //自己删除和修改自己
            if (Objects.equals(item.getUserId(), loginUserVo.getUserId())) {
                item.setCanDeleteUser(false);
                item.setCanModifyUser(false);
            }
        });
        PageData pageData = new PageData(pageInfo);
        return pageData;
    }

//    @Override
//    public boolean judgeUserHasTopPermission(Integer userId) {
////        CloudRoleDo roleDo = roleService.getUserRole(userId);
////        if (Objects.isNull(roleDo)) {
////            return false;
////        }
////        //判断用户是否拥有
////
////        CloudOrganizationDo orgDo = cloudOrganizationService.getById(roleDo.getOrgId());
////        if (Objects.equals(roleDo.getRoleType(), RoleType.SYSTEM) ||
////                (Objects.equals(roleDo.getRoleType(), RoleType.ORG) && Objects.equals(orgDo.getParentId(),
////                        KylinCloudManageConstants.TOP_PARENT_ID))) {
////            return true;
////        }
//        return false;
//    }

    @Override
    public List<CloudUserDo> listUserByOrgId(Integer orgId) {
        CloudUserDo userDo = new CloudUserDo();
        userDo.setDeleteFlag(false);
        userDo.setOrganizationId(orgId);
        userDo.setStatus(CloudUserStatus.ACTIVATE);
        Wrapper<CloudUserDo> wrapper = new QueryWrapper<>(userDo);
        return cloudUserService.getBaseMapper().selectList(wrapper);
    }

    @Override
    public List<CloudUserDo> listUserByOrgList(List<Integer> orgIdList, CloudUserStatus userStatus) {
        CloudUserDo userDo = new CloudUserDo();
        userDo.setDeleteFlag(false);
        if (Objects.nonNull(userStatus)) {
            userDo.setStatus(userStatus);
        }
        QueryWrapper<CloudUserDo> wrapper = new QueryWrapper<>(userDo);
        wrapper.in("organization_id", orgIdList);
        return cloudUserService.getBaseMapper().selectList(wrapper);
    }

    /**
     * 根据用户ID获取该用户可见的用户列表
     *
     * @param userId
     * @return
     */
    @Override
    public List<CloudUserDo> userVisibleUserList(Integer userId) {

        List<CloudUserDo> userDoList = new ArrayList<>();

        CloudUserDo userDo = cloudUserService.getById(userId);
        //获取用户角色
        CloudRoleDo roleDo = roleService.getUserRole(userId);
        if (Objects.isNull(roleDo)) {
            userDoList.add(userDo);
            return userDoList;
        }
        //组织管理员，可用用户为该组织及子组织下用户
        if (Objects.equals(roleDo.getRoleType(), RoleType.ORG)) {
            return listUserByOrgIdIncludeChildOrg(userDo.getOrganizationId());
        }

        //角色类型是平台管理，判断用户是否用管理用户的权限，有全部用户为可见用户。
        boolean hasManageUserPermission = userHasManageUserPermission(userId);
        if (hasManageUserPermission) {
            CloudUserDo queryUserDo = new CloudUserDo();
            userDo.setDeleteFlag(false);
            Wrapper<CloudUserDo> wrapper = new QueryWrapper<>(queryUserDo);
            return cloudUserService.list(wrapper);
        }
        userDoList.add(userDo);
        return userDoList;
    }

    @Override
    public boolean userHasManageUserPermission(Integer userId) {

        return judgeUserHasRouteKeyPermission(userId, KylinCloudManageConstants.MANAGE_USER_PERMISSION);
    }

    @Override
    public boolean judgeUserHasRouteKeyPermission(Integer userId, String routeKey) {
        //获取用户角色
        CloudRoleDo roleDo = roleService.getUserRole(userId);
        //查询用户管理全新
        CloudPermissionDo manageUserPermission =
                permissionService.getPermissionByRouteKey(KylinCloudManageConstants.MANAGE_USER_PERMISSION);
        //查询角色是否拥有用户管理权限
        CloudRolePermissionDo queryRolePermissionDo = new CloudRolePermissionDo();
        queryRolePermissionDo.setRoleId(roleDo.getId());
        queryRolePermissionDo.setPermissionId(manageUserPermission.getId());
        QueryWrapper queryWrapper = new QueryWrapper(queryRolePermissionDo);
        return !cloudRolePermissionService.list(queryWrapper).isEmpty();
    }

    @Override
    public List<CloudUserDo> listUserByOrgIdIncludeChildOrg(Integer orgId) {
        List<Integer> childOrgList = orgService.getOrgChildIdList(orgId);
        CloudUserDo userDo = new CloudUserDo();
        userDo.setDeleteFlag(false);
        QueryWrapper<CloudUserDo> queryWrapper = new QueryWrapper<>(userDo);
        queryWrapper.in("organization_id", childOrgList);
        return cloudUserService.list(queryWrapper);
    }

    @Override
    public Integer countSelfServiceUserByOrgList(List<Integer> orgIdList) {

        return null;
    }


    @Override
    public CloudUserDo orgLeaderUser(Integer orgId) {
        Integer orgLeaderUserId = roleMapper.getOrgUserIdList(orgId);
        if (Objects.nonNull(orgLeaderUserId)) {

            return cloudUserService.getById(orgLeaderUserId);
        }
        return null;
    }

    @Override
    public boolean judgeIfPlatformUser(Integer userId) {
        //获取登录用户角色
        CloudRoleDo loginUserRole = roleService.getUserRole(userId);
        return Objects.equals(loginUserRole.getRoleType(), RoleType.PLATFORM);
    }
}
