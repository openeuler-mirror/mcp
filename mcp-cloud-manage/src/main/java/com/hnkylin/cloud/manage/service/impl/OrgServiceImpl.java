package com.hnkylin.cloud.manage.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hnkylin.cloud.core.common.DateUtils;
import com.hnkylin.cloud.core.common.KylinCommonConstants;
import com.hnkylin.cloud.core.common.servervm.McServerVmPageDetailResp;
import com.hnkylin.cloud.core.config.exception.KylinException;
import com.hnkylin.cloud.core.domain.*;
import com.hnkylin.cloud.core.enums.CloudUserStatus;
import com.hnkylin.cloud.core.enums.McServerVmStatus;
import com.hnkylin.cloud.core.enums.RoleType;
import com.hnkylin.cloud.core.enums.UserType;
import com.hnkylin.cloud.core.service.*;
import com.hnkylin.cloud.manage.constant.KylinCloudManageConstants;
import com.hnkylin.cloud.manage.constant.KylinHttpResponseOrgConstants;
import com.hnkylin.cloud.manage.constant.KylinHttpResponseUserConstants;
import com.hnkylin.cloud.manage.entity.LoginUserVo;
import com.hnkylin.cloud.manage.entity.req.org.*;
import com.hnkylin.cloud.manage.entity.resp.org.*;
import com.hnkylin.cloud.manage.entity.resp.role.OrgStatisticTreeDto;
import com.hnkylin.cloud.manage.entity.resp.vdc.VdcUsedResourceDto;
import com.hnkylin.cloud.manage.enums.OrgStatisticTreeType;
import com.hnkylin.cloud.manage.service.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class OrgServiceImpl implements OrgService {


    @Resource
    private CloudOrganizationService cloudOrganizationService;

    @Resource
    private CloudOrgVdcService cloudOrgVdcService;


    @Resource
    private CloudUserService cloudUserService;

    @Resource
    private CloudRoleService cloudRoleService;

    @Resource
    private CloudUserRoleService cloudUserRoleService;

    @Resource
    private CloudVdcService cloudVdcService;

    @Resource
    private RoleService roleService;

    @Resource
    private UserService userService;


    @Resource
    private UserMachineService userMachineService;

    @Resource
    private CloudPermissionService cloudPermissionService;

    @Resource
    private CloudRolePermissionService cloudRolePermissionService;

    @Resource
    private VdcService vdcService;

    @Override
    @Transactional
    public void createOrg(CreateOrgParam createOrgParam, LoginUserVo loginUserVo) {

        Date now = new Date();

        //先判断是否重名
        CloudOrganizationDo existOrg =
                cloudOrganizationService.queryByOrganizationNameName(createOrgParam.getOrganizationName());
        if (Objects.nonNull(existOrg)) {
            throw new KylinException(KylinHttpResponseOrgConstants.EXIST_ORG);
        }
        Integer parentId = createOrgParam.getParentId();
        if (Objects.equals(createOrgParam.getParentId(), KylinCommonConstants.TOP_PARENT_ID)) {
            CloudOrganizationDo defaultTopOrg = cloudOrganizationService.getDefaultTopOrg();
            parentId = defaultTopOrg.getId();
        }

        //新增组织
        CloudOrganizationDo cloudOrganizationDo = new CloudOrganizationDo();
        cloudOrganizationDo.setOrganizationName(createOrgParam.getOrganizationName());
        cloudOrganizationDo.setParentId(parentId);
        cloudOrganizationDo.setRemark(createOrgParam.getRemark());
        cloudOrganizationDo.setCreateBy(loginUserVo.getUserId());
        cloudOrganizationDo.setCreateTime(now);
        cloudOrganizationService.save(cloudOrganizationDo);
        //组织和VDC关联表
        CloudOrgVdcDo cloudOrgVdcDo = new CloudOrgVdcDo();
        cloudOrgVdcDo.setOrgId(cloudOrganizationDo.getId());
        cloudOrgVdcDo.setVdcId(createOrgParam.getVdcId());
        cloudOrgVdcDo.setCreateBy(loginUserVo.getUserId());
        cloudOrgVdcDo.setCreateTime(now);
        cloudOrgVdcService.save(cloudOrgVdcDo);


        if (createOrgParam.getCreateLeader()) {
            //创建组织管理员和对应角色
            createOrgLeaderUser(loginUserVo, cloudOrganizationDo.getId(),
                    createOrgParam.getLeaderUserName(), createOrgParam.getLeaderRealName(),
                    createOrgParam.getLeaderPassword());
        }


    }

    /**
     * 创建组织管理员和对应角色，并赋予组织管理员权限
     */
    private void createOrgLeaderUser(LoginUserVo loginUserVo, Integer orgId,
                                     String leaderUserName, String leaderRealName, String leaderPassword) {

        //验证用户名是否重复
        CloudUserDo userDo = new CloudUserDo();
        userDo.setUserName(leaderUserName);
        userDo.setDeleteFlag(false);
        Wrapper<CloudUserDo> wrapper = new QueryWrapper<>(userDo);
        int existUserCount = cloudUserService.getBaseMapper().selectCount(wrapper);
        if (existUserCount > 0) {
            throw new KylinException(KylinHttpResponseUserConstants.EXIST_USER);
        }

        //查询是否存在组织管理员角色
        CloudRoleDo orgRole = cloudRoleService.getOrgRole();

        Integer roleId = orgRole.getId();
        //创建用户
        CloudUserDo cloudUserDo = createLeaderUser(loginUserVo, orgId,
                leaderUserName, leaderRealName, leaderPassword);
        CloudUserRoleDo userRoleDo = CloudUserRoleDo.builder().userId(cloudUserDo.getId())
                .roleId(roleId).build();
        userRoleDo.setCreateBy(loginUserVo.getUserId());
        userRoleDo.setCreateTime(new Date());
        cloudUserRoleService.save(userRoleDo);


    }

    @Override
    @Transactional
    public void modifyOrg(ModifyOrgParam modifyOrgParam, LoginUserVo loginUserVo) {


        CloudOrganizationDo existOrg =
                cloudOrganizationService.queryByOrganizationNameName(modifyOrgParam.getOrganizationName());
        if (Objects.nonNull(existOrg) && !Objects.equals(existOrg.getId(), modifyOrgParam.getOrgId())) {
            throw new KylinException(KylinHttpResponseOrgConstants.EXIST_ORG);
        }


        Date updateTime = new Date();

        CloudOrganizationDo cloudOrganizationDo = cloudOrganizationService.getById(modifyOrgParam.getOrgId());
        cloudOrganizationDo.setOrganizationName(modifyOrgParam.getOrganizationName());
        cloudOrganizationDo.setRemark(modifyOrgParam.getRemark());
        cloudOrganizationDo.setParentId(modifyOrgParam.getParentId());
        cloudOrganizationDo.setUpdateBy(loginUserVo.getUserId());
        cloudOrganizationDo.setUpdateTime(updateTime);
        cloudOrganizationService.updateById(cloudOrganizationDo);


        //组织和VDC关联
        CloudOrgVdcDo cloudOrgVdcDo = new CloudOrgVdcDo();
        cloudOrgVdcDo.setOrgId(modifyOrgParam.getOrgId());
        cloudOrgVdcDo.setDeleteFlag(false);
        Wrapper<CloudOrgVdcDo> orgVdcDoWrapper =
                new QueryWrapper<>(cloudOrgVdcDo);
        CloudOrgVdcDo orgVdcDo = cloudOrgVdcService.getOne(orgVdcDoWrapper);
        //如果变更了VDC
        if (Objects.nonNull(orgVdcDo) && !Objects.equals(orgVdcDo.getVdcId(), modifyOrgParam.getVdcId())) {
            orgVdcDo.setVdcId(modifyOrgParam.getVdcId());
            orgVdcDo.setUpdateBy(loginUserVo.getUserId());
            orgVdcDo.setUpdateTime(updateTime);
            cloudOrgVdcService.updateById(orgVdcDo);
        }
        //创建组织管理员
        if (modifyOrgParam.getCreateLeader()) {
            //新建的组织管理员
            if (Objects.equals(modifyOrgParam.getLeaderUserId(), 0) || Objects.isNull(modifyOrgParam.getLeaderUserId())) {
                //创建组织管理员和对应角色，并赋予组织管理员权限
                createOrgLeaderUser(loginUserVo, cloudOrganizationDo.getId(),
                        modifyOrgParam.getLeaderUserName(), modifyOrgParam.getLeaderRealName(),
                        modifyOrgParam.getLeaderPassword());
            } else {
                //修改原有的组织管理员
                CloudUserDo orgLeaderUser = cloudUserService.getById(modifyOrgParam.getLeaderUserId());
                orgLeaderUser.setUserName(modifyOrgParam.getLeaderUserName());
                orgLeaderUser.setRealName(modifyOrgParam.getLeaderRealName());
                orgLeaderUser.setPassword(modifyOrgParam.getLeaderPassword());
                orgLeaderUser.setUpdateBy(loginUserVo.getUserId());
                orgLeaderUser.setUpdateTime(updateTime);
                cloudUserService.updateById(orgLeaderUser);
            }
        }
    }


    @Override
    public void modifyTopOrgName(ModifyTopOrgNameParam modifyOrgParam, LoginUserVo loginUserVo) {
        CloudOrganizationDo existOrg =
                cloudOrganizationService.queryByOrganizationNameName(modifyOrgParam.getOrganizationName());
        if (Objects.nonNull(existOrg) && !Objects.equals(existOrg.getId(), modifyOrgParam.getOrgId())) {
            throw new KylinException(KylinHttpResponseOrgConstants.EXIST_ORG);
        }

        Date updateTime = new Date();

        CloudOrganizationDo cloudOrganizationDo = cloudOrganizationService.getById(modifyOrgParam.getOrgId());
        cloudOrganizationDo.setOrganizationName(modifyOrgParam.getOrganizationName());
        cloudOrganizationDo.setUpdateBy(loginUserVo.getUserId());
        cloudOrganizationDo.setUpdateTime(updateTime);
        cloudOrganizationService.updateById(cloudOrganizationDo);
    }

    /**
     * 组织管理-创建(编辑)组织时选择的父组织结构
     */
    @Override
    public List<ParentOrgRespDto> queryParentOrg(QueryParentOrgParam queryParentOrgParam, LoginUserVo loginUserVo) {
        //获取登录用户组织
        CloudOrganizationDo loginUserOrg = getByUserId(loginUserVo.getUserId());
        //查询全部组织
        List<CloudOrganizationDo> orgDoList = cloudOrganizationService.queryAllOrgList();
        //编辑时，当前的组织，需要过滤不能选中自己做为上级分组
        Integer nowOrgId = queryParentOrgParam.getOrganizationId();
        List<ParentOrgRespDto> parentOrgList = new ArrayList<>();
        for (CloudOrganizationDo orgDo : orgDoList) {

            if (Objects.equals(orgDo.getId(), loginUserOrg.getId())) {
                if (Objects.nonNull(nowOrgId) && nowOrgId > 0) {
                    if (!Objects.equals(orgDo.getId(), nowOrgId)) {
                        parentOrgList.add(formatParentOrgDto(orgDo, orgDoList, nowOrgId));
                    }
                } else {
                    parentOrgList.add(formatParentOrgDto(orgDo, orgDoList, nowOrgId));
                }

            }
        }
        return parentOrgList;
    }


    private void formatCommonOrgTree(CloudOrganizationDo cloudOrganizationDo,
                                     List<CloudOrganizationDo> allOrgList,
                                     CommonOrgTreeRespDto commonOrgTreeRespDto) {
        commonOrgTreeRespDto.setOrganizationId(cloudOrganizationDo.getId());
        commonOrgTreeRespDto.setOrganizationName(cloudOrganizationDo.getOrganizationName());
        commonOrgTreeRespDto.setParentId(cloudOrganizationDo.getParentId());
        commonOrgTreeRespDto.setRemark(cloudOrganizationDo.getRemark());
        commonOrgTreeRespDto.setParentName("");
        CloudOrganizationDo parentDo =
                allOrgList.stream().filter(item -> Objects.equals(item.getId(), cloudOrganizationDo.getParentId())).findFirst().orElse(null);
        if (Objects.nonNull(parentDo)) {
            commonOrgTreeRespDto.setParentName(parentDo.getOrganizationName());
        }
    }

    /**
     * 组织管理-创建(编辑)组织时选择的父组织结构-封装组织数据
     */
    private ParentOrgRespDto formatParentOrgDto(CloudOrganizationDo cloudOrganizationDo,
                                                List<CloudOrganizationDo> allOrgList, Integer nowOrgId) {
        ParentOrgRespDto parentOrgRespDto = new ParentOrgRespDto();
        formatCommonOrgTree(cloudOrganizationDo, allOrgList, parentOrgRespDto);
        parentOrgRespDto.setChildren(getParentChildOrg(cloudOrganizationDo.getId(), allOrgList, nowOrgId));
        return parentOrgRespDto;
    }

    /**
     * 组织管理-创建(编辑)组织时选择的父组织结构-获取子组织
     */
    private List<ParentOrgRespDto> getParentChildOrg(Integer parentId, List<CloudOrganizationDo> allOrgList,
                                                     Integer nowOrgId) {
        List<ParentOrgRespDto> childList = new ArrayList<>();
        for (CloudOrganizationDo organizationDo : allOrgList) {
            if (Objects.equals(parentId, organizationDo.getParentId())) {
                if (Objects.nonNull(nowOrgId) && nowOrgId > 0) {
                    if (!Objects.equals(organizationDo.getId(), nowOrgId) && !Objects.equals(organizationDo.getParentId(),
                            nowOrgId)) {
                        ParentOrgRespDto parentOrgRespDto = formatParentOrgDto(organizationDo, allOrgList, nowOrgId);
                        childList.add(parentOrgRespDto);
                    }
                } else {
                    ParentOrgRespDto parentOrgRespDto = formatParentOrgDto(organizationDo, allOrgList, nowOrgId);
                    childList.add(parentOrgRespDto);
                }

            }
        }
        return childList;
    }

    /**
     * 组织管理-主列表
     */
    @Override
    public List<OrganizationRespDto> queryOrgList(LoginUserVo loginUserVo) {

        //查询全部组织
        List<CloudOrganizationDo> orgDoList = cloudOrganizationService.queryAllOrgList();
        List<OrganizationRespDto> organizationList = new ArrayList<>();
        //登录用户组织
        CloudOrganizationDo loginUserOrg = getByUserId(loginUserVo.getUserId());

        for (CloudOrganizationDo orgDo : orgDoList) {
            if (Objects.equals(orgDo.getId(), loginUserOrg.getId())) {
                organizationList.add(formatOrgDto(orgDo, orgDoList, loginUserVo));
            }
        }
        return organizationList;
    }


    /**
     * 组织管理主列表-创新主列表数据
     */
    private OrganizationRespDto formatOrgDto(CloudOrganizationDo cloudOrganizationDo,
                                             List<CloudOrganizationDo> allOrgList, LoginUserVo loginUserVo) {
        OrganizationRespDto organizationDto = new OrganizationRespDto();
        formatCommonOrgTree(cloudOrganizationDo, allOrgList, organizationDto);
        orgStatistics(organizationDto, cloudOrganizationDo, loginUserVo);

        organizationDto.setChildren(getChild(cloudOrganizationDo.getId(), allOrgList, loginUserVo));
        return organizationDto;
    }


    /**
     * 组织数据统计
     *
     * @param organizationRespDto
     */
    private void orgStatistics(OrganizationRespDto organizationRespDto, CloudOrganizationDo cloudOrganizationDo,
                               LoginUserVo loginUserVo) {


        List<CloudUserDo> userDoList = userService.listUserByOrgId(cloudOrganizationDo.getId());
        organizationRespDto.setUserNum(userDoList.size());
        organizationRespDto.setServerVmNum(0);
        if (!userDoList.isEmpty()) {
            List<Integer> userIdList = userDoList.stream().map(CloudUserDo::getId).collect(Collectors.toList());
            organizationRespDto.setServerVmNum(userMachineService.countUserMachineByUserIdList(userIdList));
        }
        if (Objects.equals(cloudOrganizationDo.getParentId(), KylinCommonConstants.TOP_PARENT_ID)) {
            organizationRespDto.setTopOrg(true);
            return;
        }

        CloudVdcDo vdcDo = cloudVdcService.getVdcByOrgId(cloudOrganizationDo.getId());
        organizationRespDto.setVdcId(vdcDo.getId());
        organizationRespDto.setVdcName(vdcDo.getVdcName());
        //获取VDC资源情况
        VdcUsedResourceDto vdcResourceDto = vdcService.getVdcResourceInfo(vdcDo.getId(), loginUserVo);

        organizationRespDto.setAllocationCpu(vdcResourceDto.getTotalCpu());
        organizationRespDto.setUsedCpu(vdcResourceDto.getUsedCpu());

        organizationRespDto.setAllocationMem(new BigDecimal(vdcResourceDto.getTotalMem()));

        organizationRespDto.setUsedMem(new BigDecimal(vdcResourceDto.getUsedMem()));
        organizationRespDto.setMemUnit(vdcResourceDto.getMemUnit());

        organizationRespDto.setAllocationDisk(new BigDecimal(vdcResourceDto.getTotalStorage()));
        organizationRespDto.setStorageUnit(vdcResourceDto.getStorageUnit());

        organizationRespDto.setUsedDisk(new BigDecimal(vdcResourceDto.getUsedStorage()));

    }


    /**
     * 组织管理主列表-获取组织的子组织
     */
    private List<OrganizationRespDto> getChild(Integer parentId, List<CloudOrganizationDo> allOrgList,
                                               LoginUserVo loginUserVo) {
        List<OrganizationRespDto> childList = new ArrayList<>();
        for (CloudOrganizationDo organizationDo : allOrgList) {
            if (Objects.equals(parentId, organizationDo.getParentId())) {
                OrganizationRespDto organizationDto = formatOrgDto(organizationDo, allOrgList, loginUserVo);
                childList.add(organizationDto);
            }
        }
        return childList;
    }


    /**
     * 是否拥有下级组织
     * return  true:拥有 false:没有下级组织
     */
    private boolean ifHasChildOrg(Integer parentOrgId) {
        CloudOrganizationDo parentOrg = new CloudOrganizationDo();
        parentOrg.setParentId(parentOrgId);
        parentOrg.setDeleteFlag(Boolean.FALSE);
        Wrapper<CloudOrganizationDo> wrapper = new QueryWrapper<>(parentOrg);
        List<CloudOrganizationDo> childOrgList = cloudOrganizationService.getBaseMapper().selectList(wrapper);
        return !childOrgList.isEmpty();
    }

    /**
     * 删除组织，限制条件：
     * 1:拥有下级组织不能删除
     * 2:组织及下级组织有用户不能删除
     */
    @Override
    @Transactional
    public void deleteOrg(BaseOrgParam baseOrgParam, LoginUserVo loginUserVo) {
        //1:查询是否拥有下级组织

        boolean hasChildOrg = ifHasChildOrg(baseOrgParam.getOrgId());
        if (hasChildOrg) {
            throw new KylinException(KylinHttpResponseOrgConstants.HAS_CHILD_NOT_DELETE);
        }

        //查询组织是否拥有用户
        CloudUserDo cloudUserDo = new CloudUserDo();
        cloudUserDo.setOrganizationId(baseOrgParam.getOrgId());
        cloudUserDo.setDeleteFlag(false);
        Wrapper<CloudUserDo> userWrapper = new QueryWrapper<>(cloudUserDo);
        List<CloudUserDo> userList = cloudUserService.getBaseMapper().selectList(userWrapper);
        if (!userList.isEmpty()) {
            throw new KylinException(KylinHttpResponseOrgConstants.HAS_USER_NOT_DELETE);
        }
        Date deleteTime = new Date();
        //删除组织
        CloudOrganizationDo cloudOrganizationDo = cloudOrganizationService.getById(baseOrgParam.getOrgId());
        cloudOrganizationDo.setDeleteBy(loginUserVo.getUserId());
        cloudOrganizationDo.setDeleteTime(deleteTime);
        cloudOrganizationDo.setDeleteFlag(true);
        cloudOrganizationService.updateById(cloudOrganizationDo);

        //删除组织和VDC绑定关系
        Wrapper<CloudOrgVdcDo> orgVdcDoWrapper =
                new QueryWrapper<>(CloudOrgVdcDo.builder().orgId(baseOrgParam.getOrgId()).build());
        List<CloudOrgVdcDo> orgVdcList = cloudOrgVdcService.getBaseMapper().selectList(orgVdcDoWrapper);
        if (!orgVdcList.isEmpty()) {
            orgVdcList.stream().forEach(item -> {
                item.setDeleteBy(loginUserVo.getUserId());
                item.setDeleteTime(deleteTime);
                item.setDeleteFlag(true);
            });
            cloudOrgVdcService.updateBatchById(orgVdcList);
        }
    }


    /**
     * 组织明显是否拥有云服务器
     * return  true:拥有 false:没有云服务器
     */
    private boolean ifHasServerVm(Integer orgId) {
        return false;
    }

    /**
     * canModifyParent(能否修改上级组织)  canModifyVdc(能否修改绑定的VDC)限制条件
     * 当前组织是没有下级，并且当前组织下没有任何云服务器，
     */
    @Override
    public OrgDetailRespDto orgDetail(BaseOrgParam baseOrgParam, LoginUserVo loginUserVo) {

        CloudOrganizationDo cloudOrganizationDo = cloudOrganizationService.getById(baseOrgParam.getOrgId());

        OrgDetailRespDto orgDetailRespDto = new OrgDetailRespDto();
        orgDetailRespDto.setOrganizationId(cloudOrganizationDo.getId());
        orgDetailRespDto.setOrganizationName(cloudOrganizationDo.getOrganizationName());
        orgDetailRespDto.setRemark(cloudOrganizationDo.getRemark());
        orgDetailRespDto.setParentId(cloudOrganizationDo.getParentId());
        orgDetailRespDto.setParentName("");
        if (!Objects.equals(cloudOrganizationDo.getParentId(), KylinCloudManageConstants.TOP_PARENT_ID)) {
            CloudOrganizationDo parentOrg = cloudOrganizationService.getById(cloudOrganizationDo.getParentId());
            orgDetailRespDto.setParentName(parentOrg.getOrganizationName());
        }

        //组织和VDC关联
        CloudOrgVdcDo cloudOrgVdcDo = new CloudOrgVdcDo();
        cloudOrgVdcDo.setOrgId(baseOrgParam.getOrgId());
        cloudOrgVdcDo.setDeleteFlag(false);
        Wrapper<CloudOrgVdcDo> orgVdcDoWrapper =
                new QueryWrapper<>(cloudOrgVdcDo);
        CloudOrgVdcDo orgVdcDo = cloudOrgVdcService.getOne(orgVdcDoWrapper);

        if (Objects.nonNull(orgVdcDo)) {
            CloudVdcDo vdcDo = cloudVdcService.getById(orgVdcDo.getVdcId());
            orgDetailRespDto.setVdcId(vdcDo.getId());
            orgDetailRespDto.setVdcName(vdcDo.getVdcName());
        }
        orgDetailRespDto.setCanModifyParent(false);
        orgDetailRespDto.setCanModifyVdc(false);

        //是否拥有下级组织
        boolean hasChildOrg = ifHasChildOrg(baseOrgParam.getOrgId());
        if (!hasChildOrg && !Objects.equals(cloudOrganizationDo.getParentId(),
                KylinCloudManageConstants.TOP_PARENT_ID)) {
            //是否拥有云服务器
            boolean orgHasServerVm = ifHasServerVm(baseOrgParam.getOrgId());
            if (!orgHasServerVm) {
                orgDetailRespDto.setCanModifyParent(true);
                orgDetailRespDto.setCanModifyVdc(true);
            }
        }

        CloudUserDo orgLeaderUser = userService.orgLeaderUser(cloudOrganizationDo.getId());
        //组织是否有管理员
        orgDetailRespDto.setHasOrgLeader(Objects.nonNull(orgLeaderUser));
        if (Objects.nonNull(orgLeaderUser)) {
            orgDetailRespDto.setOrgLeaderUserId(orgLeaderUser.getId());
            orgDetailRespDto.setOrgLeaderUserName(orgLeaderUser.getUserName());
            orgDetailRespDto.setOrgLeaderRealName(orgLeaderUser.getRealName());
            orgDetailRespDto.setOrgLeaderUserPassword(orgLeaderUser.getPassword());
        }


        return orgDetailRespDto;
    }


    /**
     * 创建组织管理员用户
     */
    private CloudUserDo createLeaderUser(LoginUserVo loginUserVo,
                                         Integer orgId,
                                         String leaderUserName, String leaderRealName, String leaderPassword) {
        CloudUserDo cloudUserDo = new CloudUserDo();
        cloudUserDo.setUserName(leaderUserName);
        cloudUserDo.setRealName(leaderRealName);
        cloudUserDo.setPassword(leaderPassword);
        cloudUserDo.setStatus(CloudUserStatus.ACTIVATE);
        cloudUserDo.setUserType(UserType.cloudUser);
        cloudUserDo.setOrganizationId(orgId);
        cloudUserDo.setCreateBy(loginUserVo.getUserId());
        cloudUserDo.setCreateTime(new Date());
        cloudUserDo.setDefaultUser(false);
        cloudUserDo.setSuperUser(false);
        cloudUserService.save(cloudUserDo);
        return cloudUserDo;
    }

    @Override
    public List<OrgStatisticTreeDto> orgStatisticTree(LoginUserVo loginUserVo, OrgStatisticTreeType orgTreeType) {


        //查询全部组织
        List<CloudOrganizationDo> orgDoList = cloudOrganizationService.queryAllOrgList();
        CloudOrganizationDo loginUserOrg = getByUserId(loginUserVo.getUserId());
        List<OrgStatisticTreeDto> statisticOrgTrees = new ArrayList<>();
        for (CloudOrganizationDo orgDo : orgDoList) {
            if (Objects.equals(orgDo.getId(), loginUserOrg.getId())) {
                statisticOrgTrees.add(formatStatisticOrgTree(orgDo, orgDoList, orgTreeType));
            }
        }
        return statisticOrgTrees;
    }

    /**
     * 封装统计数实体
     */
    private OrgStatisticTreeDto formatStatisticOrgTree(CloudOrganizationDo cloudOrganizationDo,
                                                       List<CloudOrganizationDo> allOrgList,
                                                       OrgStatisticTreeType orgTreeType) {

        OrgStatisticTreeDto statisticOrgTree = new OrgStatisticTreeDto();
        formatCommonOrgTree(cloudOrganizationDo, allOrgList, statisticOrgTree);
        List<Integer> orgIdList = getOrgChildIdList(cloudOrganizationDo.getId());
        //统计数量
        switch (orgTreeType) {
            case USER:
                statisticOrgTree.setStatisticNum(userService.statisticOrgUserNumByOrgId(cloudOrganizationDo.getId()));
                break;
            default:
                statisticOrgTree.setStatisticNum(0);
                break;
        }
        statisticOrgTree.setChildren(getStatisticOrgTreeChild(cloudOrganizationDo.getId(), allOrgList, orgTreeType));
        return statisticOrgTree;
    }


    /**
     * 获取统计树子组织
     */
    private List<OrgStatisticTreeDto> getStatisticOrgTreeChild(Integer parentId, List<CloudOrganizationDo> allOrgList,
                                                               OrgStatisticTreeType orgTreeType) {
        List<OrgStatisticTreeDto> childList = new ArrayList<>();
        for (CloudOrganizationDo organizationDo : allOrgList) {
            if (Objects.equals(parentId, organizationDo.getParentId())) {
                OrgStatisticTreeDto statisticOrgTree = formatStatisticOrgTree(organizationDo, allOrgList, orgTreeType);
                childList.add(statisticOrgTree);
            }
        }
        return childList;
    }


    @Override
    public List<Integer> getOrgChildIdList(Integer parentOrgId) {
        return cloudOrganizationService.listChildOrgByOrgId(parentOrgId).stream().map(CloudOrganizationDo::getId).collect(Collectors.toList());
    }


    @Override
    public CloudOrganizationDo getOrgByVdcId(Integer vdcId) {
        CloudOrgVdcDo orgVdcDo = new CloudOrgVdcDo();
        orgVdcDo.setVdcId(vdcId);
        orgVdcDo.setDeleteFlag(false);
        Wrapper<CloudOrgVdcDo> orgVdcDoWrapper =
                new QueryWrapper<>(orgVdcDo);
        List<CloudOrgVdcDo> orgVdcList = cloudOrgVdcService.getBaseMapper().selectList(orgVdcDoWrapper);
        return orgVdcList.isEmpty() ? null : cloudOrganizationService.getById(orgVdcList.get(0).getOrgId());
    }


    @Override
    public boolean userHasOrgPermission(Integer userId, Integer orgId) {
        CloudRoleDo roleDo = roleService.getUserRole(userId);
        if (Objects.isNull(roleDo)) {
            return false;
        }
        CloudUserDo cloudUserDo = cloudUserService.getById(userId);
        CloudOrganizationDo orgDo = cloudOrganizationService.getById(cloudUserDo.getOrganizationId());
        if (Objects.equals(roleDo.getRoleType(), RoleType.PLATFORM) ||
                (Objects.equals(roleDo.getRoleType(), RoleType.ORG) && Objects.equals(orgDo.getParentId(),
                        KylinCloudManageConstants.TOP_PARENT_ID))) {
            return true;
        }


        if (Objects.equals(roleDo.getRoleType(), RoleType.ORG)) {
            CloudUserDo userDo = cloudUserService.getById(userId);
            CloudOrganizationDo userOrgDo = cloudOrganizationService.getById(userDo.getOrganizationId());
            List<CloudOrganizationDo> childOrgList = cloudOrganizationService.listChildOrgByOrgId(userOrgDo.getId());
            //判断用户子组织列表中是否存在orgId
            //存在则说明拥有权限
            CloudOrganizationDo childOrg =
                    childOrgList.stream().filter(item -> Objects.equals(item.getId(), orgId)).findFirst().orElse(null);
            return Objects.nonNull(childOrg);

        }

        return false;
    }

    @Override
    public CloudOrganizationDo getByUserId(Integer userId) {
        CloudUserDo userDo = cloudUserService.getById(userId);
        return cloudOrganizationService.getById(userDo.getOrganizationId());
    }


    @Override
    public OrgSummaryRespDto orgSummary(BaseOrgParam baseOrgParam, LoginUserVo loginUserVo) {
        CloudVdcDo vdcDo = cloudVdcService.getVdcByOrgId(baseOrgParam.getOrgId());

        OrgSummaryRespDto orgSummary = new OrgSummaryRespDto();
        CloudOrganizationDo organizationDo = cloudOrganizationService.getById(baseOrgParam.getOrgId());
        orgSummary.setOrgName(organizationDo.getOrganizationName());
        orgSummary.setRemark(organizationDo.getRemark());
        orgSummary.setCreateTime(DateUtils.format(organizationDo.getCreateTime(), DateUtils.DATE_ALL_PATTEN));
        orgSummary.setParentOrgName("-");
        if (!Objects.equals(organizationDo.getParentId(), KylinCloudManageConstants.TOP_PARENT_ID)) {
            CloudOrganizationDo parentOrg = cloudOrganizationService.getById(organizationDo.getParentId());
            orgSummary.setParentOrgName(parentOrg.getOrganizationName());
        }

        orgSummary.setVdcName(vdcDo.getVdcName());
        List<Integer> orgIdList = getOrgChildIdList(baseOrgParam.getOrgId());

        List<CloudUserDo> userDoList = userService.listUserByOrgList(orgIdList, null);
        orgSummary.setTotalUser(userDoList.size());
        List<CloudUserDo> activeUserDoList = userService.listUserByOrgList(orgIdList, CloudUserStatus.ACTIVATE);
        orgSummary.setActiveUser(activeUserDoList.size());
        orgSummary.setNoActiveUser(userDoList.size() - activeUserDoList.size());


        //vdc资源使用情况
        VdcUsedResourceDto vdcResourceDto = vdcService.getVdcResourceInfo(vdcDo.getId(), loginUserVo);

        orgSummary.setTotalCpu(vdcResourceDto.getTotalCpu());
        orgSummary.setAllocationCpu(vdcResourceDto.getUsedCpu());
        orgSummary.setSurplusCpu(orgSummary.getTotalCpu() - orgSummary.getAllocationCpu());

        orgSummary.setTotalMem(vdcResourceDto.getTotalMem());
        orgSummary.setMemUnit(vdcResourceDto.getMemUnit());

        orgSummary.setAllocationMem(vdcResourceDto.getUsedMem());
        orgSummary.setSurplusMem(orgSummary.getTotalMem() - orgSummary.getAllocationMem());

        orgSummary.setTotalStorage(vdcResourceDto.getTotalStorage());
        orgSummary.setStorageUnit(vdcResourceDto.getStorageUnit());
        orgSummary.setAllocationStorage(vdcResourceDto.getUsedStorage());
        orgSummary.setSurplusStorage(orgSummary.getTotalStorage() - orgSummary.getAllocationStorage());


        List<McServerVmPageDetailResp> mcServerVmList =
                userMachineService.listUserMachineByUserIdListAndClusterId(0,
                        userDoList.stream().map(CloudUserDo::getId).collect(Collectors.toList()),
                        loginUserVo.getUserName());
        orgSummary.setMachineTotal(mcServerVmList.size());
        List<McServerVmPageDetailResp> runningMachine =
                mcServerVmList.stream().filter(item -> (Objects.equals(item.getStatus(), McServerVmStatus.CONNECTED) ||
                        Objects.equals(item.getStatus(), McServerVmStatus.SUSPEND) ||
                        Objects.equals(item.getStatus(), McServerVmStatus.INSTALLING) || Objects.equals(item.getStatus(),
                        McServerVmStatus.AVAILABLE)))
                        .collect(Collectors.toList());
        orgSummary.setMachineOnline(runningMachine.size());
        orgSummary.setMachineOffline(orgSummary.getMachineTotal() - orgSummary.getMachineOnline());

        return orgSummary;
    }


    @Override
    public List<ParentOrgRespDto> transferCanSelectOrg(LoginUserVo loginUserVo, Integer zoneId) {

        //获取可用区绑定的VDC列表
        List<CloudVdcDo> vdcList = cloudVdcService.vdcListByZone(zoneId);

        if (!vdcList.isEmpty()) {
            //获取绑定可用区的组织列表
            List<CloudOrganizationDo> bindZoneOrgList =
                    cloudOrganizationService.getOrgListByVdcList(vdcList.stream().map(CloudVdcDo::getId).collect(Collectors.toList()));
            List<Integer> bindZoneOrgIdList =
                    bindZoneOrgList.stream().map(CloudOrganizationDo::getId).collect(Collectors.toList());
            //获取登录用户组织
            CloudOrganizationDo loginUserOrg = getByUserId(loginUserVo.getUserId());
            List<CloudOrganizationDo> childOrgList = cloudOrganizationService.listChildOrgByOrgId(loginUserOrg.getId());


            //数据过滤，获取绑定了可用区的登录用户可见的组织列表
            List<CloudOrganizationDo> bindZoneVisibleOrgList =
                    childOrgList.stream().filter(item -> bindZoneOrgIdList.contains(item.getId())).collect(Collectors.toList());
            if (bindZoneVisibleOrgList.isEmpty()) {
                return new ArrayList<>();
            }

            List<ParentOrgRespDto> parentOrgList = new ArrayList<>();
            //用户是否是平台管理用户
            boolean platformUser = userService.judgeIfPlatformUser(loginUserVo.getUserId());
            if (platformUser) {
                CloudOrganizationDo topOrg = cloudOrganizationService.getDefaultTopOrg();
                for (CloudOrganizationDo orgDo : bindZoneVisibleOrgList) {
                    if (Objects.equals(orgDo.getParentId(), topOrg.getId())) {
                        parentOrgList.add(formatParentOrgDto(orgDo, bindZoneVisibleOrgList, null));
                    }
                }
            } else {
                for (CloudOrganizationDo orgDo : bindZoneVisibleOrgList) {
                    if (Objects.equals(orgDo.getId(), loginUserOrg.getId())) {
                        parentOrgList.add(formatParentOrgDto(orgDo, bindZoneVisibleOrgList, null));
                    }
                }
            }

            return parentOrgList;

        }
        return new ArrayList<>();
    }
}
