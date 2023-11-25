package com.hnkylin.cloud.manage.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.hnkylin.cloud.core.common.DateUtils;
import com.hnkylin.cloud.core.common.KylinCommonConstants;
import com.hnkylin.cloud.core.common.PageData;
import com.hnkylin.cloud.core.config.exception.KylinException;
import com.hnkylin.cloud.core.domain.*;
import com.hnkylin.cloud.core.enums.*;
import com.hnkylin.cloud.core.service.*;
import com.hnkylin.cloud.manage.constant.KylinCloudManageConstants;
import com.hnkylin.cloud.manage.constant.KylinHttpResponseOrderConstants;
import com.hnkylin.cloud.manage.entity.LoginUserVo;
import com.hnkylin.cloud.manage.entity.mc.req.*;
import com.hnkylin.cloud.manage.entity.mc.resp.*;
import com.hnkylin.cloud.manage.entity.req.workorder.*;
import com.hnkylin.cloud.manage.entity.resp.network.NetworkConfigRespDto;
import com.hnkylin.cloud.manage.entity.resp.vdc.VdcArchitectureUsedResourceDto;
import com.hnkylin.cloud.manage.entity.resp.vdc.VdcModifyArchitectureResourceRespDto;
import com.hnkylin.cloud.manage.entity.resp.vdc.VdcModifyResourceRespDto;
import com.hnkylin.cloud.manage.entity.resp.vdc.VdcUsedResourceDto;
import com.hnkylin.cloud.manage.entity.resp.workorder.*;
import com.hnkylin.cloud.manage.mapper.WorkOrderMapper;
import com.hnkylin.cloud.manage.service.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by kylin-ksvd on 21-6-22.
 */
@Service
public class WorkOrderServiceImpl implements WorkOrderService {

    @Resource
    private CloudWorkOrderService cloudWorkOrderService;

    @Resource
    private CloudUserService cloudUserService;

    @Resource
    private CloudWorkOrderUserService cloudWorkOrderUserService;

    @Resource
    private CloudWorkOrderDeferredMachineService cloudWorkOrderDeferredMachineService;


    @Resource
    private CloudWorkOrderServerVmService cloudWorkOrderServerVmService;

    @Resource
    private CloudWorkOrderModifyServerVmService cloudWorkOrderModifyServerVmService;

    @Resource
    private CloudWorkOrderServerVmDiskService cloudWorkOrderServerVmDiskService;


    @Resource
    private CloudWorkOrderServerVmNetworkService cloudWorkOrderServerVmNetworkService;

    @Resource
    private CloudOrganizationService cloudOrganizationService;

    @Resource
    private CloudWorkOrderDeferredMachineService deferredMachineService;


    @Resource
    private WorkOrderMapper workOrderMapper;


    @Resource
    private CloudUserMachineService userMachineService;

    @Resource
    private NetworkConfigService networkConfigService;


    @Resource
    private McServerVmService mcServerVmService;

    @Resource
    private CloudWorkOrderServerVmIsoService cloudWorkOrderServerVmIsoService;

    @Resource
    private CloudClusterService cloudClusterService;

    @Resource
    private VdcService vdcService;

    @Resource
    private UserService userService;

    @Resource
    private RoleService roleService;

    @Resource
    private CloudUserRoleService cloudUserRoleService;

    @Resource
    private CloudWorkOrderVdcService cloudWorkOrderVdcService;

    @Resource
    private CloudWorkOrderVdcCpuMemService cloudWorkOrderVdcCpuMemService;

    @Resource
    private CloudVdcService cloudVdcService;

    @Resource
    private OrgService orgService;

    @Resource
    private CloudVdcStorageService cloudVdcStorageService;

    @Resource
    private CloudVdcCpuService cloudVdcCpuService;

    @Resource
    private CloudVdcMemService cloudVdcMemService;

    @Override
    public PageData<PageWorkOrderRespDto> pageWorkOrder(WorkOrderPageParam workOrderPageParam,
                                                        LoginUserVo loginUserVo) {


        //根据登录用户，登录用户可见的工单
        List<CloudUserDo> userDoList = userService.userVisibleUserList(loginUserVo.getUserId());
        List<Integer> userIdList = userDoList.stream().map(CloudUserDo::getId).collect(Collectors.toList());
        workOrderPageParam.setVisibleUserIdList(userIdList);

        PageHelper.startPage(workOrderPageParam.getPageNo(), workOrderPageParam.getPageSize());
        List<PageWorkOrderRespDto> list = workOrderMapper.pageWorkOrder(workOrderPageParam);
        PageInfo<PageWorkOrderRespDto> pageInfo = new PageInfo<>(list);

        pageInfo.getList().forEach(workOrder -> {
            workOrder.setTypeDesc(workOrder.getType().getDesc());
            workOrder.setStatusDesc(workOrder.getStatus().getDesc());
            if (StringUtils.isBlank(workOrder.getApplyUser())) {
                workOrder.setApplyUser(workOrder.getUserName());
            }
        });
        PageData pageData = new PageData(pageInfo);
        return pageData;
    }


    @Override
    @Transactional
    public void batchCheck(BatchCheckParam batchCheckParam, LoginUserVo loginUserVo) {
        CloudWorkOrderDo wrapperDo = new CloudWorkOrderDo();
        wrapperDo.setStatus(WorkOrderStatus.WAIT_CHECK);
        QueryWrapper<CloudWorkOrderDo> wrapper = new QueryWrapper<>(wrapperDo);
        wrapper.in("id", batchCheckParam.getWorkOrderIds());
        List<CloudWorkOrderDo> workOrderDoList = cloudWorkOrderService.list(wrapper);
        if (workOrderDoList.size() > 0) {

            CommonCheckParam commonCheckParam = new CommonCheckParam();
            commonCheckParam.setAuditOpinion(batchCheckParam.getAuditOpinion());
            workOrderDoList.forEach(workOrderDo -> {
                commonCheckParam.setWorkOrderId(workOrderDo.getId());

                if (Objects.equals(batchCheckParam.getCheckStatus(), WorkOrderStatus.CHECK_NO_PASS)) {
                    refuseWorkOrder(commonCheckParam, loginUserVo);
                } else {
                    //判断工单类型，遍历进行对应的审核
                    switch (workOrderDo.getType()) {
                        case REGISTER_USER:
                            checkPassRegister(commonCheckParam, loginUserVo);
                            break;
                        case MODIFY_USER:
                            checkPassUpdateUser(commonCheckParam, loginUserVo);
                            break;
                        case DEFERRED_SERVERVM:
                            checkPassDeferred(commonCheckParam, loginUserVo);
                            break;
                        default:
                            break;
                    }
                }


            });
        }
    }

    @Override
    @Transactional
    public void checkPassRegister(CommonCheckParam commonCheckParam, LoginUserVo loginUserVo) {

        CloudWorkOrderDo cloudWorkOrderDo = cloudWorkOrderService.getById(commonCheckParam.getWorkOrderId());
        judgeAlreadyCheck(cloudWorkOrderDo);
        formatCommonCheckWorkOrder(cloudWorkOrderDo, WorkOrderStatus.CHECK_PASS, commonCheckParam
                .getAuditOpinion(), loginUserVo);
        cloudWorkOrderService.updateById(cloudWorkOrderDo);
        //审核通过，激活用户
        CloudUserDo cloudUserDo = cloudUserService.getById(cloudWorkOrderDo.getUserId());
        cloudUserDo.setStatus(CloudUserStatus.ACTIVATE);
        cloudUserDo.setUpdateBy(loginUserVo.getUserId());
        cloudUserDo.setUpdateTime(new Date());
        cloudUserService.updateById(cloudUserDo);

        //插入,自服务用户，用户角色关联表
        CloudRoleDo selfServiceRole = roleService.getSelfServiceRole();
        CloudUserRoleDo userRoleDo = new CloudUserRoleDo();
        userRoleDo.setRoleId(selfServiceRole.getId());
        userRoleDo.setUserId(cloudUserDo.getId());
        userRoleDo.setCreateBy(loginUserVo.getUserId());
        userRoleDo.setCreateTime(new Date());
        cloudUserRoleService.save(userRoleDo);

    }

    /*
     *  判断工单是否已审核过，防止重复审核
     */
    private void judgeAlreadyCheck(CloudWorkOrderDo cloudWorkOrderDo) {
        if (!Objects.equals(cloudWorkOrderDo.getStatus(), WorkOrderStatus.WAIT_CHECK)) {
            throw new KylinException(KylinHttpResponseOrderConstants.WORK_ORDER_ALREADY_CHECK);
        }
    }

    /**
     * 封装基础审核信息
     */
    private void formatCommonCheckWorkOrder(CloudWorkOrderDo cloudWorkOrderDo, WorkOrderStatus status, String
            auditOpinion, LoginUserVo loginUserVo) {
        Date now = new Date();
        cloudWorkOrderDo.setStatus(status);
        cloudWorkOrderDo.setAuditOpinion(auditOpinion);
        cloudWorkOrderDo.setAuditBy(loginUserVo.getUserId());
        cloudWorkOrderDo.setAuditTime(now);
        cloudWorkOrderDo.setUpdateBy(loginUserVo.getUserId());
        cloudWorkOrderDo.setUpdateTime(now);

    }

    @Override
    @Transactional
    public void checkPassUpdateUser(CommonCheckParam commonCheckParam, LoginUserVo loginUserVo) {
        CloudWorkOrderDo cloudWorkOrderDo = cloudWorkOrderService.getById(commonCheckParam.getWorkOrderId());
        judgeAlreadyCheck(cloudWorkOrderDo);
        formatCommonCheckWorkOrder(cloudWorkOrderDo, WorkOrderStatus.CHECK_PASS, commonCheckParam
                .getAuditOpinion(), loginUserVo);
        cloudWorkOrderService.updateById(cloudWorkOrderDo);
        //审核通过，修改用户真实姓名
        CloudWorkOrderUserDo wrapperDo = new CloudWorkOrderUserDo();
        wrapperDo.setWorkOrderId(commonCheckParam.getWorkOrderId());
        QueryWrapper<CloudWorkOrderUserDo> wrapper = new QueryWrapper<>(wrapperDo);
        CloudWorkOrderUserDo cloudWorkOrderUserDo = cloudWorkOrderUserService.getOne(wrapper);

        //设置用户真实姓名为新真实姓名
        CloudUserDo cloudUserDo = cloudUserService.getById(cloudWorkOrderDo.getUserId());
        cloudUserDo.setRealName(cloudWorkOrderUserDo.getNewRealName());
        cloudUserDo.setUpdateBy(loginUserVo.getUserId());
        cloudUserDo.setUpdateTime(new Date());
        cloudUserService.updateById(cloudUserDo);

    }

    @Override
    @Transactional
    public void checkPassDeferred(CommonCheckParam commonCheckParam, LoginUserVo loginUserVo) {
        CloudWorkOrderDo cloudWorkOrderDo = cloudWorkOrderService.getById(commonCheckParam.getWorkOrderId());
        judgeAlreadyCheck(cloudWorkOrderDo);
        formatCommonCheckWorkOrder(cloudWorkOrderDo, WorkOrderStatus.CHECK_PASS, commonCheckParam
                .getAuditOpinion(), loginUserVo);
        cloudWorkOrderService.updateById(cloudWorkOrderDo);

        //审核通过，增加用户云服务器到期时间
        CloudWorkOrderDeferredMachineDo wrapperDo = new CloudWorkOrderDeferredMachineDo();
        wrapperDo.setWorkOrderId(commonCheckParam.getWorkOrderId());
        QueryWrapper<CloudWorkOrderDeferredMachineDo> wrapper = new QueryWrapper<>(wrapperDo);

        CloudWorkOrderDeferredMachineDo deferredMachineDo = cloudWorkOrderDeferredMachineService.getOne(wrapper);


        CloudUserMachineDo userMachineDo = getUserMachineByUuid(deferredMachineDo.getUserMachineUuid(),
                cloudWorkOrderDo.getUserId());

        userMachineDo.setDeadlineTime(deferredMachineDo.getDeadlineTime());
        //新的截至时间大于当期时间，则该用户已拥有云服务器为非过期
        if (deferredMachineDo.getDeadlineTime().getTime() > System.currentTimeMillis()) {
            userMachineDo.setDeadlineFlag(Boolean.FALSE);
        }
        userMachineDo.setUpdateBy(loginUserVo.getUserId());
        userMachineDo.setUpdateTime(new Date());
        userMachineService.updateById(userMachineDo);
    }


    /**
     * 获取用户云服务器关系记录
     *
     * @param uuid
     * @param userId
     * @return
     */
    private CloudUserMachineDo getUserMachineByUuid(String uuid, Integer userId) {
        CloudUserMachineDo cloudUserMachineDo = new CloudUserMachineDo();
        cloudUserMachineDo.setUserId(userId);
        cloudUserMachineDo.setMachineUuid(uuid);
        QueryWrapper<CloudUserMachineDo> queryWrapper = new QueryWrapper<>(cloudUserMachineDo);
        CloudUserMachineDo userMachineDo = userMachineService.getOne(queryWrapper);
        return userMachineDo;
    }

    @Override
    public void refuseWorkOrder(CommonCheckParam commonCheckParam, LoginUserVo loginUserVo) {
        CloudWorkOrderDo cloudWorkOrderDo = cloudWorkOrderService.getById(commonCheckParam.getWorkOrderId());
        judgeAlreadyCheck(cloudWorkOrderDo);
        formatCommonCheckWorkOrder(cloudWorkOrderDo, WorkOrderStatus.CHECK_NO_PASS, commonCheckParam
                .getAuditOpinion(), loginUserVo);
        cloudWorkOrderService.updateById(cloudWorkOrderDo);
        //如果是拒绝注册用户审核，则将申请用户标记为已删除
        if (Objects.equals(cloudWorkOrderDo.getType(), WorkOrderType.REGISTER_USER)) {
            CloudUserDo cloudUserDo = cloudUserService.getById(cloudWorkOrderDo.getUserId());
            cloudUserDo.setDeleteFlag(true);
            cloudUserDo.setDeleteBy(loginUserVo.getUserId());
            cloudUserDo.setDeleteTime(new Date());
            cloudUserService.updateById(cloudUserDo);
        }

    }

    private void formatBaseWorkOrderDetail(Integer workOrderId, BaseWorkOrderDetailDto baseWorkOrderDetailDto) {
        CloudWorkOrderDo cloudWorkOrderDo = cloudWorkOrderService.getById(workOrderId);
        CloudUserDo cloudUserDo = cloudUserService.getById(cloudWorkOrderDo.getUserId());
        baseWorkOrderDetailDto.setWorkOrderTarget(cloudWorkOrderDo.getTarget());
        baseWorkOrderDetailDto.setWorkOrderType(cloudWorkOrderDo.getType());
        baseWorkOrderDetailDto.setWorkOrderTypeDesc(cloudWorkOrderDo.getType().getDesc());
        baseWorkOrderDetailDto.setApplyReason(cloudWorkOrderDo.getApplyReason());
        baseWorkOrderDetailDto.setStatus(cloudWorkOrderDo.getStatus());
        baseWorkOrderDetailDto.setStatusDesc(cloudWorkOrderDo.getStatus().getDesc());
        baseWorkOrderDetailDto.setUserId(cloudWorkOrderDo.getUserId());
        baseWorkOrderDetailDto.setApplyUser(cloudUserDo.getUserName());
        if (StringUtils.isNotBlank(cloudUserDo.getRealName())) {
            baseWorkOrderDetailDto.setApplyUser(cloudUserDo.getRealName());
        }

        baseWorkOrderDetailDto.setApplyTime(DateUtils.format(cloudWorkOrderDo.getCreateTime(),
                DateUtils.DATE_ALL_PATTEN));
        baseWorkOrderDetailDto.setAuditOpinion(cloudWorkOrderDo.getAuditOpinion());

        if (Objects.nonNull(cloudWorkOrderDo.getAuditTime())) {
            baseWorkOrderDetailDto.setAuditionTime(DateUtils.format(cloudWorkOrderDo.getAuditTime(),
                    DateUtils.DATE_ALL_PATTEN));
        }
        if (!Objects.equals(cloudWorkOrderDo.getStatus(), WorkOrderStatus.WAIT_CHECK) && Objects.nonNull(cloudWorkOrderDo.getAuditBy()) && cloudWorkOrderDo.getAuditBy() > 0) {
            CloudUserDo auditionUser = cloudUserService.getById(cloudWorkOrderDo.getAuditBy());
            baseWorkOrderDetailDto.setAuditionUser(auditionUser.getUserName());
            if (StringUtils.isNotBlank(auditionUser.getRealName())) {
                baseWorkOrderDetailDto.setAuditionUser(auditionUser.getRealName());
            }

        }
        CloudOrganizationDo organizationDo = cloudOrganizationService.getById(cloudUserDo.getOrganizationId());
        baseWorkOrderDetailDto.setOrgName(organizationDo.getOrganizationName());

    }

    @Override
    public ApplyServerVmDetailRespDto getApplyServerVmDetailByWorkOrderId(Integer workOrderId,
                                                                          LoginUserVo loginUserVo) {
        ApplyServerVmDetailRespDto applyServerVmDetailRespDto = new ApplyServerVmDetailRespDto();

        formatBaseWorkOrderDetail(workOrderId, applyServerVmDetailRespDto);

        CloudWorkOrderDo cloudWorkOrderDo = cloudWorkOrderService.getById(workOrderId);
        //查询服务器申请详情表
        CloudWorkOrderServerVmDo queryServerVmDo = new CloudWorkOrderServerVmDo();
        queryServerVmDo.setWorkOrderId(workOrderId);
        QueryWrapper<CloudWorkOrderServerVmDo> wrapper = new QueryWrapper<>(queryServerVmDo);
        CloudWorkOrderServerVmDo serverVmDo = cloudWorkOrderServerVmService.getOne(wrapper);

        if (Objects.nonNull(serverVmDo)) {
            applyServerVmDetailRespDto.setApplyNum(serverVmDo.getApplyNum());
            applyServerVmDetailRespDto.setApplyServerVmType(serverVmDo.getApplyServervmType());
            applyServerVmDetailRespDto.setIfModifyApplyNum(false);
            applyServerVmDetailRespDto.setModifyApplyNum(serverVmDo.getModifyApplyNum());

            applyServerVmDetailRespDto.setUseMonth(serverVmDo.getUseMonth());
            applyServerVmDetailRespDto.setDeadlineType(serverVmDo.getDeadlineType());
            applyServerVmDetailRespDto.setDeadlineTypeDesc(serverVmDo.getDeadlineType().getDesc());
            applyServerVmDetailRespDto.setOsMachine(serverVmDo.getOsMachine());
            applyServerVmDetailRespDto.setArchitecture(serverVmDo.getArchitecture());


            applyServerVmDetailRespDto.setCpu(serverVmDo.getCpu());
            applyServerVmDetailRespDto.setModifyCpu(serverVmDo.getModifyCpu());
            applyServerVmDetailRespDto.setIfModifyCpu(false);
            applyServerVmDetailRespDto.setMem(serverVmDo.getMem());
            applyServerVmDetailRespDto.setModifyMem(serverVmDo.getModifyMem());
            applyServerVmDetailRespDto.setIfModifyMem(false);
            applyServerVmDetailRespDto.setDescription(serverVmDo.getDescription());
            applyServerVmDetailRespDto.setMcCloneType(serverVmDo.getCloneType());
            applyServerVmDetailRespDto.setClusterName(cloudClusterService.getById(serverVmDo.getClusterId()).getName());

            if (Objects.equals(cloudWorkOrderDo.getStatus(), WorkOrderStatus.CHECK_PASS)) {
                if (!Objects.equals(serverVmDo.getCpu(), serverVmDo.getModifyCpu())) {
                    applyServerVmDetailRespDto.setIfModifyCpu(true);
                }
                if (!Objects.equals(serverVmDo.getMem(), serverVmDo.getModifyMem())) {
                    applyServerVmDetailRespDto.setIfModifyMem(true);
                }
                if (!Objects.equals(serverVmDo.getApplyNum(), serverVmDo.getModifyApplyNum())) {
                    applyServerVmDetailRespDto.setIfModifyApplyNum(true);
                }

            }
            applyServerVmDetailRespDto.setMenUtil(serverVmDo.getMemUnit());
            //查询硬盘信息

            List<CloudWorkOrderServerVmDiskDo> diskDoList = getDisksByWorkOrderId(workOrderId);
            List<ServerVmDiskDto> disks = new ArrayList<>();
            if (diskDoList.size() > 0) {
                diskDoList.forEach(disk -> {
                    ServerVmDiskDto diskDto = new ServerVmDiskDto();
                    diskDto.setDiskSize(disk.getDiskSize());
                    diskDto.setPurpose(disk.getPurpose());
                    diskDto.setOldDiskSize(disk.getOldDiskSize());
                    diskDto.setModifyType(disk.getModifyType());
                    disks.add(diskDto);
                });
            }
            applyServerVmDetailRespDto.setDisks(disks);

            //查询网卡信息
            List<CloudWorkOrderServerVmNetworkDo> networkDoList = getNetworkListByWorkOrderId(workOrderId);
            List<ServerVmNetworkDto> networks = new ArrayList<>();
            networkDoList.forEach(network -> {
                ServerVmNetworkDto serverVmNetworkDto = new ServerVmNetworkDto();
                serverVmNetworkDto.setPurpose(network.getPurpose());
                serverVmNetworkDto.setModifyType(network.getModifyType());
                serverVmNetworkDto.setIpBindMac(network.getIpBindMac());
                serverVmNetworkDto.setManualSetIp(network.getManualSetIp());
                serverVmNetworkDto.setAutomaticAcqIp(network.getAutomaticAcqIp());
                serverVmNetworkDto.setIp(network.getIp());
                serverVmNetworkDto.setMask(network.getMask());
                serverVmNetworkDto.setGw(network.getGw());
                serverVmNetworkDto.setDns1(network.getDns1());
                serverVmNetworkDto.setDns2(network.getDns2());
                boolean setIpInfo = network.getIpBindMac() || network.getManualSetIp() || network.getAutomaticAcqIp();
                serverVmNetworkDto.setSetIpInfo(setIpInfo);
                networks.add(serverVmNetworkDto);
            });
            applyServerVmDetailRespDto.setNetworks(networks);


            List<CloudWorkOrderServerVmIsoDo> isoDoList = getIsoByWorkOrderId(serverVmDo.getWorkOrderId());
            List<ServerVmIsoDto> isoDtoList = new ArrayList<>();
            if (!isoDoList.isEmpty()) {
                isoDoList.stream().forEach(iso -> {
                    ServerVmIsoDto vmIsoDto = new ServerVmIsoDto();
                    vmIsoDto.setIsoFile(iso.getIsoFile());
                    vmIsoDto.setModifyType(iso.getModifyType());
                    vmIsoDto.setOldIsoFile(iso.getOldIsoFile());
                    isoDtoList.add(vmIsoDto);
                });
            }
            applyServerVmDetailRespDto.setIsoList(isoDtoList);

            if (Objects.equals(serverVmDo.getApplyServervmType(), ApplyServerVmType.TEMPLATE)) {
                //获取模板名称
                QueryMcServerDetailParamReq queryMcServerDetailParamReq = new QueryMcServerDetailParamReq();
                queryMcServerDetailParamReq.setServervmId(serverVmDo.getTemplateId());
                McServerVmDetailResp mcServerVmDetailResp =
                        mcServerVmService.getMcServerVmDetailByServerVmId(serverVmDo.getClusterId(),
                                queryMcServerDetailParamReq,
                                loginUserVo);
                applyServerVmDetailRespDto.setTemplateName(mcServerVmDetailResp.getAlisname());
            }

        }

        return applyServerVmDetailRespDto;
    }

    @Override
    public ModifyServerVmDetailRespDto getModifyServerVmDetailByWorkOrderId(Integer workOrderId) {
        ModifyServerVmDetailRespDto modifyServerVmDetailRespDto = new ModifyServerVmDetailRespDto();

        formatBaseWorkOrderDetail(workOrderId, modifyServerVmDetailRespDto);

        //查询变更云服务器详情
        CloudWorkOrderModifyServerVmDo modifyServerVmDo = new CloudWorkOrderModifyServerVmDo();
        modifyServerVmDo.setWorkOrderId(workOrderId);
        QueryWrapper<CloudWorkOrderModifyServerVmDo> wrapper = new QueryWrapper<>(modifyServerVmDo);
        CloudWorkOrderModifyServerVmDo serverVmDo = cloudWorkOrderModifyServerVmService.getOne(wrapper);

        //查询用户已经拥有的云服务器
        CloudWorkOrderDo cloudWorkOrderDo = cloudWorkOrderService.getById(workOrderId);

        CloudUserMachineDo cloudUserMachineDo = new CloudUserMachineDo();
        cloudUserMachineDo.setUserId(cloudWorkOrderDo.getUserId());
        cloudUserMachineDo.setMachineUuid(serverVmDo.getMachineUuid());
        QueryWrapper<CloudUserMachineDo> queryWrapper = new QueryWrapper<>(cloudUserMachineDo);

        CloudUserMachineDo userMachineDo = userMachineService.getOne(queryWrapper);
        if (Objects.nonNull(userMachineDo)) {


            String deadLineTime = DateUtils.format(serverVmDo.getDeadlineTime(), DateUtils.DATE_ALL_PATTEN);

            modifyServerVmDetailRespDto.setDeadLineTime(deadLineTime);
            modifyServerVmDetailRespDto.setDeadlineType(serverVmDo.getDeadlineType());
            modifyServerVmDetailRespDto.setDeadlineTypeDesc(serverVmDo.getDeadlineType().getDesc());
            modifyServerVmDetailRespDto.setOsMachine(serverVmDo.getOsMachine());
            modifyServerVmDetailRespDto.setArchitecture(serverVmDo.getArchitecture());
            modifyServerVmDetailRespDto.setCpu(serverVmDo.getCpu());
            modifyServerVmDetailRespDto.setIfModifyCpu(!Objects.equals(serverVmDo.getCpu(),
                    serverVmDo.getOriginalCpu()));
            modifyServerVmDetailRespDto.setOriginalCpu(serverVmDo.getOriginalCpu());

            modifyServerVmDetailRespDto.setMem(serverVmDo.getMem());
            modifyServerVmDetailRespDto.setMenUtil(serverVmDo.getMemUnit());
            modifyServerVmDetailRespDto.setOriginalMem(serverVmDo.getOriginalMem());
            modifyServerVmDetailRespDto.setIfModifyMem(!Objects.equals(serverVmDo.getMem(),
                    serverVmDo.getOriginalMem()));


            //查询硬盘信息
            CloudWorkOrderServerVmDiskDo queryDiskDo = new CloudWorkOrderServerVmDiskDo();
            queryDiskDo.setWorkOrderId(workOrderId);
            QueryWrapper<CloudWorkOrderServerVmDiskDo> diskQueryWrapper = new QueryWrapper<>(queryDiskDo);
            List<CloudWorkOrderServerVmDiskDo> diskDoList = cloudWorkOrderServerVmDiskService.list(diskQueryWrapper);
            List<ServerVmDiskDto> disks = new ArrayList<>();
            if (diskDoList.size() > 0) {
                diskDoList.forEach(disk -> {
                    ServerVmDiskDto diskDto = new ServerVmDiskDto();
                    diskDto.setDiskSize(disk.getDiskSize());
                    diskDto.setPurpose(disk.getPurpose());
                    diskDto.setModifyType(disk.getModifyType());
                    diskDto.setOldDiskSize(disk.getOldDiskSize());
                    disks.add(diskDto);
                });
            }
            modifyServerVmDetailRespDto.setDisks(disks);

            //查询网卡信息
            CloudWorkOrderServerVmNetworkDo queryNetworkDo = new CloudWorkOrderServerVmNetworkDo();
            queryNetworkDo.setWorkOrderId(workOrderId);
            QueryWrapper<CloudWorkOrderServerVmNetworkDo> networkQueryWrapper = new QueryWrapper<>(queryNetworkDo);
            List<CloudWorkOrderServerVmNetworkDo> networkDoList =
                    cloudWorkOrderServerVmNetworkService.list(networkQueryWrapper);
            List<ServerVmNetworkDto> networks = new ArrayList<>();
            networkDoList.forEach(network -> {
                ServerVmNetworkDto serverVmNetworkDto = new ServerVmNetworkDto();
                serverVmNetworkDto.setPurpose(network.getPurpose());
                serverVmNetworkDto.setModifyType(network.getModifyType());
                serverVmNetworkDto.setIpBindMac(network.getIpBindMac());
                serverVmNetworkDto.setManualSetIp(network.getManualSetIp());
                serverVmNetworkDto.setAutomaticAcqIp(network.getAutomaticAcqIp());
                serverVmNetworkDto.setIp(network.getIp());
                serverVmNetworkDto.setMask(network.getMask());
                serverVmNetworkDto.setGw(network.getGw());
                serverVmNetworkDto.setDns1(network.getDns1());
                serverVmNetworkDto.setDns2(network.getDns2());
                boolean setIpInfo = network.getIpBindMac() || network.getManualSetIp() || network.getAutomaticAcqIp();
                serverVmNetworkDto.setSetIpInfo(setIpInfo);
                networks.add(serverVmNetworkDto);
            });
            modifyServerVmDetailRespDto.setNetworks(networks);

        }
        return modifyServerVmDetailRespDto;
    }

    @Override
    public WorkOrderUserDetailRespDto getWorkOrderUserDetailByWorkOrderId(Integer workOrderId) {
        WorkOrderUserDetailRespDto workOrderUserDetailRespDto = new WorkOrderUserDetailRespDto();

        formatBaseWorkOrderDetail(workOrderId, workOrderUserDetailRespDto);

        CloudUserDo cloudUserDo = cloudUserService.getById(workOrderUserDetailRespDto.getUserId());
        workOrderUserDetailRespDto.setUserName(cloudUserDo.getUserName());
        workOrderUserDetailRespDto.setRealName(cloudUserDo.getRealName());
        workOrderUserDetailRespDto.setMobile(cloudUserDo.getMobile());
        if (cloudUserDo.getOrganizationId() > 0) {
            workOrderUserDetailRespDto.setOrganizationName(cloudOrganizationService.getById(cloudUserDo
                    .getOrganizationId()).getOrganizationName());
        }
        //修改账号，需要查询旧真实姓名，及新真实姓名
        if (Objects.equals(workOrderUserDetailRespDto.getWorkOrderType(), WorkOrderType.MODIFY_USER)) {
            CloudWorkOrderUserDo queryDo = new CloudWorkOrderUserDo();
            queryDo.setWorkOrderId(workOrderId);
            QueryWrapper<CloudWorkOrderUserDo> wrapper = new QueryWrapper<>(queryDo);
            CloudWorkOrderUserDo cloudWorkOrderUserDo = cloudWorkOrderUserService.getOne(wrapper);
            workOrderUserDetailRespDto.setNewRealName(cloudWorkOrderUserDo.getNewRealName());
            workOrderUserDetailRespDto.setRealName(cloudWorkOrderUserDo.getOldRealName());
        }
        return workOrderUserDetailRespDto;
    }


    /**
     * 审核-云服务器时封装相同的孕妇器详情 封装相同的mc云服务器基础信息
     *
     * @param mcServerVmDetailResp
     * @param commonServerVmDetailResp
     */
    private void commonFormatMcServerDetail(McServerVmDetailResp mcServerVmDetailResp,
                                            CommonServerVmDetailResp commonServerVmDetailResp) {

        commonServerVmDetailResp.setArchitecture(mcServerVmDetailResp.getArchitecture());
        commonServerVmDetailResp.setSystemType(mcServerVmDetailResp.getSystemType());
        commonServerVmDetailResp.setOsMachine(mcServerVmDetailResp.getOsMachine());
        commonServerVmDetailResp.setSelectCluster(mcServerVmDetailResp.getSelectCluster());
        commonServerVmDetailResp.setSelectClusterUuid(mcServerVmDetailResp.getSelectClusterUuid());
        commonServerVmDetailResp.setStorageLocationId(mcServerVmDetailResp.getStorageLocationId());
        commonServerVmDetailResp.setSelectTagIds(mcServerVmDetailResp.getSelectTagIds());
        commonServerVmDetailResp.setSelectTagNames(mcServerVmDetailResp.getSelectTagNames());
        if (Objects.equals(mcServerVmDetailResp.getServerClusterType(), McServerClusterType.AUTO.getValue())) {
            commonServerVmDetailResp.setServerClusterType(McServerClusterType.AUTO);
        } else if (Objects.equals(mcServerVmDetailResp.getServerClusterType(), McServerClusterType.CUSTOM.getValue())) {
            commonServerVmDetailResp.setServerClusterType(McServerClusterType.CUSTOM);
        } else if (Objects.equals(mcServerVmDetailResp.getServerClusterType(),
                McServerClusterType.BIND_RESOURCE.getValue())) {
            commonServerVmDetailResp.setServerClusterType(McServerClusterType.BIND_RESOURCE);
        }
    }

    /**
     * 封装云服务器审核时-计算资源，存储资源
     *
     * @param commonServerVmDetailResp
     */
    private void commonFormatMcServerClusterAndLocation(String architecture,
                                                        CommonServerVmDetailResp commonServerVmDetailResp,
                                                        Integer clusterId,
                                                        LoginUserVo loginUserVo, Integer workOrderUserId) {

        //获取mc存储位置列表
        List<McStorageLocationResp> storageLocationList =
                mcServerVmService.getMcStorageLocationList(clusterId, loginUserVo);
        commonServerVmDetailResp.setStorageLocationList(storageLocationList);

        //根据模板架构获取计算资源节点
        QueryMcClusterParamReq queryMcClusterParamReq = new QueryMcClusterParamReq();
        queryMcClusterParamReq.setPlateformType(architecture);
        if (Objects.equals(architecture, McArchitectureType.LOWER_86_64.getName())) {
            queryMcClusterParamReq.setPlateformType(McArchitectureType.X86_64.name());
        }

        List<McClusterResp> clusterList = mcServerVmService.getMcClusterList(clusterId,
                queryMcClusterParamReq, loginUserVo);
        commonServerVmDetailResp.setClusterList(clusterList);
        //查询mc中主机绑定资源
        List<McClusterBindResource> clusterBindResourceList =
                mcServerVmService.getMcClusterBindResourceList(clusterId, loginUserVo);
        commonServerVmDetailResp.setClusterBindResourceList(clusterBindResourceList);

        //查询云管中网络配置信息

        CloudVdcDo vdcDo = vdcService.getUserOrgBindVdc(workOrderUserId);
        List<NetworkConfigRespDto> networkList =
                networkConfigService.listNetworkListByVdcIdAndClusterId(vdcDo.getId(), clusterId);
        commonServerVmDetailResp.setNetworkConfigList(networkList);
    }

    @Override
    public PassApplyServerVmDetailRespDto passApplyServerVmDetailRespDto(WorkOrderDetailParam workOrderDetailParam,
                                                                         LoginUserVo loginUserVo) {

        PassApplyServerVmDetailRespDto passApplyServerVmDetailRespDto = new PassApplyServerVmDetailRespDto();
        CloudWorkOrderDo cloudWorkOrderDo = cloudWorkOrderService.getById(workOrderDetailParam.getWorkOrderId());
        judgeAlreadyCheck(cloudWorkOrderDo);

        //查询服务器申请详情表
        CloudWorkOrderServerVmDo queryServerVmDo = new CloudWorkOrderServerVmDo();
        queryServerVmDo.setWorkOrderId(workOrderDetailParam.getWorkOrderId());
        QueryWrapper<CloudWorkOrderServerVmDo> wrapper = new QueryWrapper<>(queryServerVmDo);
        CloudWorkOrderServerVmDo serverVmDo = cloudWorkOrderServerVmService.getOne(wrapper);

        passApplyServerVmDetailRespDto.setAliasName(serverVmDo.getServervmName());
        passApplyServerVmDetailRespDto.setApplyServerVmType(serverVmDo.getApplyServervmType());
        passApplyServerVmDetailRespDto.setCpu(serverVmDo.getCpu());
        passApplyServerVmDetailRespDto.setMem(serverVmDo.getMem());
        passApplyServerVmDetailRespDto.setMemUnit(serverVmDo.getMemUnit());
        passApplyServerVmDetailRespDto.setApplyNum(serverVmDo.getApplyNum());
        passApplyServerVmDetailRespDto.setTemplateId(serverVmDo.getTemplateId());
        passApplyServerVmDetailRespDto.setArchitecture(serverVmDo.getArchitecture());
        passApplyServerVmDetailRespDto.setSystemType(serverVmDo.getSystemType());
        passApplyServerVmDetailRespDto.setOsMachine(serverVmDo.getOsMachine());

        List<McServerVmNetworkDetailResp> templateInterfaceList = new ArrayList<McServerVmNetworkDetailResp>();
        if (Objects.equals(serverVmDo.getApplyServervmType(), ApplyServerVmType.TEMPLATE)) {
            //获取模板信息
            QueryMcServerDetailParamReq queryMcServerDetailParamReq = new QueryMcServerDetailParamReq();
            queryMcServerDetailParamReq.setServervmId(serverVmDo.getTemplateId());
            McServerVmDetailResp mcServerVmDetailResp =
                    mcServerVmService.getMcServerVmDetailByServerVmId(serverVmDo.getClusterId(),
                            queryMcServerDetailParamReq,
                            loginUserVo);
            passApplyServerVmDetailRespDto.setTemplateName(mcServerVmDetailResp.getAlisname());
            commonFormatMcServerDetail(mcServerVmDetailResp, passApplyServerVmDetailRespDto);
            //模板中网卡信息
            templateInterfaceList = mcServerVmDetailResp.getInterfaceList();
        }

        //passApplyServerVmDetailRespDto.setDisks(mcServerVmDetailResp.getDisks());
        passApplyServerVmDetailRespDto.setDisks(new ArrayList<>());
        //查询申请时自定义硬盘信息
        List<CloudWorkOrderServerVmDiskDo> diskDoList = getDisksByWorkOrderId(serverVmDo.getWorkOrderId());
        if (diskDoList.size() > 0) {
            diskDoList.forEach(disk -> {
                McServerVmDiskDetailResp diskDto = new McServerVmDiskDetailResp();
                diskDto.setDiskSize(disk.getDiskSize());
                diskDto.setType(disk.getType());
                diskDto.setApplyId(disk.getId());
                diskDto.setModifyType(ModifyType.NONE);
                passApplyServerVmDetailRespDto.getDisks().add(diskDto);
            });
        }

        passApplyServerVmDetailRespDto.setInterfaceList(new ArrayList<>());
        //查询申请时的网卡列表
        List<CloudWorkOrderServerVmNetworkDo> networkDoList = getNetworkListByWorkOrderId(serverVmDo.getWorkOrderId());
        for (CloudWorkOrderServerVmNetworkDo network : networkDoList) {
            if (Objects.equals(network.getType(), ApplyMcServerVmType.custom)) {
                McServerVmNetworkDetailResp networkDetailResp = new McServerVmNetworkDetailResp();
                networkDetailResp.setPurpose(network.getPurpose());
                networkDetailResp.setType(ApplyMcServerVmType.custom);
                networkDetailResp.setApplyId(network.getId());
                networkDetailResp.setModifyType(ModifyType.NONE);
                passApplyServerVmDetailRespDto.getInterfaceList().add(networkDetailResp);
            } else {
                if (!templateInterfaceList.isEmpty()) {
                    McServerVmNetworkDetailResp templateInterface =
                            templateInterfaceList.stream().filter(item -> Objects.equals(item.getId(),
                                    network.getInterfaceId()))
                                    .findFirst().orElse(null);
                    if (Objects.nonNull(templateInterface)) {
                        templateInterface.setPurpose(formatNetworkStr(templateInterface.getInterfaceType(),
                                templateInterface.getPortGroup(),
                                templateInterface.getVirtualSwitch(), templateInterface.getModelType()));
                        templateInterface.setApplyId(network.getId());
                        templateInterface.setModifyType(ModifyType.NONE);
                        passApplyServerVmDetailRespDto.getInterfaceList().add(templateInterface);
                    }
                }

            }
        }


        commonFormatMcServerClusterAndLocation(passApplyServerVmDetailRespDto.getArchitecture(),
                passApplyServerVmDetailRespDto,
                serverVmDo.getClusterId(), loginUserVo, cloudWorkOrderDo.getUserId());

        if (Objects.equals(serverVmDo.getApplyServervmType(), ApplyServerVmType.ISO)) {
            //iso光驱文件
            List<CloudWorkOrderServerVmIsoDo> isoDoList = getIsoByWorkOrderId(serverVmDo.getWorkOrderId());
            List<ApplyServerVmIsoDetailResp> applyIsoList = new ArrayList<>();
            if (!isoDoList.isEmpty()) {
                isoDoList.stream().forEach(iso -> {
                    ApplyServerVmIsoDetailResp isoDetail = new ApplyServerVmIsoDetailResp();
                    isoDetail.setApplyId(iso.getId());
                    isoDetail.setApplyModifyType(iso.getModifyType());
                    isoDetail.setModifyType(iso.getModifyType());
                    isoDetail.setIsoFile(iso.getIsoFile());
                    applyIsoList.add(isoDetail);
                });
            }
            passApplyServerVmDetailRespDto.setApplyIsoList(applyIsoList);

            //mc所以光驱文件
            passApplyServerVmDetailRespDto.setAllIsoList(mcServerVmService.mcAllIsoList(serverVmDo.getClusterId(),
                    loginUserVo));

            List<String> selectClusterList =
                    passApplyServerVmDetailRespDto.getClusterList().stream().map(McClusterResp::getServerAddr).collect(Collectors.toList());
            List<String> selectClusterUUIDList =
                    passApplyServerVmDetailRespDto.getClusterList().stream().map(McClusterResp::getServerId).collect(Collectors.toList());
            passApplyServerVmDetailRespDto.setSelectCluster(String.join(",", selectClusterList));
            passApplyServerVmDetailRespDto.setSelectClusterUuid(String.join(",", selectClusterUUIDList));
            passApplyServerVmDetailRespDto.setStorageLocationId(passApplyServerVmDetailRespDto.getStorageLocationList().get(0).getId());
        }


        return passApplyServerVmDetailRespDto;
    }


    @Override
    public PassModifyServerVmDetailRespDto passModifyServerVmDetail(WorkOrderDetailParam workOrderDetailParam,
                                                                    LoginUserVo loginUserVo) {

        PassModifyServerVmDetailRespDto passModifyServerVmDetailRespDto = new PassModifyServerVmDetailRespDto();
        CloudWorkOrderDo cloudWorkOrderDo = cloudWorkOrderService.getById(workOrderDetailParam.getWorkOrderId());
        judgeAlreadyCheck(cloudWorkOrderDo);

        //查询变更云服务器申请表
        CloudWorkOrderModifyServerVmDo queryServerVmDo = new CloudWorkOrderModifyServerVmDo();
        queryServerVmDo.setWorkOrderId(workOrderDetailParam.getWorkOrderId());
        QueryWrapper<CloudWorkOrderModifyServerVmDo> wrapper = new QueryWrapper<>(queryServerVmDo);
        CloudWorkOrderModifyServerVmDo modifyServerVmDo = cloudWorkOrderModifyServerVmService.getOne(wrapper);


        passModifyServerVmDetailRespDto.setAliasName(modifyServerVmDo.getServervmName());
        passModifyServerVmDetailRespDto.setCpu(modifyServerVmDo.getCpu());
        passModifyServerVmDetailRespDto.setMem(modifyServerVmDo.getMem());
        passModifyServerVmDetailRespDto.setMemUnit(modifyServerVmDo.getMemUnit());


        CloudUserMachineDo userMachineDo = getUserMachineByUuid(modifyServerVmDo.getMachineUuid(),
                cloudWorkOrderDo.getUserId());

        //获取云服务器信息
        QueryMcServerDetailParamReq queryMcServerDetailParamReq = new QueryMcServerDetailParamReq();
        queryMcServerDetailParamReq.setUuid(modifyServerVmDo.getMachineUuid());
        McServerVmDetailResp mcServerVmDetailResp =
                mcServerVmService.getMcServerVmDetailByServerVmUuid(userMachineDo.getClusterId(),
                        queryMcServerDetailParamReq, loginUserVo);


        commonFormatMcServerDetail(mcServerVmDetailResp, passModifyServerVmDetailRespDto);

        passModifyServerVmDetailRespDto.setDisks(new ArrayList<>());
        //现mc中云服务器磁盘信息
        List<McServerVmDiskDetailResp> oldDisks = mcServerVmDetailResp.getDisks();
        //查询申请时自定义硬盘信息
        List<CloudWorkOrderServerVmDiskDo> diskDoList = getDisksByWorkOrderId(modifyServerVmDo.getWorkOrderId());
        //用现在mc中磁盘列表和变更申请是进行对比，
        oldDisks.forEach(oldDisk -> {
            CloudWorkOrderServerVmDiskDo serverVmDiskDo =
                    diskDoList.stream().filter(diskDo -> Objects.equals(oldDisk.getId(),
                            diskDo.getDiskId())).findFirst().orElse(null);
            if (Objects.isNull(serverVmDiskDo)) {
                //说明是变更申请时间，该云服务器在mc中新增的磁盘
                oldDisk.setApplyModifyType(ModifyType.NONE);
                oldDisk.setApplyId(0);
            } else {
                oldDisk.setApplyModifyType(serverVmDiskDo.getModifyType());
                oldDisk.setOldDiskSize(serverVmDiskDo.getOldDiskSize());
                oldDisk.setDiskSize(serverVmDiskDo.getDiskSize());
                oldDisk.setType(serverVmDiskDo.getType());
                oldDisk.setApplyId(serverVmDiskDo.getId());
            }
            oldDisk.setModifyType(ModifyType.NONE);
            passModifyServerVmDetailRespDto.getDisks().add(oldDisk);
        });
        diskDoList.forEach(disk -> {
            if (Objects.equals(disk.getDiskId(), 0L)) {
                McServerVmDiskDetailResp diskDto = new McServerVmDiskDetailResp();
                diskDto.setDiskSize(disk.getDiskSize());
                diskDto.setId(0L);
                diskDto.setType(ApplyMcServerVmType.custom);
                diskDto.setApplyModifyType(ModifyType.ADD);
                diskDto.setApplyId(disk.getId());
                diskDto.setModifyType(ModifyType.NONE);
                passModifyServerVmDetailRespDto.getDisks().add(diskDto);
            }
        });


        //模板中网卡信息
        passModifyServerVmDetailRespDto.setInterfaceList(new ArrayList<>());
        List<McServerVmNetworkDetailResp> templateInterfaceList = mcServerVmDetailResp.getInterfaceList();
        //查询申请是自定义网卡信息
        List<CloudWorkOrderServerVmNetworkDo> networkDoList =
                getNetworkListByWorkOrderId(modifyServerVmDo.getWorkOrderId());
        //用现在mc中网卡列表和变更申请的网卡进行对比，
        templateInterfaceList.forEach(oldInterfaceDo -> {
            CloudWorkOrderServerVmNetworkDo serverVmNetworkDo =
                    networkDoList.stream().filter(network -> Objects.equals(oldInterfaceDo.getId(),
                            network.getInterfaceId())).findFirst().orElse(null);
            oldInterfaceDo.setPurpose(formatNetworkStr(oldInterfaceDo.getInterfaceType(), oldInterfaceDo.getPortGroup(),
                    oldInterfaceDo.getVirtualSwitch(), oldInterfaceDo.getModelType()));
            if (Objects.isNull(serverVmNetworkDo)) {
                //说明是变更申请时间，该云服务器在mc中新增的网卡
                oldInterfaceDo.setApplyModifyType(ModifyType.NONE);
                oldInterfaceDo.setApplyId(0);
            } else {
                oldInterfaceDo.setApplyModifyType(serverVmNetworkDo.getModifyType());
                oldInterfaceDo.setApplyId(serverVmNetworkDo.getId());
            }
            oldInterfaceDo.setModifyType(ModifyType.NONE);
            if (oldInterfaceDo.getAutomaticAcqIp()) {
                oldInterfaceDo.setIp("");
            }
            passModifyServerVmDetailRespDto.getInterfaceList().add(oldInterfaceDo);
        });


        networkDoList.forEach(network -> {
            if (Objects.equals(network.getInterfaceId(), 0L)) {
                //变更申请是新申请的网卡
                McServerVmNetworkDetailResp networkDetailResp = new McServerVmNetworkDetailResp();
                networkDetailResp.setPurpose(network.getPurpose());
                networkDetailResp.setType(ApplyMcServerVmType.custom);
                networkDetailResp.setId(0L);
                networkDetailResp.setModifyType(ModifyType.NONE);
                networkDetailResp.setApplyModifyType(ModifyType.ADD);
                networkDetailResp.setApplyId(network.getId());
                passModifyServerVmDetailRespDto.getInterfaceList().add(networkDetailResp);
            }
        });

        commonFormatMcServerClusterAndLocation(mcServerVmDetailResp.getArchitecture(), passModifyServerVmDetailRespDto,
                userMachineDo.getClusterId(), loginUserVo, cloudWorkOrderDo.getUserId());

        return passModifyServerVmDetailRespDto;
    }

    /*
     *  封装网络信息
     *  如 网络类型(Bridge) 交换机（TPlinksafg）端口组（102）
     */
    private String formatNetworkStr(String interfaceType, String portGroup, String virtualSwitch, String modelType) {
        StringBuilder purposeSb = new StringBuilder();

        purposeSb.append(KylinCloudManageConstants.NETWORK_TYPE).append(KylinCloudManageConstants.LEFT_BRACKET)
                .append(interfaceType).append(KylinCloudManageConstants.RIGHT_BRACKET)
                .append(KylinCloudManageConstants.SPACE);

        purposeSb.append(KylinCloudManageConstants.MODEL_TYPE).append(KylinCloudManageConstants
                .LEFT_BRACKET).append(modelType)
                .append(KylinCloudManageConstants.RIGHT_BRACKET).append(KylinCloudManageConstants.SPACE);

        purposeSb.append(KylinCloudManageConstants.NETWORK_SWITCH).append(KylinCloudManageConstants
                .LEFT_BRACKET).append(virtualSwitch)
                .append(KylinCloudManageConstants.RIGHT_BRACKET).append(KylinCloudManageConstants.SPACE);

        purposeSb.append(KylinCloudManageConstants.NETWORK_PORT).append(KylinCloudManageConstants.LEFT_BRACKET)
                .append(portGroup).append(KylinCloudManageConstants.RIGHT_BRACKET);

        return purposeSb.toString();
    }


    /**
     * 根据申请ID获取，申请时的网络信息
     */
    private List<CloudWorkOrderServerVmNetworkDo> getNetworkListByWorkOrderId(Integer workOrderId) {
        CloudWorkOrderServerVmNetworkDo queryNetworkDo = new CloudWorkOrderServerVmNetworkDo();
        queryNetworkDo.setWorkOrderId(workOrderId);
        QueryWrapper<CloudWorkOrderServerVmNetworkDo> networkQueryWrapper = new QueryWrapper<>(queryNetworkDo);
        List<CloudWorkOrderServerVmNetworkDo> networkDoList =
                cloudWorkOrderServerVmNetworkService.list(networkQueryWrapper);
        return networkDoList;
    }


    /**
     * 根据申请ID获取，申请时的光驱信息
     */
    private List<CloudWorkOrderServerVmIsoDo> getIsoByWorkOrderId(Integer workOrderId) {
        CloudWorkOrderServerVmIsoDo queryIsoDo = new CloudWorkOrderServerVmIsoDo();
        queryIsoDo.setWorkOrderId(workOrderId);
        QueryWrapper<CloudWorkOrderServerVmIsoDo> isoQueryWrapper = new QueryWrapper<>(queryIsoDo);
        List<CloudWorkOrderServerVmIsoDo> isoDoList = cloudWorkOrderServerVmIsoService.list(isoQueryWrapper);
        return isoDoList;
    }

    /**
     * 根据申请ID获取，申请时的网络信息
     */
    private List<CloudWorkOrderServerVmDiskDo> getDisksByWorkOrderId(Integer workOrderId) {
        CloudWorkOrderServerVmDiskDo queryDiskDo = new CloudWorkOrderServerVmDiskDo();
        queryDiskDo.setWorkOrderId(workOrderId);
        QueryWrapper<CloudWorkOrderServerVmDiskDo> diskQueryWrapper = new QueryWrapper<>(queryDiskDo);
        List<CloudWorkOrderServerVmDiskDo> diskDoList = cloudWorkOrderServerVmDiskService.list(diskQueryWrapper);
        return diskDoList;
    }

    private ArchitectureType changeToKcpArchitectureType(String plateformType) {

        if (Objects.equals(plateformType, McArchitectureType.X86_64.getName()) || Objects.equals(plateformType,
                McArchitectureType.LOWER_86_64.getName())) {
            return ArchitectureType.X86_64;
        }
        if (Objects.equals(plateformType, McArchitectureType.ARM.getName()) || Objects.equals(plateformType,
                McArchitectureType.AARCH64.name()) || Objects.equals(plateformType,
                McArchitectureType.AARCH64.getName())) {
            return ArchitectureType.ARM64;
        }
        if (Objects.equals(plateformType, McArchitectureType.MIPS.getName())) {
            return ArchitectureType.MIPS64;
        }
        if (Objects.equals(plateformType, McArchitectureType.sw_64.getName())) {
            return ArchitectureType.SW64;
        }
        return ArchitectureType.X86_64;
    }

    @Override
    @Transactional
    public void passApplyServerVm(PassApplyServerVmParam passApplyServerVmParam, LoginUserVo loginUserVo) {

        CloudWorkOrderDo cloudWorkOrderDo = cloudWorkOrderService.getById(passApplyServerVmParam.getWorkOrderId());
        //获取申请详情
        CloudWorkOrderServerVmDo queryServerVmDo = new CloudWorkOrderServerVmDo();
        queryServerVmDo.setWorkOrderId(passApplyServerVmParam.getWorkOrderId());
        QueryWrapper<CloudWorkOrderServerVmDo> wrapper = new QueryWrapper<>(queryServerVmDo);
        CloudWorkOrderServerVmDo serverVmDo = cloudWorkOrderServerVmService.getOne(wrapper);

        //校验是否超出申请用户对应组织VDC的资源限制
        int applyServerVmCount = Objects.equals(serverVmDo.getApplyServervmType(), ApplyServerVmType.ISO) ? 1 :
                passApplyServerVmParam.getVmNumber();
        verifyIfExceedVdcResource(cloudWorkOrderDo.getUserId(), loginUserVo, applyServerVmCount,
                passApplyServerVmParam.getVcpus(),
                passApplyServerVmParam.getMemory(), passApplyServerVmParam.getDiskList(),
                changeToKcpArchitectureType(passApplyServerVmParam.getPlateformType())
        );

        if (Objects.equals(serverVmDo.getApplyServervmType(), ApplyServerVmType.ISO)) {
            CheckServerNameParamReq checkServerNameParamReq = new CheckServerNameParamReq();
            checkServerNameParamReq.setServervmName(passApplyServerVmParam.getAliasName());
            mcServerVmService.checkServerNameExist(serverVmDo.getClusterId(), checkServerNameParamReq, loginUserVo);
        }


        formatCommonCheckWorkOrder(cloudWorkOrderDo, WorkOrderStatus.CHECK_PASS,
                passApplyServerVmParam.getAuditOpinion(), loginUserVo);
        cloudWorkOrderService.updateById(cloudWorkOrderDo);

        Date now = new Date();

        serverVmDo.setModifyApplyNum(passApplyServerVmParam.getVmNumber());
        serverVmDo.setModifyCpu(passApplyServerVmParam.getVcpus());
        serverVmDo.setModifyMem(passApplyServerVmParam.getMemory());
        serverVmDo.setUpdateBy(loginUserVo.getUserId());
        serverVmDo.setUpdateTime(now);
        serverVmDo.setCloneType(passApplyServerVmParam.getCloneType());
        cloudWorkOrderServerVmService.updateById(serverVmDo);


        McCreateServerVmParamReq mcCreateServerVmParamReq = new McCreateServerVmParamReq();
        BeanUtils.copyProperties(passApplyServerVmParam, mcCreateServerVmParamReq);
        mcCreateServerVmParamReq.setPlateformtype(passApplyServerVmParam.getPlateformType());
        if (Objects.equals(passApplyServerVmParam.getPlateformType(), McArchitectureType.LOWER_86_64.name())) {
            mcCreateServerVmParamReq.setPlateformtype(McArchitectureType.X86_64.getName());
        }
        if (Objects.equals(passApplyServerVmParam.getPlateformType(), McArchitectureType.AARCH64.name())) {
            mcCreateServerVmParamReq.setPlateformtype(McArchitectureType.AARCH64.getName());
        }
        mcCreateServerVmParamReq.setSystemVersion(passApplyServerVmParam.getOperatingSystem());

        if (Objects.equals(serverVmDo.getApplyServervmType(), ApplyServerVmType.TEMPLATE)) {
            mcCreateServerVmParamReq.setServiceTemplateId(serverVmDo.getTemplateId() + "");
        }
        mcCreateServerVmParamReq.setSystemType(passApplyServerVmParam.getSystemType());
        mcCreateServerVmParamReq.setDescription(serverVmDo.getDescription());
        //封装磁盘信息
        List<McCreateServerVmDiskParam> diskCapacity = new ArrayList<>();

        passApplyServerVmParam.getDiskList().forEach(disk -> {
            //非删除类型的传到mc中，
            if (!Objects.equals(disk.getModifyType(), ModifyType.DELETE)) {
                McCreateServerVmDiskParam mcDisk = new McCreateServerVmDiskParam();
                mcDisk.setId("");
                mcDisk.setDiskCapacity(disk.getDiskCapacity());
                mcDisk.setLastUpdateType(LastUpdateType.add.getValue());
                diskCapacity.add(mcDisk);
            }
        });
        mcCreateServerVmParamReq.setDiskCapacity(diskCapacity);
        //封装网卡信息
        List<McCreateServerVmInterfacesParam> interfaces = new ArrayList<>();
        passApplyServerVmParam.getNetworkList().forEach(network -> {
            //非删除的网卡传到mc中，进行网卡添加
            if (!Objects.equals(network.getModifyType(), ModifyType.DELETE)) {
                //模板中自带的网卡信息
                if (Objects.equals(network.getType(), ApplyMcServerVmType.original)) {
                    McCreateServerVmInterfacesParam mcInterface = new McCreateServerVmInterfacesParam();
                    createMcOriginalInterface(mcInterface, network);
                    interfaces.add(mcInterface);
                } else {
                    //自定义申请的网卡信息
                    interfaces.add(formatCustomNetwork(network));
                }
            }

        });
        McCreateServerVmInterfacesParam existHostIpConfigParam =
                interfaces.stream().filter(item -> item.getLsbind() || item.getManualSetIP() || item.getAutomaticAcqIp()).findFirst().orElse(null);
        if (Objects.nonNull(existHostIpConfigParam)) {
            mcCreateServerVmParamReq.setExistHostIpConfig(true);
        }
        mcCreateServerVmParamReq.setInterfaces(interfaces);
        CloudUserDo applyUser = cloudUserService.getById(cloudWorkOrderDo.getUserId());
        mcCreateServerVmParamReq.setApplyUser(applyUser.getUserName());

        //计算资源处理
        mcCreateServerVmParamReq.setClusterType(passApplyServerVmParam.getServerClusterType().getValue());
        mcCreateServerVmParamReq.setSelectResourceTagId(passApplyServerVmParam.getSelectResourceTagId());
        //iso文件处理
        List<McCreateServerVmIsoParam> isoSelect = new ArrayList<>();
        int isoIndex = 0;
        if (Objects.nonNull(passApplyServerVmParam.getIsoList()) && !passApplyServerVmParam.getIsoList().isEmpty()) {
            for (int i = 0; i < passApplyServerVmParam.getIsoList().size(); i++) {
                PassServerVmIsoParam passIso = passApplyServerVmParam.getIsoList().get(i);
                if (!Objects.equals(passIso.getModifyType(), ModifyType.DELETE)) {
                    McCreateServerVmIsoParam isoParam = new McCreateServerVmIsoParam();
                    isoParam.setId(null);
                    isoParam.setIsoSelect(passIso.getIsoFile());
                    isoParam.setIndex(isoIndex);
                    isoParam.setLastUpdateType(LastUpdateType.add.getValue());
                    isoParam.setDeviceName("光驱" + isoIndex);
                    isoSelect.add(isoParam);
                    isoIndex++;
                }
            }
        }
        mcCreateServerVmParamReq.setIsoSelect(isoSelect);
        List<String> mcUuidList = new ArrayList<>();
        //调用mc创建云服务器，
        if (Objects.equals(serverVmDo.getApplyServervmType(), ApplyServerVmType.TEMPLATE)) {
            mcCreateServerVmParamReq.setCloneType(passApplyServerVmParam.getCloneType().getValue().toString());
            mcUuidList = mcServerVmService.createMcServerVm(serverVmDo.getClusterId(), mcCreateServerVmParamReq,
                    loginUserVo,
                    ApplyServerVmType.TEMPLATE);
        } else {
            mcUuidList = mcServerVmService.createMcServerVm(serverVmDo.getClusterId(), mcCreateServerVmParamReq,
                    loginUserVo,
                    ApplyServerVmType.ISO);
        }
        if (mcUuidList.isEmpty()) {
            throw new KylinException(KylinHttpResponseOrderConstants.CREATE_SERVERVM_ERR);
        }

        //计算用户云服务器到期时间,
        Date afterMonthDate = DateUtils.getMonthAfter(new Date(), serverVmDo.getUseMonth());
        Date deadlineTime = DateUtils.getDayEndTime(afterMonthDate);
        //插入用户拥有的云服务器
        List<CloudUserMachineDo> userMachineDoList = new ArrayList<>();
        mcUuidList.forEach(uuid -> {
            CloudUserMachineDo cloudUserMachineDo = new CloudUserMachineDo();
            cloudUserMachineDo.setDeadlineFlag(Boolean.FALSE);
            cloudUserMachineDo.setDeadlineTime(deadlineTime);
            cloudUserMachineDo.setUserId(cloudWorkOrderDo.getUserId());
            cloudUserMachineDo.setMachineUuid(uuid);
            cloudUserMachineDo.setCreateBy(loginUserVo.getUserId());
            cloudUserMachineDo.setDeadlineType(serverVmDo.getDeadlineType());
            cloudUserMachineDo.setCreateTime(now);
            cloudUserMachineDo.setClusterId(serverVmDo.getClusterId());
            userMachineDoList.add(cloudUserMachineDo);
        });
        userMachineService.saveBatch(userMachineDoList);

        //管理员审核时，可能针对申请的磁盘信息进行了变动，处理变动信息
        handleDisk(passApplyServerVmParam.getDiskList(), loginUserVo, serverVmDo.getWorkOrderId(), now);
        //管理员审核时，可能针对申请的网卡信息进行了变动，处理变动信息
        formatHandleNetwork(passApplyServerVmParam.getNetworkList(), loginUserVo, serverVmDo.getWorkOrderId(), now);
        //管理员审核时，可能针对申请的ISO进行了变动，处理变动信息
        formatHandleIso(passApplyServerVmParam.getIsoList(), loginUserVo, serverVmDo.getWorkOrderId(), now);

    }


    /**
     * 校验是否超出VDC资源限制
     */
    private void verifyIfExceedVdcResource(Integer applyUsedId, LoginUserVo loginUserVo, Integer applyServerVmNum,
                                           Integer cpu, Integer mem, List<PassServerVmDiskParam> diskList,
                                           ArchitectureType architectureType) {
        //根据申请用户获取用户对应的组织
        CloudOrganizationDo orgDo =
                cloudOrganizationService.getById(cloudUserService.getById(applyUsedId).getOrganizationId());
        //获取组织绑定的VDC
        CloudVdcDo vdcDo = vdcService.getVdcByOrgId(orgDo.getId());
        //获取VDC资源使用情况
        VdcUsedResourceDto vdcResourceDto = vdcService.getVdcResourceInfo(vdcDo.getId(), loginUserVo);

        int applyCpu = applyServerVmNum * cpu;
        int applyMem = applyServerVmNum * mem;
        int applyStorage =
                diskList.stream().collect(Collectors.summingInt(PassServerVmDiskParam::getDiskCapacity)) * applyServerVmNum;

        boolean exceedVdcCpu = false;
        boolean exceedVdcMem = false;
        boolean exceedVdcStorage = false;
        StringBuilder exceedVdcResourceSb =
                new StringBuilder();

        //VDC-各架构资源使用情况
        List<VdcArchitectureUsedResourceDto> vdcArchitectureUsedResourceList =
                vdcResourceDto.getVdcArchitectureUsedResourceList();

        VdcArchitectureUsedResourceDto architectureUsedResource =
                vdcArchitectureUsedResourceList.stream().filter(item -> Objects.equals(item.getArchitectureType(),
                        architectureType))
                        .findFirst().orElse(null);
        //校验vdc-架构-cpu
        if (Objects.isNull(architectureUsedResource) || applyCpu > architectureUsedResource.getSurplusCpu()) {
            exceedVdcCpu = true;
        }
        //校验vdc-架构-内存
        if (Objects.isNull(architectureUsedResource) || applyMem > architectureUsedResource.getSurplusMem()) {
            exceedVdcMem = true;
        }
        //校验存储资源
        if (applyStorage > vdcResourceDto.getSurplusStorage()) {
            exceedVdcStorage = true;
        }

        if (exceedVdcCpu || exceedVdcMem || exceedVdcStorage) {
            exceedVdcResourceSb.append(architectureType.name()).append(KylinCloudManageConstants.ARCHITECTURE);
            if (exceedVdcCpu) {
                exceedVdcResourceSb.append(KylinCloudManageConstants.EXCEED_CPU);
            }
            if (exceedVdcMem) {
                exceedVdcResourceSb.append(KylinCloudManageConstants.EXCEED_MEM);
            }
            if (exceedVdcStorage) {
                exceedVdcResourceSb.append(KylinCloudManageConstants.EXCEED_STORAGE);
            }
            exceedVdcResourceSb.append(KylinCloudManageConstants.EXCEED_ALLOCATE);
            throw new KylinException(exceedVdcResourceSb.toString());
        }

    }


    /**
     * 管理员审核时，可能变动了申请时的磁盘信息，将新变动的信息进行处理
     */
    private void handleDisk(List<PassServerVmDiskParam> diskList, LoginUserVo loginUserVo,
                            Integer workOrderId, Date now) {
        //查询申请时自定义硬盘信息
        List<CloudWorkOrderServerVmDiskDo> diskDoList = getDisksByWorkOrderId(workOrderId);

        List<CloudWorkOrderServerVmDiskDo> newInsertDiskList = new ArrayList<>();
        List<CloudWorkOrderServerVmDiskDo> updateDiskList = new ArrayList<>();

        diskList.forEach(disk -> {
            //管理员审核时，新添加的磁盘
            if (Objects.equals(disk.getModifyType(), ModifyType.ADD)) {
                CloudWorkOrderServerVmDiskDo cloudWorkOrderServerVmDiskDo =
                        formatNewInsertDisk(workOrderId, disk.getDiskCapacity(), loginUserVo, now);
                newInsertDiskList.add(cloudWorkOrderServerVmDiskDo);
            } else if (Objects.equals(disk.getModifyType(), ModifyType.DELETE)) {
                //管理员审核时删除的网卡
                CloudWorkOrderServerVmDiskDo deleteDisk =
                        diskDoList.stream().filter(item -> Objects.equals(item.getId(), disk.getApplyId()))
                                .findFirst().orElse(null);
                if (Objects.nonNull(deleteDisk)) {
                    deleteDisk.setModifyType(ModifyType.DELETE);
                    deleteDisk.setUpdateBy(loginUserVo.getUserId());
                    deleteDisk.setUpdateTime(now);
                    updateDiskList.add(deleteDisk);
                }
            } else {
                //没有变动的磁盘，
                CloudWorkOrderServerVmDiskDo noneDisk =
                        diskDoList.stream().filter(item -> Objects.equals(item.getId(), disk.getApplyId()))
                                .findFirst().orElse(null);
                if (Objects.nonNull(noneDisk)) {
                    //比较大小是否变动，磁盘大小变动则说明，管理员审核时改了磁盘大小
                    if (!Objects.equals(noneDisk.getDiskSize(), disk.getDiskCapacity())) {
                        noneDisk.setModifyType(ModifyType.MODIFY);
                        noneDisk.setOldDiskSize(noneDisk.getDiskSize());
                        noneDisk.setDiskSize(disk.getDiskCapacity());
                        noneDisk.setUpdateBy(loginUserVo.getUserId());
                        noneDisk.setUpdateTime(now);
                        updateDiskList.add(noneDisk);
                    }
                }
            }

        });
        if (!newInsertDiskList.isEmpty()) {
            cloudWorkOrderServerVmDiskService.saveBatch(newInsertDiskList);
        }

        if (!updateDiskList.isEmpty()) {
            cloudWorkOrderServerVmDiskService.updateBatchById(updateDiskList);
        }

    }


    /**
     * 封装审核时，新增的磁盘
     *
     * @param workOrderId
     */
    private CloudWorkOrderServerVmDiskDo formatNewInsertDisk(Integer workOrderId, Integer diskSize,
                                                             LoginUserVo loginUser, Date now) {
        CloudWorkOrderServerVmDiskDo cloudWorkOrderServerVmDiskDo = new CloudWorkOrderServerVmDiskDo();
        cloudWorkOrderServerVmDiskDo.setWorkOrderId(workOrderId);
        cloudWorkOrderServerVmDiskDo.setDiskSize(diskSize);
        cloudWorkOrderServerVmDiskDo.setPurpose("");
        cloudWorkOrderServerVmDiskDo.setType(ApplyMcServerVmType.custom);
        cloudWorkOrderServerVmDiskDo.setCreateBy(loginUser.getUserId());
        cloudWorkOrderServerVmDiskDo.setCreateTime(now);
        cloudWorkOrderServerVmDiskDo.setDiskId(0L);
        cloudWorkOrderServerVmDiskDo.setModifyType(ModifyType.ADD);
        cloudWorkOrderServerVmDiskDo.setOldDiskSize(diskSize);
        return cloudWorkOrderServerVmDiskDo;
    }

    /**
     * 封装审核时，新增的ISO
     *
     * @param workOrderId
     */
    private CloudWorkOrderServerVmIsoDo formatNewInsertIso(Integer workOrderId, String isoFile,
                                                           LoginUserVo loginUser, Date now) {
        CloudWorkOrderServerVmIsoDo cloudWorkOrderServerVmIsoDo = new CloudWorkOrderServerVmIsoDo();
        cloudWorkOrderServerVmIsoDo.setWorkOrderId(workOrderId);
        cloudWorkOrderServerVmIsoDo.setOldIsoFile(isoFile);
        cloudWorkOrderServerVmIsoDo.setIsoFile(isoFile);
        cloudWorkOrderServerVmIsoDo.setCreateBy(loginUser.getUserId());
        cloudWorkOrderServerVmIsoDo.setCreateTime(now);
        cloudWorkOrderServerVmIsoDo.setModifyType(ModifyType.ADD);
        return cloudWorkOrderServerVmIsoDo;
    }

    /**
     * 封装审核时，新增的网卡
     *
     * @param workOrderId
     */
    private CloudWorkOrderServerVmNetworkDo formatNewInsertNetwork(Integer workOrderId,
                                                                   PassServerVmNetworkParam network,
                                                                   LoginUserVo loginUser, Date now) {
        CloudWorkOrderServerVmNetworkDo cloudWorkOrderServerVmDiskDo = new CloudWorkOrderServerVmNetworkDo();
        cloudWorkOrderServerVmDiskDo.setWorkOrderId(workOrderId);
        cloudWorkOrderServerVmDiskDo.setInterfaceId(0L);
        cloudWorkOrderServerVmDiskDo.setPurpose("");
        cloudWorkOrderServerVmDiskDo.setType(ApplyMcServerVmType.custom);
        cloudWorkOrderServerVmDiskDo.setCreateBy(loginUser.getUserId());
        cloudWorkOrderServerVmDiskDo.setCreateTime(now);
        cloudWorkOrderServerVmDiskDo.setModifyType(ModifyType.ADD);

        cloudWorkOrderServerVmDiskDo.setIpBindMac(network.getIpBindMac());
        cloudWorkOrderServerVmDiskDo.setManualSetIp(network.getManualSetIp());
        cloudWorkOrderServerVmDiskDo.setAutomaticAcqIp(network.getAutomaticAcqIp());
        cloudWorkOrderServerVmDiskDo.setIp(network.getIp());
        cloudWorkOrderServerVmDiskDo.setMask(network.getMask());
        cloudWorkOrderServerVmDiskDo.setGw(network.getGw());
        cloudWorkOrderServerVmDiskDo.setDns1(network.getDns1());
        cloudWorkOrderServerVmDiskDo.setDns2(network.getDns2());

        //自定义的网络设置
        CloudNetworkConfigDo cloudNetworkConfigDo = networkConfigService.getById(network.getNetworkId());
        cloudWorkOrderServerVmDiskDo.setPurpose(formatNetworkStr(cloudNetworkConfigDo.getInterfaceType(),
                cloudNetworkConfigDo.getPortGroup(),
                cloudNetworkConfigDo.getVirtualSwitch(), cloudNetworkConfigDo.getModelType()));
        return cloudWorkOrderServerVmDiskDo;
    }

    /**
     * 管理员审核时，可能变动了申请时的网卡信息，将新变动网卡信息进行处理
     *
     * @param networkList
     * @param loginUserVo
     * @param now
     */
    private void formatHandleNetwork(List<PassServerVmNetworkParam> networkList, LoginUserVo loginUserVo,
                                     Integer workOrderId, Date now) {
        //查询申请时的网卡列表
        List<CloudWorkOrderServerVmNetworkDo> networkDoList = getNetworkListByWorkOrderId(workOrderId);
        List<CloudWorkOrderServerVmNetworkDo> newInsertNetworkList = new ArrayList<>();
        List<CloudWorkOrderServerVmNetworkDo> updateNetworkList = new ArrayList<>();


        networkList.forEach(network -> {
            if (Objects.equals(network.getModifyType(), ModifyType.ADD)) {
                //新增的网卡
                newInsertNetworkList.add(formatNewInsertNetwork(workOrderId, network, loginUserVo, now));
            } else if (Objects.equals(network.getModifyType(), ModifyType.DELETE)) {
                //管理员审核时删除的网卡
                CloudWorkOrderServerVmNetworkDo deleteNetwork =
                        networkDoList.stream().filter(item -> Objects.equals(item.getId(), network.getApplyId()))
                                .findFirst().orElse(null);
                if (Objects.nonNull(deleteNetwork)) {
                    deleteNetwork.setModifyType(ModifyType.DELETE);
                    deleteNetwork.setUpdateBy(loginUserVo.getUserId());
                    deleteNetwork.setUpdateTime(now);
                    updateNetworkList.add(deleteNetwork);
                }
            } else {
                if (network.getNetworkId() > 0) {
                    CloudWorkOrderServerVmNetworkDo customerNetWork =
                            networkDoList.stream().filter(item -> Objects.equals(item.getId(), network.getApplyId()))
                                    .findFirst().orElse(null);

                    if (Objects.nonNull(customerNetWork)) {
                        customerNetWork.setModifyType(ModifyType.MODIFY);
                        String oldPurpose = customerNetWork.getPurpose();
                        CloudNetworkConfigDo cloudNetworkConfigDo =
                                networkConfigService.getById(network.getNetworkId());
                        String newPurpose = formatNetworkStr(cloudNetworkConfigDo.getInterfaceType(),
                                cloudNetworkConfigDo.getPortGroup(),
                                cloudNetworkConfigDo.getVirtualSwitch(), cloudNetworkConfigDo.getModelType());
                        customerNetWork.setPurpose(oldPurpose + " —> " + newPurpose);
                        customerNetWork.setUpdateBy(loginUserVo.getUserId());
                        customerNetWork.setUpdateTime(now);
                        customerNetWork.setIpBindMac(network.getIpBindMac());
                        customerNetWork.setManualSetIp(network.getManualSetIp());
                        customerNetWork.setAutomaticAcqIp(network.getAutomaticAcqIp());
                        customerNetWork.setIp(network.getIp());
                        customerNetWork.setMask(network.getMask());
                        customerNetWork.setGw(network.getGw());
                        customerNetWork.setDns1(network.getDns1());
                        customerNetWork.setDns2(network.getDns2());
                        updateNetworkList.add(customerNetWork);
                    }
                } else {
                    CloudWorkOrderServerVmNetworkDo updateNetwork =
                            networkDoList.stream().filter(item -> Objects.equals(item.getId(), network.getApplyId()))
                                    .findFirst().orElse(null);
                    if (Objects.nonNull(updateNetwork)) {
                        updateNetwork.setIpBindMac(network.getIpBindMac());
                        updateNetwork.setManualSetIp(network.getManualSetIp());
                        updateNetwork.setAutomaticAcqIp(network.getAutomaticAcqIp());
                        updateNetwork.setIp(network.getIp());
                        updateNetwork.setMask(network.getMask());
                        updateNetwork.setGw(network.getGw());
                        updateNetwork.setDns1(network.getDns1());
                        updateNetwork.setDns2(network.getDns2());
                        updateNetworkList.add(updateNetwork);
                    }
                }
            }
        });
        if (!newInsertNetworkList.isEmpty()) {
            cloudWorkOrderServerVmNetworkService.saveBatch(newInsertNetworkList);
        }

        if (!updateNetworkList.isEmpty()) {
            cloudWorkOrderServerVmNetworkService.updateBatchById(updateNetworkList);
        }
    }


    /**
     * 管理员审核时，可能变动了申请时的iso信息，将新变动网卡信息进行处理
     *
     * @param isoList
     * @param loginUserVo
     * @param now
     */
    private void formatHandleIso(List<PassServerVmIsoParam> isoList, LoginUserVo loginUserVo,
                                 Integer workOrderId, Date now) {
        //查询申请时的网卡列表
        List<CloudWorkOrderServerVmIsoDo> isoDoList = getIsoByWorkOrderId(workOrderId);
        List<CloudWorkOrderServerVmIsoDo> newInsertIsoList = new ArrayList<>();
        List<CloudWorkOrderServerVmIsoDo> updateIsoList = new ArrayList<>();
        if (Objects.nonNull(isoList) && !isoList.isEmpty()) {
            isoList.forEach(iso -> {
                if (Objects.equals(iso.getModifyType(), ModifyType.ADD)) {
                    //新增的ISO
                    newInsertIsoList.add(formatNewInsertIso(workOrderId, iso.getIsoFile(), loginUserVo, now));
                } else if (Objects.equals(iso.getModifyType(), ModifyType.DELETE)) {
                    //管理员审核时删除的网卡
                    CloudWorkOrderServerVmIsoDo deleteIso =
                            isoDoList.stream().filter(item -> Objects.equals(item.getId(), iso.getApplyId()))
                                    .findFirst().orElse(null);
                    if (Objects.nonNull(deleteIso)) {
                        deleteIso.setModifyType(ModifyType.DELETE);
                        deleteIso.setUpdateBy(loginUserVo.getUserId());
                        deleteIso.setUpdateTime(now);
                        updateIsoList.add(deleteIso);
                    }
                } else {
                    //没有变动的磁盘，
                    CloudWorkOrderServerVmIsoDo noneIso =
                            isoDoList.stream().filter(item -> Objects.equals(item.getId(), iso.getApplyId()))
                                    .findFirst().orElse(null);
                    if (Objects.nonNull(noneIso)) {
                        //比较文件名是否改动
                        if (!Objects.equals(noneIso.getIsoFile(), iso.getIsoFile())) {
                            noneIso.setModifyType(ModifyType.MODIFY);
                            noneIso.setOldIsoFile(noneIso.getIsoFile());
                            noneIso.setIsoFile(iso.getIsoFile());
                            noneIso.setUpdateBy(loginUserVo.getUserId());
                            noneIso.setUpdateTime(now);
                            updateIsoList.add(noneIso);
                        }
                    }
                }
            });
            if (!newInsertIsoList.isEmpty()) {
                cloudWorkOrderServerVmIsoService.saveBatch(newInsertIsoList);
            }

            if (!updateIsoList.isEmpty()) {
                cloudWorkOrderServerVmIsoService.updateBatchById(updateIsoList);
            }
        }

    }

    /**
     * 封装审核云服务器，变更源服务器，原始的网卡信息
     *
     * @return
     */
    private void createMcOriginalInterface(McCreateServerVmInterfacesParam mcInterface,
                                           PassServerVmNetworkParam network) {
        mcInterface.setInterfaceType(network.getInterfaceType());
        mcInterface.setModeltype(network.getModelType());
        mcInterface.setPortGroup(network.getPortGroupUuid());
        mcInterface.setMacAddress(network.getMacAddressPool());
        mcInterface.setVirtualSwitch(network.getVirtualSwitch());
        mcInterface.setSecurityStrategy(network.getSecurityStrategy());
        //安全组策略
        if (Objects.equals(network.getSecurityStrategy(), NetworkSecurityPolicy.SECURITY_GROUP.getValue())) {
            if (Objects.nonNull(network.getSecurityGroupUuid())) {
                List<String> securityGroupUuidList =
                        Arrays.asList(network.getSecurityGroupUuid()
                                .split(",")).stream().map(s -> (s.trim())).collect(Collectors.toList());
                mcInterface.setSecurityGroup(securityGroupUuidList);
            }
        }
        mcInterface.setQueueCount(network.getQueueCount().toString());
        mcInterface.setMtuCount(network.getMtuCount().toString());
        mcInterface.setVirtualFirewall(network.getVirtualFirewall());
        mcInterface.setLastUpdateType(LastUpdateType.add.getValue());
        mcInterface.setLsbind(network.getIpBindMac());
        mcInterface.setManualSetIP(network.getManualSetIp());
        mcInterface.setAutomaticAcqIp(network.getAutomaticAcqIp());
        mcInterface.setIp(network.getAutomaticAcqIp() ? KylinCloudManageConstants.DHCP : network.getIp());
        mcInterface.setMask(network.getMask());
        mcInterface.setGw(network.getGw());
        mcInterface.setDns1(network.getDns1());
        mcInterface.setDns2(network.getDns2());
    }

    /**
     * 封装申请的自定义的网卡信息
     */
    private McModifyServerVmInterfacesParam formatCustomNetwork(PassServerVmNetworkParam network) {
        McModifyServerVmInterfacesParam mcInterface = new McModifyServerVmInterfacesParam();
        mcInterface.setLastUpdateType(LastUpdateType.add.getValue());
        //自定义的网络设置
        CloudNetworkConfigDo cloudNetworkConfigDo = networkConfigService.getById(network.getNetworkId());
        mcInterface.setInterfaceType(cloudNetworkConfigDo.getInterfaceType());
        mcInterface.setModeltype(cloudNetworkConfigDo.getModelType());
        mcInterface.setPortGroup(cloudNetworkConfigDo.getPortGroupUuid());
        mcInterface.setVirtualSwitch(cloudNetworkConfigDo.getVirtualSwitch());
        mcInterface.setMacAddress(cloudNetworkConfigDo.getAddressPool());
        mcInterface.setLastUpdateType(LastUpdateType.add.getValue());
        mcInterface.setSecurityStrategy(cloudNetworkConfigDo.getSecurityPolicy().getValue());
        if (Objects.equals(cloudNetworkConfigDo.getSecurityPolicy(), NetworkSecurityPolicy.SECURITY_GROUP)) {
            if (StringUtils.isNotBlank(cloudNetworkConfigDo.getSecurityGroupUuid())) {
                List<String> securityGroupUuidList =
                        Arrays.asList(cloudNetworkConfigDo.getSecurityGroupUuid()
                                .split(",")).stream().map(s -> (s.trim())).collect(Collectors.toList());
                mcInterface.setSecurityGroup(securityGroupUuidList);
            }
        } else if (Objects.equals(cloudNetworkConfigDo.getSecurityPolicy(), NetworkSecurityPolicy.VIRTUAL_FIREWALL)) {
            mcInterface.setVirtualFirewall(cloudNetworkConfigDo.getVirtualFirewallId());
        }

        mcInterface.setLsbind(network.getIpBindMac());
        mcInterface.setManualSetIP(network.getManualSetIp());
        mcInterface.setAutomaticAcqIp(network.getAutomaticAcqIp());
        mcInterface.setIp(network.getAutomaticAcqIp() ? KylinCloudManageConstants.DHCP : network.getIp());
        mcInterface.setMask(network.getMask());
        mcInterface.setGw(network.getGw());
        mcInterface.setDns1(network.getDns1());
        mcInterface.setDns2(network.getDns2());
        return mcInterface;
    }

    /**
     * 校验是否超出VDC资源限制
     */
    private void modifyServerVmVerifyIfExceedVdcResource(LoginUserVo loginUserVo,
                                                         Integer applyCpu, Integer applyMem,
                                                         List<PassServerVmDiskParam> diskList,
                                                         ArchitectureType architectureType,
                                                         CloudUserMachineDo userMachineDo) {
        //根据申请用户获取用户对应的组织
        CloudOrganizationDo orgDo =
                cloudOrganizationService.getById(cloudUserService.getById(userMachineDo.getUserId()).getOrganizationId());
        //获取组织绑定的VDC
        CloudVdcDo vdcDo = vdcService.getVdcByOrgId(orgDo.getId());
        //获取VDC资源使用情况
        VdcUsedResourceDto vdcResourceDto = vdcService.getVdcResourceInfo(vdcDo.getId(), loginUserVo);


        //根据uuid获取原来云服务器详情
        QueryMcServerDetailParamReq queryMcServerDetailParamReq = new QueryMcServerDetailParamReq();
        queryMcServerDetailParamReq.setUuid(userMachineDo.getMachineUuid());
        McServerVmDetailResp mcServerVmDetailResp =
                mcServerVmService.getMcServerVmDetailByServerVmUuid(userMachineDo.getClusterId(),
                        queryMcServerDetailParamReq, loginUserVo);
        //获取审核前云服务器的CPU，磁盘，存储
        int oldMem = mcServerVmDetailResp.getMem();
        int oldCpu = mcServerVmDetailResp.getCpu();
        int oldDiskSize =
                mcServerVmDetailResp.getDisks().stream().collect(Collectors.summingInt(McServerVmDiskDetailResp::getDiskSize));

        int applyStorage =
                diskList.stream().filter(item -> !Objects.equals(item.getModifyType(), ModifyType.DELETE)).collect(Collectors.summingInt(PassServerVmDiskParam::getDiskCapacity));

        boolean exceedVdcCpu = false;
        boolean exceedVdcMem = false;
        boolean exceedVdcStorage = false;
        StringBuilder exceedVdcResourceSb = new StringBuilder();
        //先校验存储资源
        if (applyStorage > (vdcResourceDto.getSurplusStorage() + oldDiskSize)) {
            exceedVdcStorage = true;
        }
        //VDC-各架构资源使用情况
        List<VdcArchitectureUsedResourceDto> vdcArchitectureUsedResourceList =
                vdcResourceDto.getVdcArchitectureUsedResourceList();

        VdcArchitectureUsedResourceDto architectureUsedResource =
                vdcArchitectureUsedResourceList.stream().filter(item -> Objects.equals(item.getArchitectureType(),
                        architectureType))
                        .findFirst().orElse(null);
        //校验vdc-架构-cpu
        if (Objects.isNull(architectureUsedResource) || applyCpu > (architectureUsedResource.getSurplusCpu() + oldCpu)) {
            exceedVdcCpu = true;
        }
        //校验vdc-架构-内存
        if (Objects.isNull(architectureUsedResource) || applyMem > architectureUsedResource.getSurplusMem() + oldMem) {
            exceedVdcMem = true;
        }

        if (exceedVdcCpu || exceedVdcMem || exceedVdcStorage) {
            exceedVdcResourceSb.append(architectureType.name()).append(KylinCloudManageConstants.ARCHITECTURE);
            if (exceedVdcCpu) {
                exceedVdcResourceSb.append(KylinCloudManageConstants.EXCEED_CPU);
            }
            if (exceedVdcMem) {
                exceedVdcResourceSb.append(KylinCloudManageConstants.EXCEED_MEM);
            }
            if (exceedVdcStorage) {
                exceedVdcResourceSb.append(KylinCloudManageConstants.EXCEED_STORAGE);
            }
            exceedVdcResourceSb.append(KylinCloudManageConstants.EXCEED_ALLOCATE);
            throw new KylinException(exceedVdcResourceSb.toString());
        }
    }


    @Override
    @Transactional
    public void passModifyServerVm(PassModifyServerVmParam passModifyServerVmParam, LoginUserVo loginUserVo) {
        CloudWorkOrderDo cloudWorkOrderDo = cloudWorkOrderService.getById(passModifyServerVmParam.getWorkOrderId());

        //查询变更云服务器申请表
        CloudWorkOrderModifyServerVmDo queryServerVmDo = new CloudWorkOrderModifyServerVmDo();
        queryServerVmDo.setWorkOrderId(passModifyServerVmParam.getWorkOrderId());
        QueryWrapper<CloudWorkOrderModifyServerVmDo> wrapper = new QueryWrapper<>(queryServerVmDo);
        CloudWorkOrderModifyServerVmDo modifyServerVmDo = cloudWorkOrderModifyServerVmService.getOne(wrapper);

        //查询用户已经拥有的云服务器
        CloudUserMachineDo userMachineDo = getUserMachineByUuid(modifyServerVmDo.getMachineUuid(),
                cloudWorkOrderDo.getUserId());

        //校验是否超出资源限制
        modifyServerVmVerifyIfExceedVdcResource(loginUserVo,
                passModifyServerVmParam.getVcpus(),
                passModifyServerVmParam.getMemory(), passModifyServerVmParam.getDiskList(),
                changeToKcpArchitectureType(passModifyServerVmParam.getPlateformType()), userMachineDo);


        formatCommonCheckWorkOrder(cloudWorkOrderDo, WorkOrderStatus.CHECK_PASS,
                passModifyServerVmParam.getAuditOpinion(), loginUserVo);
        cloudWorkOrderService.updateById(cloudWorkOrderDo);

        Date updateTime = new Date();


        modifyServerVmDo.setCpu(passModifyServerVmParam.getVcpus());
        modifyServerVmDo.setMem(passModifyServerVmParam.getMemory());
        modifyServerVmDo.setUpdateBy(loginUserVo.getUserId());
        modifyServerVmDo.setUpdateTime(updateTime);
        cloudWorkOrderModifyServerVmService.updateById(modifyServerVmDo);


        McModifyServerVmParamReq mcModifyServerVmParamReq = new McModifyServerVmParamReq();
        BeanUtils.copyProperties(passModifyServerVmParam, mcModifyServerVmParamReq);
        mcModifyServerVmParamReq.setPlateformtype(passModifyServerVmParam.getPlateformType());
        if (Objects.equals(passModifyServerVmParam.getPlateformType(), McArchitectureType.LOWER_86_64.name())) {
            mcModifyServerVmParamReq.setPlateformtype(McArchitectureType.X86_64.name());
        }
        if (Objects.equals(passModifyServerVmParam.getPlateformType(), McArchitectureType.AARCH64.name())) {
            mcModifyServerVmParamReq.setPlateformtype(McArchitectureType.AARCH64.getName());
        }

        mcModifyServerVmParamReq.setSystemVersion(passModifyServerVmParam.getOperatingSystem());
        mcModifyServerVmParamReq.setSystemType(passModifyServerVmParam.getSystemType());
        mcModifyServerVmParamReq.setUuid(modifyServerVmDo.getMachineUuid());
        //封装磁盘信息
        List<McModifyServerVmDiskParam> diskCapacity = new ArrayList<>();
        passModifyServerVmParam.getDiskList().forEach(disk -> {

            if (disk.getId() > 0 || !Objects.equals(disk.getModifyType(), ModifyType.DELETE)) {
                McModifyServerVmDiskParam mcDisk = new McModifyServerVmDiskParam();
                mcDisk.setId(disk.getId().toString());
                mcDisk.setDiskCapacity(disk.getDiskCapacity());
                if (Objects.equals(disk.getId(), 0)) {
                    mcDisk.setId("");
                    mcDisk.setLastUpdateType(LastUpdateType.add.getValue());
                } else {
                    boolean deleteFlag = Objects.equals(disk.getModifyType(), ModifyType.DELETE);
                    mcDisk.setLastUpdateType(deleteFlag ? LastUpdateType.delete.getValue() :
                            LastUpdateType.update.getValue());
                }
                diskCapacity.add(mcDisk);
            }
        });
        mcModifyServerVmParamReq.setDiskCapacity(diskCapacity);

        //封装网卡信息
        List<McModifyServerVmInterfacesParam> interfaces = new ArrayList<>();
        passModifyServerVmParam.getNetworkList().forEach(network -> {
            //变更前原始网卡信息
            if (Objects.nonNull(network.getId()) && network.getId() > 0) {
                //说明是update，或者delete
                McModifyServerVmInterfacesParam mcInterface = new McModifyServerVmInterfacesParam();
                createMcOriginalInterface(mcInterface, network);
                mcInterface.setId(network.getId());
                boolean deleteFlag = Objects.equals(ModifyType.DELETE, network.getModifyType());
                mcInterface.setLastUpdateType(deleteFlag ? LastUpdateType.delete.getValue() :
                        LastUpdateType.update.getValue());
                interfaces.add(mcInterface);
            } else {
                //说明是申请自定义网卡信息
                if (!Objects.equals(network.getModifyType(), ModifyType.DELETE)) {
                    McModifyServerVmInterfacesParam mcInterface = formatCustomNetwork(network);
                    interfaces.add(mcInterface);
                }
            }
        });
        mcModifyServerVmParamReq.setInterfaces(interfaces);
        McCreateServerVmInterfacesParam existHostIpConfigParam =
                interfaces.stream().filter(item -> item.getLsbind() || item.getManualSetIP() || item.getAutomaticAcqIp()).findFirst().orElse(null);
        if (Objects.nonNull(existHostIpConfigParam)) {
            mcModifyServerVmParamReq.setExistHostIpConfig(true);
        }

        //计算资源处理
        mcModifyServerVmParamReq.setClusterType(passModifyServerVmParam.getServerClusterType().getValue());
        mcModifyServerVmParamReq.setSelectResourceTagId(passModifyServerVmParam.getSelectResourceTagId());


        boolean modifyFlag = mcServerVmService.modifyMcServerVm(userMachineDo.getClusterId(),
                mcModifyServerVmParamReq, loginUserVo);
        if (!modifyFlag) {
            throw new KylinException(KylinHttpResponseOrderConstants.MODIFY_SERVERVM_ERR);
        }


        userMachineDo.setDeadlineTime(modifyServerVmDo.getDeadlineTime());
        //新的截至时间大于当期时间，则该用户已拥有云服务器为非过期
        if (modifyServerVmDo.getDeadlineTime().getTime() > System.currentTimeMillis()) {
            userMachineDo.setDeadlineFlag(Boolean.FALSE);
        }
        userMachineDo.setDeadlineType(modifyServerVmDo.getDeadlineType());
        userMachineDo.setUpdateBy(loginUserVo.getUserId());
        userMachineDo.setUpdateTime(updateTime);
        userMachineService.updateById(userMachineDo);

        //管理员审核时，可能针对申请的磁盘信息进行了变动，处理变动信息
        handleDisk(passModifyServerVmParam.getDiskList(), loginUserVo, modifyServerVmDo.getWorkOrderId(), updateTime);
        //管理员审核时，可能针对申请的网卡信息进行了变动，处理变动信息
        formatHandleNetwork(passModifyServerVmParam.getNetworkList(), loginUserVo, modifyServerVmDo.getWorkOrderId(),
                updateTime);

    }

    @Override
    public ApplyDeferredDetailRespDto applyDeferredDetailByWorkOrderId(Integer workOrderId) {
        ApplyDeferredDetailRespDto applyDeferredDetailRespDto = new ApplyDeferredDetailRespDto();

        formatBaseWorkOrderDetail(workOrderId, applyDeferredDetailRespDto);


        //查询延期申请表
        CloudWorkOrderDeferredMachineDo cloudWorkOrderDeferredMachineDo = new CloudWorkOrderDeferredMachineDo();
        cloudWorkOrderDeferredMachineDo.setWorkOrderId(workOrderId);
        QueryWrapper<CloudWorkOrderDeferredMachineDo> queryWrapper = new QueryWrapper<>
                (cloudWorkOrderDeferredMachineDo);
        CloudWorkOrderDeferredMachineDo queryDo = deferredMachineService.getOne(queryWrapper);

        applyDeferredDetailRespDto.setOldDeadlineTime(DateUtils.format(queryDo.getOldDeadlineTime()));
        applyDeferredDetailRespDto.setNewDeadlineTime(DateUtils.format(queryDo.getDeadlineTime()));

        return applyDeferredDetailRespDto;
    }

    @Override
    public UserWaitCheckCountParam getWaitCheckCount(LoginUserVo loginUserVo) {

        UserWaitCheckCountParam waitCheckCountParam = new UserWaitCheckCountParam();

        List<CloudUserDo> userDoList = userService.userVisibleUserList(loginUserVo.getUserId());


        if (!userDoList.isEmpty()) {
            List<Integer> userIdList = userDoList.stream().map(CloudUserDo::getId).collect(Collectors.toList());
            CloudWorkOrderDo queryOrderDo = new CloudWorkOrderDo();
            queryOrderDo.setDeleteFlag(false);
            queryOrderDo.setStatus(WorkOrderStatus.WAIT_CHECK);
            QueryWrapper<CloudWorkOrderDo> wrapper = new QueryWrapper<>(queryOrderDo);
            wrapper.in("user_id", userIdList);
            int waitCheckCheckCount = cloudWorkOrderService.getBaseMapper().selectCount(wrapper);
            waitCheckCountParam.setWaitCheckCount(waitCheckCheckCount);
        }
        return waitCheckCountParam;
    }


    @Override
    public PassModifyVdcDetailRespDto passModifyVdcDetail(WorkOrderDetailParam workOrderDetailParam,
                                                          LoginUserVo loginUserVo) {

        CloudWorkOrderDo cloudWorkOrderDo = cloudWorkOrderService.getById(workOrderDetailParam.getWorkOrderId());
        judgeAlreadyCheck(cloudWorkOrderDo);

        CloudWorkOrderVdcDo queryCloudWorkOrderVdcDo = new CloudWorkOrderVdcDo();
        queryCloudWorkOrderVdcDo.setWorkOrderId(cloudWorkOrderDo.getId());
        QueryWrapper<CloudWorkOrderVdcDo> wrapper = new QueryWrapper<>(queryCloudWorkOrderVdcDo);
        CloudWorkOrderVdcDo modifyVdcDo = cloudWorkOrderVdcService.getOne(wrapper);


        PassModifyVdcDetailRespDto passModifyVdcDetail = new PassModifyVdcDetailRespDto();
        passModifyVdcDetail.setWorkOrderId(cloudWorkOrderDo.getId());
        CloudVdcDo cloudVdcDo = cloudVdcService.getById(modifyVdcDo.getVdcId());
        passModifyVdcDetail.setVdcName(cloudVdcDo.getVdcName());
        CloudOrganizationDo vdcOrg = orgService.getOrgByVdcId(modifyVdcDo.getVdcId());
        passModifyVdcDetail.setOrgName("---");
        if (Objects.nonNull(vdcOrg)) {
            passModifyVdcDetail.setOrgName(vdcOrg.getOrganizationName());
        }
        passModifyVdcDetail.setFirstVdc(Objects.equals(cloudVdcDo.getParentId(), KylinCommonConstants.TOP_PARENT_ID));

        //vdc 当前资源
        VdcModifyResourceRespDto vdcModifyResource = vdcService.modifyVdcResourceDetail(modifyVdcDo.getVdcId(),
                loginUserVo);

        //存储信息
        passModifyVdcDetail.setApplyStorage(modifyVdcDo.getApplyStorage());
        passModifyVdcDetail.setCurrentStorage(vdcModifyResource.getCurrentStorage());
        passModifyVdcDetail.setUsedStorage(vdcModifyResource.getUsedStorage());
        passModifyVdcDetail.setParentUsableStorage(vdcModifyResource.getParentUsableStorage());
        passModifyVdcDetail.setStorageUnit(vdcModifyResource.getStorageUnit());


        //架构资源信息
        CloudWorkOrderVdcCpuMemDo queryCloudWorkOrderVdcCpuMemDo = new CloudWorkOrderVdcCpuMemDo();
        queryCloudWorkOrderVdcCpuMemDo.setWorkOrderId(cloudWorkOrderDo.getId());
        QueryWrapper<CloudWorkOrderVdcCpuMemDo> applyCpuAndMemWrapper =
                new QueryWrapper<>(queryCloudWorkOrderVdcCpuMemDo);
        List<CloudWorkOrderVdcCpuMemDo> applyResourceList = cloudWorkOrderVdcCpuMemService.list(applyCpuAndMemWrapper);


        List<ModifyVdcArchitectureResourceRespDto> applyArchitectureResourceList = new ArrayList<>();
        //遍历架构获取架构资源
        List<ArchitectureType> architectureTypeList = Arrays.asList(ArchitectureType.values());
        architectureTypeList.forEach(architectureType -> {
            List<CloudWorkOrderVdcCpuMemDo> architectureResourceList =
                    applyResourceList.stream().filter(item -> Objects.equals(item.getArchitecture(), architectureType))
                            .collect(Collectors.toList());
            if (!architectureResourceList.isEmpty()) {
                ModifyVdcArchitectureResourceRespDto modifyVdcArchitecture = new ModifyVdcArchitectureResourceRespDto();

                CloudWorkOrderVdcCpuMemDo applyCpu =
                        architectureResourceList.stream().filter(item -> Objects.equals(item.getResourceType(),
                                ArchitectureResourceType.CPU)).findFirst().orElse(null);

                CloudWorkOrderVdcCpuMemDo applyMem =
                        architectureResourceList.stream().filter(item -> Objects.equals(item.getResourceType(),
                                ArchitectureResourceType.MEM)).findFirst().orElse(null);

                modifyVdcArchitecture.setApplyCpu(applyCpu.getApplySize());
                modifyVdcArchitecture.setApplyMem(applyMem.getApplySize());
                modifyVdcArchitecture.setArchitectureType(architectureType);
                //VDC架构已使用信息
                VdcModifyArchitectureResourceRespDto vdcUsedResource =
                        vdcModifyResource.getArchitectureResourceList().stream().filter(item -> Objects.equals(item.getArchitectureType(), architectureType))
                                .findFirst().orElse(null);
                if (Objects.nonNull(vdcUsedResource)) {
                    modifyVdcArchitecture.setCurrentVcpu(vdcUsedResource.getCurrentVcpu());
                    modifyVdcArchitecture.setUsedCpu(vdcUsedResource.getUsedCpu());
                    modifyVdcArchitecture.setParentUsableCpu(vdcUsedResource.getParentUsableCpu());

                    modifyVdcArchitecture.setCurrentMem(vdcUsedResource.getCurrentMem());
                    modifyVdcArchitecture.setUsedMem(vdcUsedResource.getUsedMem());
                    modifyVdcArchitecture.setParentUsableMem(vdcUsedResource.getParentUsableMem());

                }
                applyArchitectureResourceList.add(modifyVdcArchitecture);
            }
        });
        passModifyVdcDetail.setApplyArchitectureResourceList(applyArchitectureResourceList);
        return passModifyVdcDetail;
    }


    @Override
    @Transactional
    public void passModifyVdc(PassModifyVdcResourceParam passModifyVdcResourceParam, LoginUserVo loginUserVo) {
        Integer workOrderId = passModifyVdcResourceParam.getWorkOrderId();
        CloudWorkOrderDo cloudWorkOrderDo = cloudWorkOrderService.getById(workOrderId);
        judgeAlreadyCheck(cloudWorkOrderDo);
        formatCommonCheckWorkOrder(cloudWorkOrderDo, WorkOrderStatus.CHECK_PASS,
                passModifyVdcResourceParam.getAuditOpinion(), loginUserVo);
        cloudWorkOrderService.updateById(cloudWorkOrderDo);


        CloudWorkOrderVdcDo queryCloudWorkOrderVdcDo = new CloudWorkOrderVdcDo();
        queryCloudWorkOrderVdcDo.setWorkOrderId(cloudWorkOrderDo.getId());
        QueryWrapper<CloudWorkOrderVdcDo> wrapper = new QueryWrapper<>(queryCloudWorkOrderVdcDo);
        CloudWorkOrderVdcDo modifyVdcDo = cloudWorkOrderVdcService.getOne(wrapper);

        modifyVdcDo.setRealStorage(passModifyVdcResourceParam.getRealStorage());
        cloudWorkOrderVdcService.updateById(modifyVdcDo);

        //更新VDC-存储信息
        CloudVdcStorageDo cloudVdcStorageDo =
                cloudVdcStorageService.getByVdcId(modifyVdcDo.getVdcId());
        cloudVdcStorageDo.setUpdateBy(loginUserVo.getUserId());
        cloudVdcStorageDo.setUpdateTime(new Date());
        cloudVdcStorageDo.setStorage(passModifyVdcResourceParam.getRealStorage());
        cloudVdcStorageService.updateById(cloudVdcStorageDo);

        //原VDC-CPU和内存资源
        List<CloudVdcCpuDo> oldVdcCpuList = cloudVdcCpuService.listVDdcCpuByVdc(modifyVdcDo.getVdcId());
        List<CloudVdcMemDo> oldVdcMemList = cloudVdcMemService.listVdcMemByVdcId(modifyVdcDo.getVdcId());

        //更新vdc的cpu和资源信息
        List<CloudVdcCpuDo> batchUpdateVdcCpuList = new ArrayList<>();
        List<CloudVdcMemDo> batchUpdateVdcMemList = new ArrayList<>();

        //批量新插入的
        List<CloudVdcCpuDo> batchNewInsertVdcCpuList = new ArrayList<>();
        List<CloudVdcMemDo> batchNewInsertVdcMemList = new ArrayList<>();

        //架构资源信息
        CloudWorkOrderVdcCpuMemDo queryCloudWorkOrderVdcCpuMemDo = new CloudWorkOrderVdcCpuMemDo();
        queryCloudWorkOrderVdcCpuMemDo.setWorkOrderId(cloudWorkOrderDo.getId());
        QueryWrapper<CloudWorkOrderVdcCpuMemDo> applyCpuAndMemWrapper =
                new QueryWrapper<>(queryCloudWorkOrderVdcCpuMemDo);
        List<CloudWorkOrderVdcCpuMemDo> applyResourceList = cloudWorkOrderVdcCpuMemService.list(applyCpuAndMemWrapper);

        List<PassModifyArchitectureResourceParam> commitResourceList =
                passModifyVdcResourceParam.getArchitectureResourceList();
        applyResourceList.forEach(applyResource -> {

            PassModifyArchitectureResourceParam passModifyResource =
                    commitResourceList.stream().filter(item -> Objects.equals(item.getArchitectureType(),
                            applyResource.getArchitecture())).findFirst().orElse(null);
            if (Objects.equals(applyResource.getResourceType(), ArchitectureResourceType.CPU)) {
                applyResource.setRealSize(passModifyResource.getRealCpu());
                CloudVdcCpuDo oldCpuDo = oldVdcCpuList.stream().filter(item -> Objects.equals(item.getArchitecture(),
                        applyResource.getArchitecture()))
                        .findFirst().orElse(null);
                if (Objects.nonNull(oldCpuDo)) {
                    oldCpuDo.setVcpus(passModifyResource.getRealCpu());
                    batchUpdateVdcCpuList.add(oldCpuDo);
                } else {
                    //变更前vdc该架构的车贷cpu资源不存在，则新增
                    CloudVdcCpuDo newInsertVdcCpuDo = new CloudVdcCpuDo();
                    newInsertVdcCpuDo.setVdcId(modifyVdcDo.getVdcId());
                    newInsertVdcCpuDo.setVcpus(passModifyResource.getRealCpu());
                    newInsertVdcCpuDo.setArchitecture(applyResource.getArchitecture());
                    newInsertVdcCpuDo.setCreateBy(loginUserVo.getUserId());
                    newInsertVdcCpuDo.setCreateTime(new Date());
                    batchNewInsertVdcCpuList.add(newInsertVdcCpuDo);
                }
            } else if (Objects.equals(applyResource.getResourceType(), ArchitectureResourceType.MEM)) {
                applyResource.setRealSize(passModifyResource.getRealMem());

                CloudVdcMemDo oldMemDo = oldVdcMemList.stream().filter(item -> Objects.equals(item.getArchitecture(),
                        applyResource.getArchitecture()))
                        .findFirst().orElse(null);
                if (Objects.nonNull(oldMemDo)) {
                    oldMemDo.setMem(passModifyResource.getRealMem());
                    oldMemDo.setMemUnit(applyResource.getUnit());
                    batchUpdateVdcMemList.add(oldMemDo);
                } else {
                    //变更前vdc该架构的车贷cpu资源不存在，则新增
                    CloudVdcMemDo newInsertVdcMemDo = new CloudVdcMemDo();
                    newInsertVdcMemDo.setVdcId(modifyVdcDo.getVdcId());
                    newInsertVdcMemDo.setMem(passModifyResource.getRealMem());
                    newInsertVdcMemDo.setMemUnit(applyResource.getUnit());
                    newInsertVdcMemDo.setArchitecture(applyResource.getArchitecture());
                    newInsertVdcMemDo.setCreateBy(loginUserVo.getUserId());
                    newInsertVdcMemDo.setCreateTime(new Date());
                    batchNewInsertVdcMemList.add(newInsertVdcMemDo);
                }
            }
            applyResource.setUpdateBy(loginUserVo.getUserId());
            applyResource.setUpdateTime(new Date());
        });

        cloudWorkOrderVdcCpuMemService.updateBatchById(applyResourceList);

        if (!batchUpdateVdcCpuList.isEmpty()) {
            cloudVdcCpuService.updateBatchById(batchUpdateVdcCpuList);
        }
        if (!batchUpdateVdcMemList.isEmpty()) {
            cloudVdcMemService.updateBatchById(batchUpdateVdcMemList);
        }
        if (!batchNewInsertVdcCpuList.isEmpty()) {
            cloudVdcCpuService.saveBatch(batchNewInsertVdcCpuList);
        }
        if (!batchNewInsertVdcMemList.isEmpty()) {
            cloudVdcMemService.saveBatch(batchNewInsertVdcMemList);
        }
    }


    @Override
    public ApplyModifyVdcDetailRespDto applyModifyVdcDetail(WorkOrderDetailParam workOrderDetailParam) {
        ApplyModifyVdcDetailRespDto applyModifyVdcDetail = new ApplyModifyVdcDetailRespDto();

        formatBaseWorkOrderDetail(workOrderDetailParam.getWorkOrderId(), applyModifyVdcDetail);


        CloudWorkOrderVdcDo queryCloudWorkOrderVdcDo = new CloudWorkOrderVdcDo();
        queryCloudWorkOrderVdcDo.setWorkOrderId(workOrderDetailParam.getWorkOrderId());
        QueryWrapper<CloudWorkOrderVdcDo> wrapper = new QueryWrapper<>(queryCloudWorkOrderVdcDo);
        CloudWorkOrderVdcDo modifyVdcDo = cloudWorkOrderVdcService.getOne(wrapper);

        CloudVdcDo cloudVdcDo = cloudVdcService.getById(modifyVdcDo.getVdcId());
        applyModifyVdcDetail.setVdcName(cloudVdcDo.getVdcName());
        CloudOrganizationDo vdcOrg = orgService.getOrgByVdcId(modifyVdcDo.getVdcId());
        applyModifyVdcDetail.setOrgName("---");
        if (Objects.nonNull(vdcOrg)) {
            applyModifyVdcDetail.setOrgName(vdcOrg.getOrganizationName());
        }
        applyModifyVdcDetail.setOldStorage(modifyVdcDo.getOldStorage());
        applyModifyVdcDetail.setApplyStorage(modifyVdcDo.getApplyStorage());
        applyModifyVdcDetail.setRealStorage(modifyVdcDo.getRealStorage());
        applyModifyVdcDetail.setStorageUnit(modifyVdcDo.getStorageUnit());
        //架构资源信息
        CloudWorkOrderVdcCpuMemDo queryCloudWorkOrderVdcCpuMemDo = new CloudWorkOrderVdcCpuMemDo();
        queryCloudWorkOrderVdcCpuMemDo.setWorkOrderId(workOrderDetailParam.getWorkOrderId());
        QueryWrapper<CloudWorkOrderVdcCpuMemDo> applyCpuAndMemWrapper =
                new QueryWrapper<>(queryCloudWorkOrderVdcCpuMemDo);
        List<CloudWorkOrderVdcCpuMemDo> applyResourceList = cloudWorkOrderVdcCpuMemService.list(applyCpuAndMemWrapper);


        //架构资源信息
        List<ApplyModifyVdcArchitectureResourceRespDto> applyArchitectureResourceList = new ArrayList<>();
        Map<ArchitectureType, List<CloudWorkOrderVdcCpuMemDo>> architectureResourceMap =
                applyResourceList.stream().collect(Collectors.groupingBy(CloudWorkOrderVdcCpuMemDo::getArchitecture));

        architectureResourceMap.forEach((key, list) -> {
            ApplyModifyVdcArchitectureResourceRespDto applyModifyVdcArchitectureResource =
                    new ApplyModifyVdcArchitectureResourceRespDto();
            applyModifyVdcArchitectureResource.setArchitectureType(key);

            CloudWorkOrderVdcCpuMemDo cpuDo =
                    list.stream().filter(item -> Objects.equals(item.getResourceType(), ArchitectureResourceType.CPU)).findFirst().orElse(null);
            applyModifyVdcArchitectureResource.setOldCpu(cpuDo.getOldSize());
            applyModifyVdcArchitectureResource.setApplyCpu(cpuDo.getApplySize());
            applyModifyVdcArchitectureResource.setRealCpu(cpuDo.getRealSize());

            CloudWorkOrderVdcCpuMemDo memDo =
                    list.stream().filter(item -> Objects.equals(item.getResourceType(), ArchitectureResourceType.MEM)).findFirst().orElse(null);

            applyModifyVdcArchitectureResource.setOldMem(memDo.getOldSize());
            applyModifyVdcArchitectureResource.setApplyMem(memDo.getApplySize());
            applyModifyVdcArchitectureResource.setRealMem(memDo.getRealSize());
            applyModifyVdcArchitectureResource.setMemUnit(memDo.getUnit());

            applyArchitectureResourceList.add(applyModifyVdcArchitectureResource);
        });
        applyModifyVdcDetail.setApplyArchitectureResourceList(applyArchitectureResourceList);
        return applyModifyVdcDetail;
    }
}
