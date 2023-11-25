package com.hnkylin.cloud.manage.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.hnkylin.cloud.core.common.KylinCommonConstants;
import com.hnkylin.cloud.core.common.PageData;
import com.hnkylin.cloud.core.config.exception.KylinException;
import com.hnkylin.cloud.core.domain.*;
import com.hnkylin.cloud.core.enums.RoleType;
import com.hnkylin.cloud.core.service.CloudOrganizationService;
import com.hnkylin.cloud.core.service.CloudRolePermissionService;
import com.hnkylin.cloud.core.service.CloudRoleService;
import com.hnkylin.cloud.core.service.CloudUserRoleService;
import com.hnkylin.cloud.manage.constant.KylinHttpResponseRoleConstants;
import com.hnkylin.cloud.manage.entity.LoginUserVo;
import com.hnkylin.cloud.manage.entity.req.org.BaseOrgParam;
import com.hnkylin.cloud.manage.entity.req.role.*;
import com.hnkylin.cloud.manage.entity.resp.role.*;
import com.hnkylin.cloud.manage.mapper.RoleMapper;
import com.hnkylin.cloud.manage.service.OrgService;
import com.hnkylin.cloud.manage.service.PermissionService;
import com.hnkylin.cloud.manage.service.RoleService;
import com.hnkylin.cloud.manage.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class RoleServiceImpl implements RoleService {

    @Resource
    private CloudRoleService cloudRoleService;

    @Resource
    private CloudRolePermissionService cloudRolePermissionService;

    @Resource
    private CloudUserRoleService cloudUserRoleService;

    @Resource
    private PermissionService permissionService;

    @Resource
    private RoleMapper roleMapper;

    @Resource
    private UserService userService;

    @Resource
    private CloudOrganizationService cloudOrganizationService;


    @Override
    @Transactional
    public void createPlatformRole(CreateRoleParam createRoleParam, LoginUserVo loginUserVo) {


        //查询是否同名
        checkIfExistRoleName(createRoleParam.getRoleName());

        Date now = new Date();
        CloudRoleDo cloudRoleDo = new CloudRoleDo();
        cloudRoleDo.setRoleName(createRoleParam.getRoleName());
        cloudRoleDo.setRemark(createRoleParam.getRemark());
        cloudRoleDo.setRoleType(RoleType.PLATFORM);
        cloudRoleDo.setCreateBy(loginUserVo.getUserId());
        cloudRoleDo.setCreateTime(now);
        cloudRoleDo.setDefaultRole(false);
        cloudRoleService.save(cloudRoleDo);

        //插入角色对应的权限
        cloudRolePermissionService.insertRolePermission(cloudRoleDo.getId(), createRoleParam.getPermissions(),
                loginUserVo.getUserId(), now);
    }

    /**
     * 删除角色  限制条件：没有用户绑定该角色
     *
     * @param baseRoleParam
     * @param loginUserVo
     */
    @Override
    @Transactional
    public void deleteRole(BaseRoleParam baseRoleParam, LoginUserVo loginUserVo) {
        //查询是否有用户绑定该角色
        CloudUserRoleDo userRoleDo = new CloudUserRoleDo();
        userRoleDo.setRoleId(baseRoleParam.getRoleId());
        Wrapper<CloudUserRoleDo> wrapper = new QueryWrapper<>(userRoleDo);
        List<CloudUserRoleDo> roleList = cloudUserRoleService.getBaseMapper().selectList(wrapper);
        if (!roleList.isEmpty()) {
            throw new KylinException(KylinHttpResponseRoleConstants.USER_BIND_ROLE_NOT_DELETE);
        }

        Date deleteTime = new Date();
        CloudRoleDo cloudRoleDo = cloudRoleService.getById(baseRoleParam.getRoleId());
        cloudRoleDo.setDeleteFlag(true);
        cloudRoleDo.setDeleteBy(loginUserVo.getUserId());
        cloudRoleDo.setDeleteTime(deleteTime);
        cloudRoleService.updateById(cloudRoleDo);

        //删除角色拥有的权限
        cloudRolePermissionService.deleteRolePermissionByRole(baseRoleParam.getRoleId(), loginUserVo.getUserId(),
                deleteTime);
    }


    /**
     * 检查同组织下角色名称是否重复
     *
     * @param roleName
     */
    private void checkIfExistRoleName(String roleName) {
        //判断名称是否已经存在
        CloudRoleDo roleDo = new CloudRoleDo();
        roleDo.setRoleName(roleName);
        roleDo.setDeleteFlag(false);
        Wrapper<CloudRoleDo> wrapper = new QueryWrapper<>(roleDo);
        List<CloudRoleDo> roleList = cloudRoleService.getBaseMapper().selectList(wrapper);
        if (!roleList.isEmpty()) {
            throw new KylinException(KylinHttpResponseRoleConstants.EXIST_ROLE_NAME);
        }
    }


    @Override
    public void modifyRole(ModifyRoleParam modifyRoleParam, LoginUserVo loginUserVo) {
        CloudRoleDo cloudRoleDo = cloudRoleService.getById(modifyRoleParam.getRoleId());
        if (!Objects.equals(modifyRoleParam.getRoleName(), cloudRoleDo.getRoleName())) {
            checkIfExistRoleName(modifyRoleParam.getRoleName());
        }
        Date now = new Date();
        cloudRoleDo.setRoleName(modifyRoleParam.getRoleName());
        cloudRoleDo.setRemark(modifyRoleParam.getRemark());
        cloudRoleDo.setUpdateBy(loginUserVo.getUserId());
        cloudRoleDo.setUpdateTime(now);

        cloudRoleService.updateById(cloudRoleDo);
        //删除角色拥有的权限
        cloudRolePermissionService.deleteRolePermissionByRole(cloudRoleDo.getId(), loginUserVo.getUserId(),
                now);
        //插入角色对应的权限
        cloudRolePermissionService.insertRolePermission(cloudRoleDo.getId(), modifyRoleParam.getPermissions(),
                loginUserVo.getUserId(), now);
    }


    @Override
    public RoleDetailDto modifyRoleDetail(BaseRoleParam baseRoleParam) {
        CloudRoleDo cloudRoleDo = cloudRoleService.getById(baseRoleParam.getRoleId());
        RoleDetailDto detailDto = new RoleDetailDto();
        detailDto.setRoleId(cloudRoleDo.getId());
        detailDto.setRoleName(cloudRoleDo.getRoleName());
        detailDto.setRoleType(cloudRoleDo.getRoleType());
        detailDto.setRemark(cloudRoleDo.getRemark());
        List<CloudPermissionDo> rolePermissionList =
                permissionService.listPermissionByRoleId(baseRoleParam.getRoleId());
        List<Integer> permissions =
                rolePermissionList.stream().map(CloudPermissionDo::getId).collect(Collectors.toList());
        detailDto.setPermissions(permissions);
        return detailDto;
    }

    @Override
    public RoleInfoRespDto roleInfo(BaseRoleParam baseRoleParam) {
        CloudRoleDo cloudRoleDo = cloudRoleService.getById(baseRoleParam.getRoleId());
        RoleInfoRespDto roleInfoRespDto = new RoleInfoRespDto();
        roleInfoRespDto.setRoleId(cloudRoleDo.getId());
        roleInfoRespDto.setRoleName(cloudRoleDo.getRoleName());
        roleInfoRespDto.setRoleType(cloudRoleDo.getRoleType());
        roleInfoRespDto.setRemark(cloudRoleDo.getRemark());
        roleInfoRespDto.setPermissionTree(getRolePermission(cloudRoleDo.getId()));
        return roleInfoRespDto;
    }

    private List<RolePermissionTreeDto> getRolePermission(Integer roleId) {

        List<CloudPermissionDo> rolePermissionList = permissionService.listPermissionByRoleId(roleId);
        List<RolePermissionTreeDto> permissionTreeDtoList = new ArrayList<>();
        for (CloudPermissionDo permissionDo : rolePermissionList) {
            if (Objects.equals(permissionDo.getParentId(), KylinCommonConstants.TOP_PARENT_ID)) {
                permissionTreeDtoList.add(formatRolePermissionTreeDto(permissionDo, rolePermissionList));
            }
        }
        return permissionTreeDtoList;
    }


    /**
     * 封装RolePermissionTreeDto对象
     */
    private RolePermissionTreeDto formatRolePermissionTreeDto(CloudPermissionDo permissionDo, List<CloudPermissionDo>
            rolePermissionList) {
        RolePermissionTreeDto rolePermissionTreeDto = RolePermissionTreeDto.builder()
                .permissionId(permissionDo.getId()).name(permissionDo.getName())
                .parentId(permissionDo.getParentId()).icon(permissionDo.getIcon())
                .routeKey(permissionDo.getRouteKey()).build();

        rolePermissionTreeDto.setChild(getChild(permissionDo.getId(), rolePermissionList));
        return rolePermissionTreeDto;
    }

    /**
     * 递归创建
     */
    private List<RolePermissionTreeDto> getChild(Integer parentId, List<CloudPermissionDo> rolePermissionList) {
        List<RolePermissionTreeDto> childList = new ArrayList<>();
        for (CloudPermissionDo permissionDo : rolePermissionList) {
            if (Objects.equals(parentId, permissionDo.getParentId())) {
                RolePermissionTreeDto rolePermissionTreeDto = formatRolePermissionTreeDto(permissionDo,
                        rolePermissionList);
                childList.add(rolePermissionTreeDto);
            }
        }
        return childList;
    }


    @Override
    public List<CommonRoleDto> listRole(SearchRoleParam searchRoleParam, LoginUserVo loginUserVo) {
        List<CommonRoleDto> commonRoleList = new ArrayList<>();
        boolean ifPlatformUser = userService.judgeIfPlatformUser(loginUserVo.getUserId());
        if (ifPlatformUser) {
            CloudRoleDo roleDo = new CloudRoleDo();
            roleDo.setDeleteFlag(false);
            QueryWrapper<CloudRoleDo> queryWrapper = new QueryWrapper<>(roleDo);
            if (StringUtils.isNotBlank(searchRoleParam.getSearchKey())) {
                queryWrapper.and(wrapper -> wrapper.like("role_name", searchRoleParam.getSearchKey())
                        .or().like("remark", searchRoleParam.getSearchKey()));
            }
            List<CloudRoleDo> roleDoList = cloudRoleService.list(queryWrapper);
            CloudOrganizationDo organizationDo = cloudOrganizationService.getById(searchRoleParam.getOrgId());
            roleDoList.forEach(item -> {
                CommonRoleDto commonRoleDto = new CommonRoleDto();
                commonRoleDto.setRoleId(item.getId());
                commonRoleDto.setRoleName(item.getRoleName());
                commonRoleDto.setRemark(item.getRemark());
                commonRoleDto.setRoleType(item.getRoleType());

                //根组织，不能选择组织管理员，不能选择自服务用户
                if (Objects.equals(organizationDo.getParentId(), KylinCommonConstants.TOP_PARENT_ID)) {
                    if (Objects.equals(item.getRoleType(), RoleType.PLATFORM) && !item.getDefaultRole()) {
                        commonRoleList.add(commonRoleDto);
                    }
                } else {
                    if (Objects.equals(item.getRoleType(), RoleType.ORG) || Objects.equals(item.getRoleType(),
                            RoleType.SELF_SERVICE)) {
                        commonRoleList.add(commonRoleDto);
                    }
                }
            });
        } else {
            CloudRoleDo selfRole = getSelfServiceRole();
            CommonRoleDto commonRoleDto = new CommonRoleDto();
            commonRoleDto.setRoleId(selfRole.getId());
            commonRoleDto.setRoleName(selfRole.getRoleName());
            commonRoleDto.setRemark(selfRole.getRemark());
            commonRoleDto.setRoleType(selfRole.getRoleType());
            commonRoleList.add(commonRoleDto);
        }
        return commonRoleList;
    }

    @Override
    public PageData<PageRoleRespDto> pageRole(RolePageParam rolePageParam) {

        PageHelper.startPage(rolePageParam.getPageNo(), rolePageParam.getPageSize());

        List<PageRoleRespDto> list = roleMapper.pageRole();
        PageInfo<PageRoleRespDto> pageInfo = new PageInfo<>(list);

        pageInfo.getList().forEach(role -> {
            role.setRoleTypeDesc(role.getRoleType().getDesc());
            role.setCanDelete(false);
            role.setCanModify(false);
            if (!role.getDefaultRole() && Objects.equals(role.getRoleType(), RoleType.PLATFORM)) {
                role.setCanDelete(true);
                role.setCanModify(true);
            }
        });
        PageData pageData = new PageData(pageInfo);
        return pageData;
    }


    @Override
    public CloudRoleDo getSelfServiceRole() {
        CloudRoleDo roleDo = new CloudRoleDo();
        roleDo.setDeleteFlag(false);
        roleDo.setRoleType(RoleType.SELF_SERVICE);
        Wrapper<CloudRoleDo> wrapper = new QueryWrapper<>(roleDo);
        List<CloudRoleDo> listRole = cloudRoleService.list(wrapper);
        return listRole.isEmpty() ? null : listRole.get(0);
    }


    @Override
    public void allocateRolePermission(AllocateRolePermissionParam allocateRolePermissionParam,
                                       LoginUserVo loginUserVo) {


        List<Integer> permissionIds = new ArrayList<>();
        String[] permissionArray = allocateRolePermissionParam.getPermissionIds().split(",");

        for (String s : permissionArray) {
            permissionIds.add(Integer.parseInt(s));
        }
        //插入角色对应的权限
        cloudRolePermissionService.insertRolePermission(allocateRolePermissionParam.getRoleId(), permissionIds,
                loginUserVo.getUserId(), new Date());

    }


    @Override
    public CloudRoleDo getUserRole(Integer userId) {
        //获取用户的角色
        CloudUserRoleDo cloudUserRoleDo = cloudUserRoleService.getUserRoleByUserId(userId);
        if (Objects.nonNull(cloudUserRoleDo)) {
            return cloudRoleService.getById(cloudUserRoleDo.getRoleId());
        }
        return null;
    }
}
