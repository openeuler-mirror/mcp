package com.hnkylin.cloud.manage.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hnkylin.cloud.core.common.*;
import com.hnkylin.cloud.core.config.exception.KylinException;
import com.hnkylin.cloud.core.domain.*;
import com.hnkylin.cloud.core.enums.*;
import com.hnkylin.cloud.core.service.*;
import com.hnkylin.cloud.manage.config.MCConfigProperties;
import com.hnkylin.cloud.manage.constant.KylinCloudManageConstants;
import com.hnkylin.cloud.manage.constant.KylinHttpResponseVdcConstants;
import com.hnkylin.cloud.manage.entity.LoginUserVo;
import com.hnkylin.cloud.manage.entity.mc.req.QueryVdcUsedResourceParam;
import com.hnkylin.cloud.manage.entity.mc.resp.McClusterBaseResource;
import com.hnkylin.cloud.manage.entity.mc.resp.McClusterServerResourceResp;
import com.hnkylin.cloud.manage.entity.req.cluster.BaseClusterParam;
import com.hnkylin.cloud.manage.entity.req.org.BaseOrgParam;
import com.hnkylin.cloud.manage.entity.req.vdc.*;
import com.hnkylin.cloud.manage.entity.req.zone.BaseZoneParam;
import com.hnkylin.cloud.manage.entity.resp.vdc.*;
import com.hnkylin.cloud.manage.mapper.NetworkMapper;
import com.hnkylin.cloud.manage.mapper.VdcMapper;
import com.hnkylin.cloud.manage.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Slf4j
public class VdcServiceImpl implements VdcService {

    @Resource
    private CloudOrgVdcService cloudOrgVdcService;

    @Resource
    private CloudClusterService cloudClusterService;

    @Resource
    private McHttpService mcHttpService;

    @Resource
    private MCConfigProperties mcConfigProperties;

    @Resource
    private CloudVdcService cloudVdcService;

    @Resource
    private VdcMapper vdcMapper;

    @Resource
    private CloudVdcCpuService cloudVdcCpuService;

    @Resource
    private CloudVdcMemService cloudVdcMemService;

    @Resource
    private CloudVdcStorageService cloudVdcStorageService;

    @Resource
    private McClusterThreadService mcClusterThreadService;

    @Resource
    private CloudNetworkConfigService cloudNetworkConfigService;


    @Resource
    private UserService userService;


    @Resource
    private CloudZoneService cloudZoneService;

    @Resource
    private NetworkConfigService networkConfigService;

    @Resource
    private NetworkMapper networkMapper;

    @Resource
    private RoleService roleService;

    @Resource
    private OrgService orgService;


    @Resource
    private ClusterService clusterService;

    @Resource
    private UserMachineService userMachineService;

    @Resource
    private CloudWorkOrderService cloudWorkOrderService;

    @Resource
    private CloudWorkOrderVdcService cloudWorkOrderVdcService;

    @Resource
    private CloudWorkOrderVdcCpuMemService cloudWorkOrderVdcCpuMemService;

    @Resource
    private CloudOrganizationService cloudOrganizationService;


    @Override
    public List<VdcDetailRespDto> queryNotBindVdcByParentOrgId(BaseOrgParam baseOrgParam, LoginUserVo loginUserVo) {

        List<VdcDetailRespDto> vdcDetailList = new ArrayList<>();


        CloudOrganizationDo parentOrg = cloudOrganizationService.getById(baseOrgParam.getOrgId());
        Integer parentVdcId = 0;
        //如果父组织是顶级组织，则获取父ID==0的vdc
        //如果父组织不是顶级组织，则获取父组织对应的vdc的下级未绑定的VDC
        if (Objects.equals(parentOrg.getParentId(), KylinCloudManageConstants.TOP_PARENT_ID)) {
            parentVdcId = 0;
        } else {
            CloudOrgVdcDo orgVdcDo = cloudOrgVdcService.queryOrgVdcByOrgId(baseOrgParam.getOrgId());
            if (Objects.isNull(orgVdcDo)) {
                return vdcDetailList;
            }
            parentVdcId = orgVdcDo.getVdcId();
        }
        vdcDetailList = vdcMapper.getNotBindVdcListByParentId(parentVdcId);
        vdcDetailList.stream().forEach(item -> {

            item.setAllocationCpu(cloudVdcCpuService.totalCpuByVdcId(item.getVdcId()));
            CloudVdcStorageDo vdcTotalStorage = cloudVdcStorageService.getTotalStorageByVdcId(item.getVdcId());
            item.setAllocationDisk(vdcTotalStorage.getStorage());

            CloudVdcMemDo vdcTotalMemDo = cloudVdcMemService.totalMemByVdcId(item.getVdcId());

            item.setAllocationMem(vdcTotalMemDo.getMem());
        });
        return vdcDetailList;
    }


    @Override
    public McClusterBaseResource queryClusterBaseResource(BaseClusterParam baseClusterParam, LoginUserVo loginUserVo) {

        MCResponseData<Object> mcResponse = mcHttpService.hasDataCommonMcRequest(baseClusterParam.getClusterId(),
                null, mcConfigProperties.getMcClusterBaseResourceUrl(), loginUserVo.getUserName(), 0);

        if (Objects.nonNull(mcResponse) && Objects.equals(MCServerVmConstants.SUCCESS, mcResponse.getStatus())) {
            String serverListTxt = JSON.toJSONString(mcResponse.getData());
            log.info("McClusterBaseResource-:" + serverListTxt);
        }
        return null;
    }


    @Override
    public List<ParentVdcRespDto> vdcTreeByZone(BaseZoneParam baseZoneParam, LoginUserVo loginUserVo) {
        //获取可用区下可以绑定的上级VDC列表
        CloudVdcDo queryVdcDo = new CloudVdcDo();
        queryVdcDo.setDeleteFlag(false);
        queryVdcDo.setZoneId(baseZoneParam.getZoneId());
        QueryWrapper<CloudVdcDo> vdcWrapper = new QueryWrapper<>(queryVdcDo);
        vdcWrapper.orderByAsc("id");
        List<CloudVdcDo> vdcList = cloudVdcService.getBaseMapper().selectList(vdcWrapper);

        List<ParentVdcRespDto> parentVdcList = new ArrayList<>();

        if (vdcList.isEmpty()) {
            return parentVdcList;
        }
        //用户是否是平台管理用户
        boolean platformUser = userService.judgeIfPlatformUser(loginUserVo.getUserId());

        if (platformUser) {
            //如果是系统管理员，则可查询当前可用区下全部VDC
            for (CloudVdcDo vdcDo : vdcList) {
                if (Objects.equals(vdcDo.getParentId(), KylinCommonConstants.TOP_PARENT_ID)) {
                    parentVdcList.add(formatZoneVdcTree(vdcDo, vdcList));
                }
            }

        } else {
            //如果不是系统管理员， 只是组织管理员，则只能查询当前可用区下，登录用户拥有测VDC
            //查询登录用户对应的VDC
            CloudVdcDo loginUserVdc = getUserOrgBindVdc(loginUserVo.getUserId());
            if (Objects.nonNull(loginUserVdc)) {
                for (CloudVdcDo vdcDo : vdcList) {
                    if (Objects.equals(vdcDo.getId(), loginUserVdc.getId())) {
                        parentVdcList.add(formatZoneVdcTree(vdcDo, vdcList));
                    }
                }
            }
        }
        return parentVdcList;
    }


    /**
     * 组织管理-创建(编辑)组织时选择的父组织结构-封装组织数据
     */
    private ParentVdcRespDto formatZoneVdcTree(CloudVdcDo vdcDo,
                                               List<CloudVdcDo> vdcList) {
        ParentVdcRespDto parentVdcRespDto = new ParentVdcRespDto();
        createZoneVdcTreeDto(vdcDo, vdcList, parentVdcRespDto);
        parentVdcRespDto.setChildren(getZoneChildVdc(vdcDo.getId(), vdcList));
        return parentVdcRespDto;
    }

    /**
     * 根据vdc获取下级vdc列表
     */
    private List<ParentVdcRespDto> getZoneChildVdc(Integer parentId, List<CloudVdcDo> vdcList) {
        List<ParentVdcRespDto> childList = new ArrayList<>();
        for (CloudVdcDo vdcDo : vdcList) {
            if (Objects.equals(parentId, vdcDo.getParentId())) {
                ParentVdcRespDto vdcRespDto = formatZoneVdcTree(vdcDo, vdcList);
                childList.add(vdcRespDto);
            }
        }
        return childList;
    }

    /**
     * 创建vdc树形结构实体
     *
     * @param vdcDo
     * @param vdcList
     * @param commonVdcTreeRespDto
     */
    private void createZoneVdcTreeDto(CloudVdcDo vdcDo,
                                      List<CloudVdcDo> vdcList,
                                      CommonVdcTreeRespDto commonVdcTreeRespDto) {
        commonVdcTreeRespDto.setVdcId(vdcDo.getId());
        commonVdcTreeRespDto.setVdcName(vdcDo.getVdcName());
        commonVdcTreeRespDto.setParentId(vdcDo.getParentId());
        commonVdcTreeRespDto.setParentName("");
        CloudVdcDo parentDo =
                vdcList.stream().filter(item -> Objects.equals(item.getId(), vdcDo.getParentId())).findFirst().orElse(null);
        if (Objects.nonNull(parentDo)) {
            commonVdcTreeRespDto.setParentName(parentDo.getVdcName());
        }
        commonVdcTreeRespDto.setVcpu(cloudVdcCpuService.totalCpuByVdcId(vdcDo.getId()));

        CloudVdcMemDo vdcTotalMemDo = cloudVdcMemService.totalMemByVdcId(vdcDo.getId());
        commonVdcTreeRespDto.setMem(vdcTotalMemDo.getMem());
        commonVdcTreeRespDto.setMemUnit(vdcTotalMemDo.getMemUnit());

        CloudVdcStorageDo vdcTotalStorage = cloudVdcStorageService.getTotalStorageByVdcId(vdcDo.getId());
        commonVdcTreeRespDto.setStorage(vdcTotalStorage.getStorage());
        commonVdcTreeRespDto.setStorageUnit(vdcTotalStorage.getUnit());
    }


    /**
     * 检查可用区下上级VDC能否被绑定
     * 规则：如果上级VDC为0: 检查可用区是否被背的VDC绑定，如果已绑定则提示  不能绑定该可用区，
     * 如果上级VDC不为0  检查该条VDC连是否已超过最多5级的限制
     * 能绑定：返回上级VDC的资源情况
     *
     * @param checkCreateVdcParam
     * @param loginUserVo
     * @return
     */
    @Override
    public VdcResourceRespDto checkCreateVdc(CheckCreateVdcParam checkCreateVdcParam, LoginUserVo loginUserVo) {


        if (Objects.equals(checkCreateVdcParam.getParentVdcId(), KylinCloudManageConstants.TOP_PARENT_ID)) {

            //校验用户是否拥有创建一级VDC权限
            //获取登录用户角色
            boolean topUser = userService.judgeIfPlatformUser(loginUserVo.getUserId());
            if (!topUser) {
                throw new KylinException(KylinHttpResponseVdcConstants.NOT_PERMISSION_CREATE_FIRST_VDC);
            }
            //如果上级VDC为0: 则说明是一级VDC，做不限制资源分陪
            return createZoneResource(checkCreateVdcParam.getZoneId(), loginUserVo);
        }
        //如果上级VDC不为0  检查该条VDC连是否已超过最多5级的限制
        boolean exceedVdcTire = checkExceedTire(checkCreateVdcParam.getParentVdcId());
        if (exceedVdcTire) {
            throw new KylinException(KylinHttpResponseVdcConstants.VDC_EXCEED_TIER);
        }
        return createVdcResource(checkCreateVdcParam.getParentVdcId(), loginUserVo);
    }

    /**
     * 检查可用区是否被绑定
     *
     * @param zoneId
     * @return
     */
    private boolean checkZoneIfBind(Integer zoneId) {

        //获取可用区下可以绑定的上级VDC列表
        CloudVdcDo queryVdcDo = new CloudVdcDo();
        queryVdcDo.setDeleteFlag(false);
        queryVdcDo.setZoneId(zoneId);
        QueryWrapper<CloudVdcDo> vdcWrapper = new QueryWrapper<>(queryVdcDo);
        List<CloudVdcDo> vdcList = cloudVdcService.getBaseMapper().selectList(vdcWrapper);

        return !vdcList.isEmpty();
    }

    /**
     * 检查是否超出5级的限制
     *
     * @param parentVdcId
     * @return
     */
    private boolean checkExceedTire(Integer parentVdcId) {
        int vdcTire = getVdcTire(parentVdcId, 1);
        return vdcTire >= KylinHttpResponseVdcConstants.VDC_MAX_TIRE;
    }


    /**
     * 地柜获取VDC的层数
     *
     * @param vdcId
     * @param tire
     * @return
     */
    private int getVdcTire(Integer vdcId, int tire) {
        CloudVdcDo vdcDo = cloudVdcService.getById(vdcId);
        if (!Objects.equals(vdcDo.getParentId(), KylinCloudManageConstants.TOP_PARENT_ID)) {
            tire++;
            return getVdcTire(vdcDo.getParentId(), tire);
        } else {
            return tire;
        }
    }


    /**
     * 创建上级VDC的资源池对象
     * VDC已使用资源=直接挂在本级VDC(组织)下用户云服务器占用资源+已分配给下级VDC的资源
     *
     * @param parentVdcId
     * @return
     */
    private VdcResourceRespDto createVdcResource(Integer parentVdcId, LoginUserVo loginUserVo) {
        VdcResourceRespDto vdcResourceRespDto = new VdcResourceRespDto();


        //获取直接挂在本级VDC(组织)下用户云服务器占用资源
        VdcSameLevelUserUsedResourceRespDto vdcSameLevelUserUseResource = getVdcSameLevelUserUseResource(parentVdcId,
                loginUserVo);

        //总存储
        CloudVdcStorageDo vdcTotalStorage = cloudVdcStorageService.getTotalStorageByVdcId(parentVdcId);
        vdcResourceRespDto.setStorageUnit(vdcTotalStorage.getUnit());
        //获取已分配给下级VDC的存储
        CloudVdcStorageDo childVdcStorage = cloudVdcStorageService.getChildVdcTotalStorage(parentVdcId);
        //本级VDC用户直接使用存储资源
        Integer sameVdcUserUsedStorage = Objects.nonNull(vdcSameLevelUserUseResource) ?
                vdcSameLevelUserUseResource.getUsedStorage() : 0;
        //可用存储=vdc总存储-本级VDC用户直接使用存储资源-已分配给下级VDC存储
        vdcResourceRespDto.setUsableStorage(vdcTotalStorage.getStorage() - childVdcStorage.getStorage() - sameVdcUserUsedStorage);
        //已使用存储=本级VDC用户直接使用存储资源+已分配给下级VDC存储
        vdcResourceRespDto.setUsedStorage(childVdcStorage.getStorage() + sameVdcUserUsedStorage);
        vdcResourceRespDto.setTotalStorage(vdcTotalStorage.getStorage());
        //遍历架构获取架构资源
        List<ArchitectureType> architectureTypeList = Arrays.asList(ArchitectureType.values());
        List<VdcArchitectureResourceRespDto> architectureResourceList = new ArrayList<>();
        architectureTypeList.forEach(architectureType -> {

            //获取本级VDC用户直接使用该架构资源
            VdcSameLevelUserUsedArchitectureResourceRespDto sameUserUsedArchitectureResource =
                    Objects.isNull(vdcSameLevelUserUseResource) ? null :
                            vdcSameLevelUserUseResource.getUserUsedArchitectureResourceList().stream().
                                    filter(item -> Objects.equals(item.getArchitectureType(), architectureType)).findFirst().orElse(null);

            //VDC总CPU
            Integer totalCpu = cloudVdcCpuService.totalCpuByVdcIdAndArchitectureType(parentVdcId,
                    architectureType);

            if (Objects.nonNull(totalCpu)) {
                VdcArchitectureResourceRespDto vdcArchitectureResource = new VdcArchitectureResourceRespDto();
                vdcArchitectureResource.setArchitectureType(architectureType);

                //已分配给下级CPU
                Integer childCpuTotal = cloudVdcCpuService.totalChildVdcCpuByParentIdAndArchitectureType(parentVdcId,
                        architectureType);
                //本级VDC用户直接使用CPU
                Integer sameVdcUserUsedCpu = Objects.nonNull(sameUserUsedArchitectureResource) ?
                        sameUserUsedArchitectureResource.getUsedVcpu() : 0;
                //剩余可用CPU=总cpu-本级VDC用户直接使用cpu-已分配给下级cpu
                vdcArchitectureResource.setUsableVcpu(totalCpu - childCpuTotal - sameVdcUserUsedCpu);
                //已使用CPU=本级VDC用户直接使用cpu+已分配给下级cpu
                vdcArchitectureResource.setUsedCpu(childCpuTotal + sameVdcUserUsedCpu);
                vdcArchitectureResource.setTotalCpu(totalCpu);
                //VDC总内存
                CloudVdcMemDo totalMem = cloudVdcMemService.totalMemByVdcIdAndArchitectureType(parentVdcId,
                        architectureType);
                //已分配给下级VDC存储
                CloudVdcMemDo childTotalMem =
                        cloudVdcMemService.totalChildVdcMemByParentIdAndArchitectureType(parentVdcId,
                                architectureType);

                //本级VDC用户直接使用mem
                Integer sameVdcUserUsedMem = Objects.nonNull(sameUserUsedArchitectureResource) ?
                        sameUserUsedArchitectureResource.getUsedMem() : 0;

                vdcArchitectureResource.setMemUnit(totalMem.getMemUnit());
                //剩余可用内存=总内存-本级VDC用户直接使用内存-已分配给下级内存
                vdcArchitectureResource.setUsableMem(totalMem.getMem() - childTotalMem.getMem() - sameVdcUserUsedMem);
                //已使用内存=本级VDC用户直接使用内存+已分配给下级内存
                vdcArchitectureResource.setUsedMem(childTotalMem.getMem() + sameVdcUserUsedMem);
                vdcArchitectureResource.setTotalMem(totalMem.getMem());
                architectureResourceList.add(vdcArchitectureResource);
            }

        });
        vdcResourceRespDto.setArchitectureResourceList(architectureResourceList);


        return vdcResourceRespDto;
    }


    /**
     * 创建可用区中资源
     * 根据可用区获取集群列表，分别获取获取集群中的资源
     *
     * @param zoneId
     * @return
     */
    private VdcResourceRespDto createZoneResource(Integer zoneId, LoginUserVo loginUserVo) {

        //
        VdcResourceRespDto zoneResource = new VdcResourceRespDto();
        //可用区下所有物理集群资源和
        VdcResourceRespDto zoneClusterResource = new VdcResourceRespDto();
        List<CloudClusterDo> clusterDoList = cloudClusterService.listClusterBYZoneId(zoneId);
        if (!clusterDoList.isEmpty()) {
            if (clusterDoList.size() == 1) {
                McClusterBaseResource mcClusterBaseResource =
                        clusterService.getMcClusterBaseResource(clusterDoList.get(0),
                                loginUserVo.getUserName());
                zoneClusterResource = changeMcClusterBaseResourceToVdcResource(mcClusterBaseResource);
            } else {
                zoneClusterResource = threadGetMcBaseResource(clusterDoList, loginUserVo.getUserName());
            }
        }

        zoneResource.setUsableStorage(zoneClusterResource.getUsableStorage());
        zoneResource.setStorageUnit(zoneClusterResource.getStorageUnit());
        List<VdcArchitectureResourceRespDto> zoneArchitectureResourceList =
                zoneClusterResource.getArchitectureResourceList();
        zoneResource.setArchitectureResourceList(zoneArchitectureResourceList);
        return zoneResource;
    }


    /**
     * 获取直接挂在本级VDC(组织)下用户已使用资源
     *
     * @param vdcId
     * @param loginUserVo
     * @return
     */
    private VdcSameLevelUserUsedResourceRespDto getVdcSameLevelUserUseResource(Integer vdcId, LoginUserVo loginUserVo) {

        //获取VDC对应组织
        CloudOrganizationDo organizationDo = orgService.getOrgByVdcId(vdcId);
        if (Objects.isNull(organizationDo)) {
            return null;
        }
        //获取组织下用户列表
        List<CloudUserDo> userDoList = userService.listUserByOrgId(organizationDo.getId());
        if (userDoList.isEmpty()) {
            return null;
        }

        //用户用户拥有的云服务器关联关系
        List<CloudUserMachineDo> userMachineDoList =
                userMachineService.listUserMachineByUserIdList(userDoList.stream().map(CloudUserDo::getId).collect(Collectors.toList()));
        if (userMachineDoList.isEmpty()) {
            return null;
        }
        //请求mc 获取这些云服务器   所占用的资源(cpu，内存，存储)
        //将用户云服务器进行分组
        Map<Integer, List<CloudUserMachineDo>> clusterUserMachineMap =
                userMachineDoList.stream().collect(Collectors.groupingBy(CloudUserMachineDo::getClusterId));
        if (clusterUserMachineMap.size() == 1) {
            //如果是只有一个集群有云服务器，则直接请求
            return singleClusterVdcUsedResource(userMachineDoList, loginUserVo);
        } else {
            //如果是多集群，需线程池去调用，然后在数据进行汇总
            return threadGetVdcUserUsedResource(clusterUserMachineMap, loginUserVo);
        }


    }

    /**
     * VDC下用户在单个集群中拥有云服务器，请求mc获取这些云服务器所占用资源
     */
    private VdcSameLevelUserUsedResourceRespDto singleClusterVdcUsedResource(List<CloudUserMachineDo> userMachineDoList,
                                                                             LoginUserVo loginUserVo) {
        List<String> uuidList =
                userMachineDoList.stream().map(CloudUserMachineDo::getMachineUuid).collect(Collectors.toList());
        QueryVdcUsedResourceParam queryVdcUsedResourceParam = new QueryVdcUsedResourceParam();
        queryVdcUsedResourceParam.setUuidList(uuidList);

        try {
            MCResponseData<Object> mcResponse =
                    mcHttpService.hasDataCommonMcRequest(userMachineDoList.get(0).getClusterId(),
                            queryVdcUsedResourceParam,
                            mcConfigProperties.getServerVmsUsedResourceUrl(), loginUserVo.getUserName(), 0);
            if (Objects.nonNull(mcResponse) && Objects.equals(MCServerVmConstants.SUCCESS, mcResponse.getStatus())) {
                VdcSameLevelUserUsedResourceRespDto vdcSameUserUsedResource =
                        JSON.parseObject(JSON.toJSONString(mcResponse.getData()),
                                VdcSameLevelUserUsedResourceRespDto.class);
                int totalUsedCpu =
                        vdcSameUserUsedResource.getUserUsedArchitectureResourceList().stream()
                                .collect(Collectors.summingInt(VdcSameLevelUserUsedArchitectureResourceRespDto::getUsedVcpu));
                vdcSameUserUsedResource.setUsedCpu(totalUsedCpu);
                int totalUsedMem =
                        vdcSameUserUsedResource.getUserUsedArchitectureResourceList().stream()
                                .collect(Collectors.summingInt(VdcSameLevelUserUsedArchitectureResourceRespDto::getUsedMem));
                vdcSameUserUsedResource.setUsedMem(totalUsedMem);
                return vdcSameUserUsedResource;
            }
        } catch (Exception e) {
            return null;
        }
        return null;
    }


    /**
     * VDC下用户在多个集群中拥有云服务器，多线程方式获取这些已使用的资源
     *
     * @param
     * @param
     */
    private VdcSameLevelUserUsedResourceRespDto threadGetVdcUserUsedResource(Map<Integer, List<CloudUserMachineDo>> clusterUserMachineMap, LoginUserVo loginUserVo) {

        VdcSameLevelUserUsedResourceRespDto vdcSameUserUsedResource = new VdcSameLevelUserUsedResourceRespDto();
        List<Integer> clusterIdList = new ArrayList<>();
        List<Object> mcRequestObjectList = new ArrayList<>();
        clusterUserMachineMap.forEach((key, value) -> {
            clusterIdList.add(key);
            List<String> uuidList = value.stream().map(CloudUserMachineDo::getMachineUuid).collect(Collectors.toList());
            QueryVdcUsedResourceParam queryVdcUsedResourceParam = new QueryVdcUsedResourceParam();
            queryVdcUsedResourceParam.setUuidList(uuidList);
            mcRequestObjectList.add(queryVdcUsedResourceParam);
        });


        List<String> resultList = mcClusterThreadService.threadGetMcResponse(clusterIdList,
                loginUserVo.getUserName(),
                mcConfigProperties.getServerVmsUsedResourceUrl(), mcRequestObjectList);


        List<VdcSameLevelUserUsedResourceRespDto> vdcSameUserUsedResourceList = new ArrayList<>();
        Stream.iterate(0, i -> i + 1).limit(clusterIdList.size()).forEach(i -> {
            String mcBaseResourceStr = resultList.get(i);
            if (Objects.nonNull(mcBaseResourceStr)) {
                VdcSameLevelUserUsedResourceRespDto userUsedResourceRespDto = JSON.parseObject(mcBaseResourceStr,
                        VdcSameLevelUserUsedResourceRespDto.class);
                vdcSameUserUsedResourceList.add(userUsedResourceRespDto);
            }
        });
        //将多个集群中的已使用存储汇总
        int usedTotalStorage =
                vdcSameUserUsedResourceList.stream().collect(Collectors.summingInt(VdcSameLevelUserUsedResourceRespDto::getUsedStorage));
        vdcSameUserUsedResource.setUsedStorage(usedTotalStorage);

        //所有架构资源
        List<VdcSameLevelUserUsedArchitectureResourceRespDto> usedArchitectureResourceList = new ArrayList<>();

        for (VdcSameLevelUserUsedResourceRespDto userUsedResource : vdcSameUserUsedResourceList) {
            //每一个mc集群中架构资源列表
            List<VdcSameLevelUserUsedArchitectureResourceRespDto> eachArchitectureUsedResourceList =
                    userUsedResource.getUserUsedArchitectureResourceList();
            usedArchitectureResourceList.addAll(eachArchitectureUsedResourceList);
        }
        //将所有架构资源按照架构进行分组
        Map<ArchitectureType, List<VdcSameLevelUserUsedArchitectureResourceRespDto>> differentArchitectureMap =
                usedArchitectureResourceList.stream().collect(Collectors.groupingBy(VdcSameLevelUserUsedArchitectureResourceRespDto::getArchitectureType));

        //相同架构合并和的资源列表
        List<VdcSameLevelUserUsedArchitectureResourceRespDto> afterMergeResource = new ArrayList<>();
        differentArchitectureMap.forEach((architectureType, list) -> {

            VdcSameLevelUserUsedArchitectureResourceRespDto userUsedArchitectureResource =
                    new VdcSameLevelUserUsedArchitectureResourceRespDto();
            int totalUsedCpu =
                    list.stream().mapToInt(VdcSameLevelUserUsedArchitectureResourceRespDto::getUsedVcpu).sum();
            userUsedArchitectureResource.setUsedVcpu(totalUsedCpu);
            int totalUsedMem =
                    list.stream().mapToInt(VdcSameLevelUserUsedArchitectureResourceRespDto::getUsedMem).sum();
            userUsedArchitectureResource.setUsedMem(totalUsedMem);
            userUsedArchitectureResource.setArchitectureType(architectureType);
            afterMergeResource.add(userUsedArchitectureResource);
        });
        vdcSameUserUsedResource.setUserUsedArchitectureResourceList(afterMergeResource);
        int totalUsedCpu =
                vdcSameUserUsedResource.getUserUsedArchitectureResourceList().stream()
                        .collect(Collectors.summingInt(VdcSameLevelUserUsedArchitectureResourceRespDto::getUsedVcpu));
        vdcSameUserUsedResource.setUsedCpu(totalUsedCpu);
        int totalUsedMem =
                vdcSameUserUsedResource.getUserUsedArchitectureResourceList().stream()
                        .collect(Collectors.summingInt(VdcSameLevelUserUsedArchitectureResourceRespDto::getUsedMem));
        vdcSameUserUsedResource.setUsedMem(totalUsedMem);
        return vdcSameUserUsedResource;
    }


//    /**
//     * 获取可用区下一级所有一级VDC资源
//     *
//     * @param zoneId
//     * @return
//     */
//    private VdcResourceRespDto getZoneFirstVdcResource(Integer zoneId) {
//
//        VdcResourceRespDto zoneFirstVdcResource = new VdcResourceRespDto();
//
//        List<CloudVdcDo> zoneFirstVdcDoList = cloudVdcService.getFirstVdcListByZone(zoneId);
//        if (!zoneFirstVdcDoList.isEmpty()) {
//            //所有一级VDC的全部架构资源
//            List<VdcArchitectureResourceRespDto> allVdcArchitectureResourceList = new ArrayList<>();
//            zoneFirstVdcDoList.forEach(vdc -> {
//                CloudVdcStorageDo vdcTotalStorage = cloudVdcStorageService.getTotalStorageByVdcId(vdc.getId());
//                zoneFirstVdcResource.setStorageUnit(vdcTotalStorage.getUnit());
//                //遍历架构获取架构资源
//                List<ArchitectureType> architectureTypeList = Arrays.asList(ArchitectureType.values());
//                architectureTypeList.forEach(architectureType -> {
//                    //不同架构下cpu数
//                    Integer totalCpu = cloudVdcCpuService.totalCpuByVdcIdAndArchitectureType(vdc.getId(),
//                            architectureType);
//                    if (Objects.nonNull(totalCpu)) {
//                        //不同架构下的内存
//                        CloudVdcMemDo totalMem = cloudVdcMemService.totalMemByVdcIdAndArchitectureType(vdc.getId(),
//                                architectureType);
//                        VdcArchitectureResourceRespDto vdcArchitectureResource = new VdcArchitectureResourceRespDto();
//                        vdcArchitectureResource.setMemUnit(totalMem.getMemUnit());
//                        vdcArchitectureResource.setArchitectureType(architectureType);
//                        allVdcArchitectureResourceList.add(vdcArchitectureResource);
//                    }
//
//                });
//            });
//            //架构资源进行分组
//            Map<ArchitectureType, List<VdcArchitectureResourceRespDto>> architectureResourceMap =
//                    allVdcArchitectureResourceList.stream().collect(Collectors.groupingBy
//                    (VdcArchitectureResourceRespDto::getArchitectureType));
//
//            List<VdcArchitectureResourceRespDto> architectureResourceList = new ArrayList<>();
//            architectureResourceMap.forEach((architecture, equalArchitectureResourceList) -> {
//                VdcArchitectureResourceRespDto vdcArchitectureResource = new VdcArchitectureResourceRespDto();
//                int totalCpu =
//                        equalArchitectureResourceList.stream().mapToInt
//                        (VdcArchitectureResourceRespDto::getTotalVcpu).sum();
//                vdcArchitectureResource.setTotalVcpu(totalCpu);
//                int totalMem =
//                        equalArchitectureResourceList.stream().mapToInt
//                        (VdcArchitectureResourceRespDto::getTotalMem).sum();
//                vdcArchitectureResource.setMemUnit(equalArchitectureResourceList.get(0).getMemUnit());
//                vdcArchitectureResource.setTotalMem(totalMem);
//                vdcArchitectureResource.setArchitectureType(architecture);
//                architectureResourceList.add(vdcArchitectureResource);
//            });
//
//            zoneFirstVdcResource.setArchitectureResourceList(architectureResourceList);
//        }
//        return zoneFirstVdcResource;
//    }


    /**
     * 线程方式获取mc基础资源
     *
     * @param clusterDoList
     * @param loginName
     */
    private VdcResourceRespDto threadGetMcBaseResource(List<CloudClusterDo> clusterDoList, String loginName) {

        VdcResourceRespDto parentVdcResourceRespDto = new VdcResourceRespDto();
        List<String> resultList =
                mcClusterThreadService.threadGetMcResponse(clusterDoList.stream().map(CloudClusterDo::getId).collect(Collectors.toList()),
                        loginName,
                        mcConfigProperties.getMcClusterBaseResourceUrl(), new ArrayList<>());


        //mc集群资源列表
        List<VdcResourceRespDto> vdcResourceList = new ArrayList<>();
        Stream.iterate(0, i -> i + 1).limit(clusterDoList.size()).forEach(i -> {
            String mcBaseResourceStr = resultList.get(i);
            if (Objects.nonNull(mcBaseResourceStr)) {
                McClusterBaseResource baseResource = JSON.parseObject(mcBaseResourceStr, McClusterBaseResource.class);
                vdcResourceList.add(changeMcClusterBaseResourceToVdcResource(baseResource));
            }
        });
        //将多个mc基础资源机进行汇总
        int totalStorage =
                vdcResourceList.stream().collect(Collectors.summingInt(VdcResourceRespDto::getUsableStorage));
        parentVdcResourceRespDto.setUsableStorage(totalStorage);
        parentVdcResourceRespDto.setStorageUnit(StorageUnit.GB);

        //所有架构资源
        List<VdcArchitectureResourceRespDto> allResourceList = new ArrayList<>();

        for (VdcResourceRespDto item : vdcResourceList) {
            //每一个mc集群中架构资源列表
            List<VdcArchitectureResourceRespDto> eachClusterArchitectureResourceList =
                    item.getArchitectureResourceList();
            allResourceList.addAll(eachClusterArchitectureResourceList);
        }
        //将所有架构资源按照架构进行分组
        Map<ArchitectureType, List<VdcArchitectureResourceRespDto>> differentArchitectureMap =
                allResourceList.stream().collect(Collectors.groupingBy(VdcArchitectureResourceRespDto::getArchitectureType));

        //相同架构合并和的资源列表
        List<VdcArchitectureResourceRespDto> afterMergeResource = new ArrayList<>();
        differentArchitectureMap.forEach((architectureType, value) -> {
            VdcArchitectureResourceRespDto resourceRespDto = mergeEqualArchitectureResource(value);
            afterMergeResource.add(resourceRespDto);
        });
        parentVdcResourceRespDto.setArchitectureResourceList(afterMergeResource);
        return parentVdcResourceRespDto;
    }


    /**
     * 将多个集群中相同架构资源进行汇总
     *
     * @param equalArchitectureResourceList 相同架构的资源列表
     * @return
     */
    private VdcArchitectureResourceRespDto mergeEqualArchitectureResource(List<VdcArchitectureResourceRespDto> equalArchitectureResourceList) {
        VdcArchitectureResourceRespDto vdcArchitectureResource = new VdcArchitectureResourceRespDto();
        int totalCpu =
                equalArchitectureResourceList.stream().mapToInt(VdcArchitectureResourceRespDto::getUsableVcpu).sum();
        vdcArchitectureResource.setUsableVcpu(totalCpu);

        int totalMem =
                equalArchitectureResourceList.stream().mapToInt(VdcArchitectureResourceRespDto::getUsableMem).sum();
        vdcArchitectureResource.setUsableMem(totalMem);
        vdcArchitectureResource.setMemUnit(equalArchitectureResourceList.get(0).getMemUnit());
        vdcArchitectureResource.setArchitectureType(equalArchitectureResourceList.get(0).getArchitectureType());
        return vdcArchitectureResource;
    }

    /**
     * 将mc中集群资源转换成VDc资源对象
     *
     * @param mcClusterBaseResource
     * @return
     */
    private VdcResourceRespDto changeMcClusterBaseResourceToVdcResource(McClusterBaseResource mcClusterBaseResource) {
        VdcResourceRespDto vdcResource = new VdcResourceRespDto();
        if (Objects.nonNull(mcClusterBaseResource)) {
            int totalStorage = mcClusterBaseResource.getResourceInfo().getStorageTotal().setScale(0,
                    BigDecimal.ROUND_HALF_UP).intValue();
            vdcResource.setUsableStorage(totalStorage);
            vdcResource.setStorageUnit(StorageUnit.GB);
            List<VdcArchitectureResourceRespDto> architectureResourceList = new ArrayList<>();
            List<McClusterServerResourceResp> mcSevers = mcClusterBaseResource.getSevers();
            if (!mcSevers.isEmpty()) {
                //按照架构进行分组
                Map<ArchitectureType, List<McClusterServerResourceResp>> architectureServersMap =
                        mcSevers.stream().collect(Collectors.groupingBy(McClusterServerResourceResp::getArchitecture));

                architectureServersMap.forEach((architecture, servers) -> {
                    architectureResourceList.add(createArchitectureResource(servers, architecture));

                });
            }
            vdcResource.setArchitectureResourceList(architectureResourceList);
        }

        return vdcResource;
    }

    /**
     * 根据mc服务器列表创建vdc架构资源
     *
     * @return
     */
    private VdcArchitectureResourceRespDto createArchitectureResource(List<McClusterServerResourceResp> mcClusterServerResourceRespList
            , ArchitectureType architectureType) {
        VdcArchitectureResourceRespDto architectureResource = new VdcArchitectureResourceRespDto();
        int totalCpu =
                mcClusterServerResourceRespList.stream().collect(Collectors.summingInt(McClusterServerResourceResp::getVcpus));
        architectureResource.setUsableVcpu(totalCpu);

//        BigDecimal totalMemory =
//                mcClusterServerResourceRespList.stream().map(McClusterServerResourceResp::getMemory).reduce
//                (BigDecimal.ZERO,
//                        BigDecimal::add);
//        //转换成GB
//        BigDecimal totalMemoryGB = totalMemory.divide(new BigDecimal("1024")).setScale(0, BigDecimal.ROUND_HALF_UP);

        //转换成GB
        int totalMem =
                mcClusterServerResourceRespList.stream().mapToInt(item -> item.getMemory().divide(new BigDecimal(
                        "1024")).setScale(0, BigDecimal.ROUND_HALF_UP).intValue()).sum();


        architectureResource.setUsableMem(totalMem);
        architectureResource.setMemUnit(MemUnit.GB);
        architectureResource.setArchitectureType(architectureType);
        return architectureResource;
    }


    /**
     * 检查是否存在同名Vdc
     */
    private void checkIfExistVdcName(String vdcName) {
        //判断名称是否已经存在
        CloudVdcDo cloudVdcDo = new CloudVdcDo();
        cloudVdcDo.setVdcName(vdcName);
        cloudVdcDo.setDeleteFlag(false);
        Wrapper<CloudVdcDo> wrapper = new QueryWrapper<>(cloudVdcDo);
        List<CloudVdcDo> vdcDoList = cloudVdcService.getBaseMapper().selectList(wrapper);
        if (!vdcDoList.isEmpty()) {
            throw new KylinException(KylinHttpResponseVdcConstants.EXIST_VDC_NAME);
        }
    }

    @Override
    @Transactional
    public void createVdc(CreateVdcParam createVdcParam, LoginUserVo loginUserVo) {

        checkIfExistVdcName(createVdcParam.getVdcName());

        Date now = new Date();
        CloudVdcDo cloudVdcDo = new CloudVdcDo();
        cloudVdcDo.setParentId(createVdcParam.getParentVdcId());
        cloudVdcDo.setVdcName(createVdcParam.getVdcName());
        cloudVdcDo.setRemark(createVdcParam.getRemark());
        cloudVdcDo.setCreateBy(loginUserVo.getUserId());
        cloudVdcDo.setCreateTime(now);
        cloudVdcDo.setZoneId(createVdcParam.getZoneId());
        cloudVdcService.save(cloudVdcDo);

        //插入VDC存储
        CloudVdcStorageDo vdcStorageDo = new CloudVdcStorageDo();
        vdcStorageDo.setVdcId(cloudVdcDo.getId());
        vdcStorageDo.setStorage(createVdcParam.getAllocationStorage());
        vdcStorageDo.setUnit(createVdcParam.getStorageUnit());
        vdcStorageDo.setCreateBy(loginUserVo.getUserId());
        vdcStorageDo.setCreateTime(now);
        cloudVdcStorageService.save(vdcStorageDo);

        //插入架构资源
        List<CloudVdcCpuDo> vdcCpuDoList = new ArrayList<>();
        List<CloudVdcMemDo> vdcMemDoList = new ArrayList<>();
        createVdcParam.getArchitectureResourceList().forEach(item -> {

            CloudVdcCpuDo vdcCpuDo = new CloudVdcCpuDo();
            vdcCpuDo.setVdcId(cloudVdcDo.getId());
            vdcCpuDo.setArchitecture(item.getArchitectureType());
            vdcCpuDo.setVcpus(item.getAllocationCpu());
            vdcCpuDo.setCreateBy(loginUserVo.getUserId());
            vdcCpuDo.setCreateTime(now);
            vdcCpuDoList.add(vdcCpuDo);


            CloudVdcMemDo vdcMemDo = new CloudVdcMemDo();
            vdcMemDo.setVdcId(cloudVdcDo.getId());
            vdcMemDo.setArchitecture(item.getArchitectureType());
            vdcMemDo.setMem(item.getAllocationMem());
            vdcMemDo.setMemUnit(item.getMemUnit());
            vdcMemDo.setCreateBy(loginUserVo.getUserId());
            vdcMemDo.setCreateTime(now);
            vdcMemDoList.add(vdcMemDo);

        });

        if (!vdcCpuDoList.isEmpty()) {
            cloudVdcCpuService.saveBatch(vdcCpuDoList);
        }
        if (!vdcMemDoList.isEmpty()) {
            cloudVdcMemService.saveBatch(vdcMemDoList);
        }

        //网络资源处理
        List<CloudNetworkConfigDo> networkList = new ArrayList<>();
        createVdcParam.getNetworkList().forEach(network -> {
            CloudNetworkConfigDo cloudNetworkConfigDo = new CloudNetworkConfigDo();
            cloudNetworkConfigDo.setCreateBy(loginUserVo.getUserId());
            cloudNetworkConfigDo.setVdcId(cloudVdcDo.getId());
            cloudNetworkConfigDo.setCreateTime(new Date());
            BeanUtils.copyProperties(network, cloudNetworkConfigDo);
            cloudNetworkConfigDo.setInterfaceType(KylinCloudManageConstants.BRIDGE);
            networkList.add(cloudNetworkConfigDo);
        });
        if (!networkList.isEmpty()) {
            cloudNetworkConfigService.saveBatch(networkList);
        }
    }


    @Override
    public List<VdcTreeRespDto> vdcTree(LoginUserVo loginUserVo) {

        CloudRoleDo roleDo = roleService.getUserRole(loginUserVo.getUserId());

        if (Objects.isNull(roleDo)) {
            return new ArrayList<>();
        }

        CloudVdcDo queryVdcDo = new CloudVdcDo();
        queryVdcDo.setDeleteFlag(false);
        QueryWrapper<CloudVdcDo> vdcWrapper = new QueryWrapper<>(queryVdcDo);
        List<CloudVdcDo> vdcList = cloudVdcService.getBaseMapper().selectList(vdcWrapper);

        if (vdcList.isEmpty()) {
            return new ArrayList<>();
        }

        List<VdcTreeRespDto> vdcTreeList = new ArrayList<>();
        //如果是系统管理员及顶级组织的组织管理员，则全部VDC都是该登录用户可见的
        boolean ifPlatformUser = userService.judgeIfPlatformUser(loginUserVo.getUserId());
        if (ifPlatformUser) {
            for (CloudVdcDo vdcDo : vdcList) {
                if (Objects.equals(vdcDo.getParentId(), KylinCloudManageConstants.TOP_PARENT_ID)) {
                    vdcTreeList.add(formatVdcTree(vdcDo, vdcList, loginUserVo));
                }
            }
        } else {
            //如果不是则 查询登录用户对应组织拥有的VDC

            CloudVdcDo userVdcDo = getUserOrgBindVdc(loginUserVo.getUserId());
            if (Objects.isNull(userVdcDo)) {
                return new ArrayList<>();
            }
            for (CloudVdcDo vdcDo : vdcList) {
                if (Objects.equals(vdcDo.getId(), userVdcDo.getId())) {
                    vdcTreeList.add(formatVdcTree(vdcDo, vdcList, loginUserVo));
                }
            }
        }

        return vdcTreeList;

    }

    /**
     * VDC本级用户已使用已使用资源
     *
     * @param vdcId
     * @param loginUserVo
     * @return
     */
    private VdcSameLevelUserUsedResourceRespDto createVdcSameUsedResource(Integer vdcId, LoginUserVo loginUserVo) {

        //获取直接挂在本级VDC(组织)下用户云服务器占用资源
        VdcSameLevelUserUsedResourceRespDto vdcSameLevelUserUseResource = getVdcSameLevelUserUseResource(vdcId,
                loginUserVo);
        if (Objects.nonNull(vdcSameLevelUserUseResource)) {
            vdcSameLevelUserUseResource.setUsedCpu(vdcSameLevelUserUseResource.getUserUsedArchitectureResourceList().stream()
                    .mapToInt(VdcSameLevelUserUsedArchitectureResourceRespDto::getUsedVcpu).sum());
            vdcSameLevelUserUseResource.setUsedMem(vdcSameLevelUserUseResource.getUserUsedArchitectureResourceList().stream()
                    .mapToInt(VdcSameLevelUserUsedArchitectureResourceRespDto::getUsedMem).sum());
        }
        return Objects.isNull(vdcSameLevelUserUseResource) ?
                new VdcSameLevelUserUsedResourceRespDto() : vdcSameLevelUserUseResource;
    }

    /**
     * 组织管理-创建(编辑)组织时选择的父组织结构-封装组织数据
     */
    private VdcTreeRespDto formatVdcTree(CloudVdcDo vdcDo,
                                         List<CloudVdcDo> vdcList, LoginUserVo loginUserVo) {
        VdcTreeRespDto vdcTree = new VdcTreeRespDto();
        createVdcTreeDto(vdcDo, vdcList, vdcTree, loginUserVo);
        vdcTree.setChildren(getChildVdc(vdcDo.getId(), vdcList, loginUserVo));
        return vdcTree;
    }

    /**
     * 根据vdc获取下级vdc列表
     */
    private List<VdcTreeRespDto> getChildVdc(Integer parentId, List<CloudVdcDo> vdcList, LoginUserVo loginUserVo) {
        List<VdcTreeRespDto> childList = new ArrayList<>();
        for (CloudVdcDo vdcDo : vdcList) {
            if (Objects.equals(parentId, vdcDo.getParentId())) {
                VdcTreeRespDto vecTree = formatVdcTree(vdcDo, vdcList, loginUserVo);
                childList.add(vecTree);
            }
        }
        return childList;
    }

    /**
     * 创建vdc树形结构实体
     *
     * @param vdcDo
     * @param vdcList
     * @param vdcInfo
     */
    private void createVdcTreeDto(CloudVdcDo vdcDo,
                                  List<CloudVdcDo> vdcList,
                                  VdcInfoRespDto vdcInfo, LoginUserVo loginUserVo) {


        vdcInfo.setVdcId(vdcDo.getId());
        vdcInfo.setVdcName(vdcDo.getVdcName());
        vdcInfo.setParentId(vdcDo.getParentId());
        vdcInfo.setParentName("---");
        vdcInfo.setOrgName("---");

        //根据vdcId,获取组织
        CloudOrganizationDo organizationDo = orgService.getOrgByVdcId(vdcDo.getId());
        if (Objects.nonNull(organizationDo)) {
            vdcInfo.setOrgName(organizationDo.getOrganizationName());
        }

        //获取可用区
        CloudZoneDo zoneDo = cloudZoneService.getById(vdcDo.getZoneId());
        vdcInfo.setZoneName(zoneDo.getName());
        //获取vdc网络资源
        vdcInfo.setNetworkNum(networkConfigService.getNetWorkCountByVdcId(vdcDo.getId()));


        //获取VDC资源使用情况
        VdcUsedResourceDto vdcResourceDto = getVdcResourceInfo(vdcDo.getId(), loginUserVo);

        vdcInfo.setTotalCpu(vdcResourceDto.getTotalCpu());
        vdcInfo.setUsedCpu(vdcResourceDto.getUsedCpu());
        vdcInfo.setTotalMem(vdcResourceDto.getTotalMem());
        vdcInfo.setUsedMem(vdcResourceDto.getUsedMem());
        vdcInfo.setMemUnit(vdcResourceDto.getMemUnit());
        vdcInfo.setTotalStorage(vdcResourceDto.getTotalStorage());
        vdcInfo.setUsedStorage(vdcResourceDto.getUsedStorage());
        vdcInfo.setStorageUnit(vdcResourceDto.getStorageUnit());

        CloudVdcDo parentDo =
                vdcList.stream().filter(item -> Objects.equals(item.getId(), vdcDo.getParentId())).findFirst().orElse(null);
        if (Objects.nonNull(parentDo)) {
            vdcInfo.setParentName(parentDo.getVdcName());
        }
    }


    /**
     * 获取vdc资源使用情况
     *
     * @param vdcId
     * @param loginUserVo
     * @return
     */
    @Override
    public VdcUsedResourceDto getVdcResourceInfo(Integer vdcId, LoginUserVo loginUserVo) {

        VdcUsedResourceDto vdcResourceDto = new VdcUsedResourceDto();


        //获取VDC本级用户已使用资源
        VdcSameLevelUserUsedResourceRespDto vdcSameLevelUserUseResource = createVdcSameUsedResource(vdcId, loginUserVo);

        //cpu
        vdcResourceDto.setTotalCpu(cloudVdcCpuService.totalCpuByVdcId(vdcId));
        vdcResourceDto.setAllocationCpu(cloudVdcCpuService.totalChildVdcCpuByParentIdAndArchitectureType(vdcId,
                null));
        vdcResourceDto.setSameUserUsedCpu(vdcSameLevelUserUseResource.getUsedCpu());
        vdcResourceDto.setUsedCpu(vdcResourceDto.getAllocationCpu() + vdcResourceDto.getSameUserUsedCpu());
        vdcResourceDto.setSurplusCpu(vdcResourceDto.getTotalCpu() - vdcResourceDto.getUsedCpu());
        //内存
        CloudVdcMemDo vdcTotalMemDo = cloudVdcMemService.totalMemByVdcId(vdcId);
        vdcResourceDto.setTotalMem(vdcTotalMemDo.getMem());
        vdcResourceDto.setMemUnit(vdcTotalMemDo.getMemUnit());
        CloudVdcMemDo allocationMem =
                cloudVdcMemService.totalChildVdcMemByParentIdAndArchitectureType(vdcId, null);
        vdcResourceDto.setAllocationMem(allocationMem.getMem());
        vdcResourceDto.setSameUserUsedMem(vdcSameLevelUserUseResource.getUsedMem());
        vdcResourceDto.setUsedMem(vdcResourceDto.getAllocationMem() + vdcResourceDto.getSameUserUsedMem());
        vdcResourceDto.setSurplusMem(vdcResourceDto.getTotalMem() - vdcResourceDto.getUsedMem());
        //存储
        CloudVdcStorageDo vdcTotalStorage = cloudVdcStorageService.getTotalStorageByVdcId(vdcId);
        vdcResourceDto.setTotalStorage(vdcTotalStorage.getStorage());
        vdcResourceDto.setStorageUnit(vdcTotalStorage.getUnit());
        CloudVdcStorageDo allocationStorage =
                cloudVdcStorageService.getChildVdcTotalStorage(vdcId);
        vdcResourceDto.setAllocationStorage(allocationStorage.getStorage());
        vdcResourceDto.setSameUserUsedStorage(vdcSameLevelUserUseResource.getUsedStorage());
        vdcResourceDto.setUsedStorage(vdcResourceDto.getSameUserUsedStorage() + vdcResourceDto.getAllocationStorage());
        vdcResourceDto.setSurplusStorage(vdcResourceDto.getTotalStorage() - vdcResourceDto.getUsedStorage());

        List<ArchitectureType> architectureTypeList = Arrays.asList(ArchitectureType.values());
        //vdc-本级用户使用架构资源
        List<VdcSameLevelUserUsedArchitectureResourceRespDto> vdcSameLevelUserUsedArchitectureResourceList =
                vdcSameLevelUserUseResource.getUserUsedArchitectureResourceList();
        //封装各个架构使用资源情况
        List<VdcArchitectureUsedResourceDto> vdcArchitectureUsedResourceList = new ArrayList<>();
        architectureTypeList.forEach(architectureType -> {
            //获取本级VDC用户直接使用该架构资源
            VdcSameLevelUserUsedArchitectureResourceRespDto sameUserUsedArchitectureResource =
                    Objects.isNull(vdcSameLevelUserUseResource) ? null :
                            vdcSameLevelUserUsedArchitectureResourceList.stream().
                                    filter(item -> Objects.equals(item.getArchitectureType(), architectureType)).findFirst().orElse(null);

            //VDC总CPU
            Integer totalCpu = cloudVdcCpuService.totalCpuByVdcIdAndArchitectureType(vdcId, architectureType);

            if (Objects.nonNull(totalCpu)) {
                VdcArchitectureUsedResourceDto vdcArchitectureResource = new VdcArchitectureUsedResourceDto();
                vdcArchitectureResource.setArchitectureType(architectureType);

                //已分配给下级CPU
                Integer childCpuTotal = cloudVdcCpuService.totalChildVdcCpuByParentIdAndArchitectureType(vdcId,
                        architectureType);
                //本级VDC用户直接使用CPU
                Integer sameVdcUserUsedCpu = Objects.nonNull(sameUserUsedArchitectureResource) ?
                        sameUserUsedArchitectureResource.getUsedVcpu() : 0;
                vdcArchitectureResource.setTotalCpu(totalCpu);
                vdcArchitectureResource.setAllocationCpu(childCpuTotal);
                vdcArchitectureResource.setSameUserUsedCpu(sameVdcUserUsedCpu);
                vdcArchitectureResource.setUsedCpu(childCpuTotal + sameVdcUserUsedCpu);
                vdcArchitectureResource.setSurplusCpu(totalCpu - childCpuTotal - sameVdcUserUsedCpu);

                //VDC总内存
                CloudVdcMemDo totalMem = cloudVdcMemService.totalMemByVdcIdAndArchitectureType(vdcId,
                        architectureType);
                //已分配给下级VDC存储
                CloudVdcMemDo childTotalMem =
                        cloudVdcMemService.totalChildVdcMemByParentIdAndArchitectureType(vdcId,
                                architectureType);
                //本级VDC用户直接使用mem
                Integer sameVdcUserUsedMem = Objects.nonNull(sameUserUsedArchitectureResource) ?
                        sameUserUsedArchitectureResource.getUsedMem() : 0;

                vdcArchitectureResource.setTotalMem(totalMem.getMem());
                vdcArchitectureResource.setMemUnit(totalMem.getMemUnit());
                vdcArchitectureResource.setAllocationMem(childTotalMem.getMem());
                vdcArchitectureResource.setSameUserUsedMem(sameVdcUserUsedMem);
                vdcArchitectureResource.setUsedMem(childTotalMem.getMem() + sameVdcUserUsedMem);
                vdcArchitectureResource.setSurplusMem(totalMem.getMem() - childTotalMem.getMem() - sameVdcUserUsedMem);
                vdcArchitectureUsedResourceList.add(vdcArchitectureResource);
            }

        });
        vdcResourceDto.setVdcArchitectureUsedResourceList(vdcArchitectureUsedResourceList);
        return vdcResourceDto;
    }

    @Override
    public VdcInfoRespDto vdcDetail(BaseVdcParam baseVdcParam, LoginUserVo loginUserVo) {
        CloudVdcDo vdcDo = cloudVdcService.getById(baseVdcParam.getVdcId());
        VdcInfoRespDto vdcInfo = new VdcInfoRespDto();
        vdcInfo.setVdcId(vdcDo.getId());
        vdcInfo.setVdcName(vdcDo.getVdcName());
        vdcInfo.setParentId(vdcDo.getParentId());
        vdcInfo.setCreateTime(DateUtils.format(vdcDo.getCreateTime(), DateUtils.DATE_ALL_PATTEN));
        vdcInfo.setOrgName("---");
        vdcInfo.setRemark(vdcDo.getRemark());
        vdcInfo.setParentName("--");
        if (!Objects.equals(vdcDo.getParentId(), KylinCloudManageConstants.TOP_PARENT_ID)) {
            CloudVdcDo parentVdc = cloudVdcService.getById(vdcDo.getParentId());
            vdcInfo.setParentName(parentVdc.getVdcName());
        }

        //根据vdcId,获取组织
        CloudOrganizationDo organizationDo = orgService.getOrgByVdcId(vdcDo.getId());
        if (Objects.nonNull(organizationDo)) {
            vdcInfo.setOrgName(organizationDo.getOrganizationName());
        }

        //获取可用区
        CloudZoneDo zoneDo = cloudZoneService.getById(vdcDo.getZoneId());
        vdcInfo.setZoneName(zoneDo.getName());
        //获取vdc网络资源
        vdcInfo.setNetworkNum(networkConfigService.getNetWorkCountByVdcId(vdcDo.getId()));

        //vdc资源使用情况
        VdcUsedResourceDto vdcResourceDto = getVdcResourceInfo(vdcDo.getId(), loginUserVo);

        //cpu 信息
        vdcInfo.setTotalCpu(vdcResourceDto.getTotalCpu());
        vdcInfo.setUsedCpu(vdcResourceDto.getUsedCpu());
        vdcInfo.setAllocationCpu(vdcResourceDto.getAllocationCpu());
        vdcInfo.setSameUserUsedCpu(vdcResourceDto.getSameUserUsedCpu());
        vdcInfo.setSurplusCpu(vdcResourceDto.getSurplusCpu());

        //内存信息
        vdcInfo.setTotalMem(vdcResourceDto.getTotalMem());
        vdcInfo.setUsedMem(vdcResourceDto.getUsedMem());
        vdcInfo.setAllocationMem(vdcResourceDto.getAllocationMem());
        vdcInfo.setSameUserUsedMem(vdcResourceDto.getSameUserUsedMem());
        vdcInfo.setMemUnit(vdcResourceDto.getMemUnit());
        vdcInfo.setSurplusMem(vdcResourceDto.getSurplusMem());

        //存储信息
        vdcInfo.setTotalStorage(vdcResourceDto.getTotalStorage());
        vdcInfo.setUsedStorage(vdcResourceDto.getUsedStorage());
        vdcInfo.setAllocationStorage(vdcResourceDto.getAllocationStorage());
        vdcInfo.setSameUserUsedStorage(vdcResourceDto.getSameUserUsedStorage());
        vdcInfo.setStorageUnit(vdcResourceDto.getStorageUnit());
        vdcInfo.setSurplusStorage(vdcResourceDto.getSurplusStorage());

        //网络信息
        vdcInfo.setNetworkList(networkMapper.networkListByVdcId(vdcDo.getId()));

        //各个架构的资源信息
        List<CommonVdcArchitectureResource> architectureUsedResourceList = new ArrayList<>();
        vdcResourceDto.getVdcArchitectureUsedResourceList().forEach(item -> {
            //CPU资源
            CommonVdcArchitectureResource cpuResource = new CommonVdcArchitectureResource();
            cpuResource.setArchitectureType(item.getArchitectureType());
            cpuResource.setResourceType(ArchitectureResourceType.CPU);
            cpuResource.setTotal(item.getTotalCpu());
            cpuResource.setUsed(item.getUsedCpu());
            cpuResource.setSameUserUsed(item.getSameUserUsedCpu());
            cpuResource.setAllocationChild(item.getAllocationCpu());
            cpuResource.setSurplus(item.getSurplusCpu());
            architectureUsedResourceList.add(cpuResource);

            //内存资源
            CommonVdcArchitectureResource memResource = new CommonVdcArchitectureResource();
            memResource.setArchitectureType(item.getArchitectureType());
            memResource.setResourceType(ArchitectureResourceType.MEM);
            memResource.setTotal(item.getTotalMem());
            memResource.setUsed(item.getUsedMem());
            memResource.setSameUserUsed(item.getSameUserUsedMem());
            memResource.setAllocationChild(item.getAllocationMem());
            memResource.setSurplus(item.getSurplusMem());
            architectureUsedResourceList.add(memResource);
        });
        vdcInfo.setArchitectureUsedResourceList(architectureUsedResourceList);
        return vdcInfo;
    }


    @Override
    public ModifyVdcDetailRespDto modifyVdcDetail(BaseVdcParam baseVdcParam, LoginUserVo loginUserVo) {
        CloudVdcDo vdcDo = cloudVdcService.getById(baseVdcParam.getVdcId());
        ModifyVdcDetailRespDto vdcDetail = new ModifyVdcDetailRespDto();
        vdcDetail.setVdcId(vdcDo.getId());
        vdcDetail.setVdcName(vdcDo.getVdcName());
        vdcDetail.setParentId(vdcDo.getParentId());
        if (!Objects.equals(vdcDo.getParentId(), KylinCloudManageConstants.TOP_PARENT_ID)) {
            vdcDetail.setParentName(cloudVdcService.getById(vdcDo.getParentId()).getVdcName());
        }
        CloudZoneDo zoneDo = cloudZoneService.getById(vdcDo.getZoneId());
        vdcDetail.setZoneId(vdcDo.getZoneId());
        vdcDetail.setZoneName(zoneDo.getName());
        //网络设置
        vdcDetail.setNetworkList(networkMapper.networkListByVdcId(vdcDo.getId()));

        return vdcDetail;
    }


    @Override
    @Transactional
    public void modifyVdc(ModifyVdcParam modifyVdcParam, LoginUserVo loginUserVo) {
        CloudVdcDo vdcDo = cloudVdcService.getById(modifyVdcParam.getVdcId());
        Date now = new Date();
        if (!Objects.equals(modifyVdcParam.getVdcName(), vdcDo.getVdcName())) {
            checkIfExistVdcName(modifyVdcParam.getVdcName());
            vdcDo.setVdcName(modifyVdcParam.getVdcName());
            vdcDo.setUpdateBy(loginUserVo.getUserId());
            vdcDo.setUpdateTime(now);
            cloudVdcService.updateById(vdcDo);
        }

//        //获取已分配的存储
//        CloudVdcStorageDo queryStorageDo = new CloudVdcStorageDo();
//        queryStorageDo.setDeleteFlag(false);
//        queryStorageDo.setVdcId(vdcDo.getId());
//        QueryWrapper<CloudVdcStorageDo> storageWrapper = new QueryWrapper<>(queryStorageDo);
//        List<CloudVdcStorageDo> vdcStorageList = cloudVdcStorageService.getBaseMapper().selectList(storageWrapper);
//        if (!vdcStorageList.isEmpty()) {
//            CloudVdcStorageDo updateStorageDo = vdcStorageList.get(0);
//            updateStorageDo.setStorage(modifyVdcParam.getAllocationStorage());
//            updateStorageDo.setUpdateBy(loginUserVo.getUserId());
//            updateStorageDo.setUpdateTime(now);
//            cloudVdcStorageService.updateById(updateStorageDo);
//        } else {
//            CloudVdcStorageDo insertStorageDo = new CloudVdcStorageDo();
//            insertStorageDo.setVdcId(vdcDo.getId());
//            insertStorageDo.setStorage(modifyVdcParam.getAllocationStorage());
//            insertStorageDo.setUnit(modifyVdcParam.getStorageUnit());
//            insertStorageDo.setCreateBy(loginUserVo.getUserId());
//            insertStorageDo.setCreateTime(now);
//            cloudVdcStorageService.save(insertStorageDo);
//        }
//
//        //插入架构资源
//        List<CloudVdcCpuDo> vdcCpuDoList = cloudVdcCpuService.listVDdcCpuByVdc(vdcDo.getId());
//        List<CloudVdcMemDo> vdcMemDoList = cloudVdcMemService.listVdcMemByVdcId(vdcDo.getId());
//
//        List<CloudVdcCpuDo> updateCpuDoList = new ArrayList<>();
//        List<CloudVdcCpuDo> insertCpuDoList = new ArrayList<>();
//
//        List<CloudVdcMemDo> updateMemDoList = new ArrayList<>();
//        List<CloudVdcMemDo> insertMemDoList = new ArrayList<>();
//
//        modifyVdcParam.getArchitectureResourceList().forEach(item -> {
//
//
//            CloudVdcCpuDo cpuDo =
//                    vdcCpuDoList.stream().filter(cpu -> Objects.equals(cpu.getArchitecture(),
//                            item.getArchitectureType())).findFirst().orElse(null);
//
//            if (Objects.nonNull(cpuDo)) {
//                cpuDo.setVcpus(item.getAllocationCpu());
//                cpuDo.setUpdateBy(loginUserVo.getUserId());
//                cpuDo.setUpdateTime(now);
//                updateCpuDoList.add(cpuDo);
//            } else {
//                CloudVdcCpuDo vdcCpuDo = new CloudVdcCpuDo();
//                vdcCpuDo.setVdcId(vdcDo.getId());
//                vdcCpuDo.setArchitecture(item.getArchitectureType());
//                vdcCpuDo.setVcpus(item.getAllocationCpu());
//                vdcCpuDo.setCreateBy(loginUserVo.getUserId());
//                vdcCpuDo.setCreateTime(now);
//                insertCpuDoList.add(vdcCpuDo);
//            }
//
//
//            CloudVdcMemDo memDo =
//                    vdcMemDoList.stream().filter(cpu -> Objects.equals(cpu.getArchitecture(),
//                            item.getArchitectureType())).findFirst().orElse(null);
//
//
//            if (Objects.nonNull(memDo)) {
//                memDo.setMem(item.getAllocationMem());
//                memDo.setUpdateBy(loginUserVo.getUserId());
//                memDo.setUpdateTime(now);
//                updateMemDoList.add(memDo);
//            } else {
//                CloudVdcMemDo vdcMemDo = new CloudVdcMemDo();
//                vdcMemDo.setVdcId(vdcDo.getId());
//                vdcMemDo.setArchitecture(item.getArchitectureType());
//                vdcMemDo.setMem(item.getAllocationMem());
//                vdcMemDo.setMemUnit(item.getMemUnit());
//                vdcMemDo.setCreateBy(loginUserVo.getUserId());
//                vdcMemDo.setCreateTime(now);
//                insertMemDoList.add(vdcMemDo);
//            }
//        });
//
//        if (!updateCpuDoList.isEmpty()) {
//            cloudVdcCpuService.updateBatchById(updateCpuDoList);
//        }
//
//        if (!insertCpuDoList.isEmpty()) {
//            cloudVdcCpuService.saveBatch(insertCpuDoList);
//        }
//        if (!updateMemDoList.isEmpty()) {
//            cloudVdcMemService.updateBatchById(updateMemDoList);
//        }
//        if (!insertMemDoList.isEmpty()) {
//            cloudVdcMemService.saveBatch(insertMemDoList);
//        }

        //网络处理
        modifyVdcNetwork(vdcDo.getId(), modifyVdcParam, loginUserVo);

    }

    /**
     * 编辑vdc网络
     *
     * @param vdcId
     * @param modifyVdcParam
     */
    private void modifyVdcNetwork(Integer vdcId, ModifyVdcParam modifyVdcParam, LoginUserVo loginUserVo) {


        List<CloudNetworkConfigDo> networkListDo = cloudNetworkConfigService.listNetworkListByVdcId(vdcId);
        List<CloudNetworkConfigDo> insertNetworkList = new ArrayList<>();
        List<CloudNetworkConfigDo> updateNetworkList = new ArrayList<>();

        modifyVdcParam.getNetworkList().forEach(network -> {
            CloudNetworkConfigDo networkConfigDo =
                    networkListDo.stream().filter(item -> Objects.equals(item.getId(), network.getNetworkId())).findFirst().orElse(null);
            if (Objects.isNull(networkConfigDo)) {
                CloudNetworkConfigDo cloudNetworkConfigDo = new CloudNetworkConfigDo();
                BeanUtils.copyProperties(network, cloudNetworkConfigDo);
                cloudNetworkConfigDo.setInterfaceType(KylinCloudManageConstants.BRIDGE);
                cloudNetworkConfigDo.setCreateBy(loginUserVo.getUserId());
                cloudNetworkConfigDo.setVdcId(vdcId);
                cloudNetworkConfigDo.setCreateTime(new Date());
                insertNetworkList.add(cloudNetworkConfigDo);
            } else {
                switch (network.getModifyType()) {
                    case NONE:
                    case MODIFY:
                        BeanUtils.copyProperties(network, networkConfigDo);
                        networkConfigDo.setInterfaceType(KylinCloudManageConstants.BRIDGE);
                        networkConfigDo.setUpdateBy(loginUserVo.getUserId());
                        networkConfigDo.setUpdateTime(new Date());
                        updateNetworkList.add(networkConfigDo);
                        break;
                    case DELETE:
                        networkConfigDo.setDeleteFlag(true);
                        networkConfigDo.setUpdateBy(loginUserVo.getUserId());
                        networkConfigDo.setUpdateTime(new Date());
                        networkConfigDo.setDeleteBy(loginUserVo.getUserId());
                        networkConfigDo.setDeleteTime(new Date());
                        updateNetworkList.add(networkConfigDo);
                        break;
                }
            }
        });
        if (!insertNetworkList.isEmpty()) {
            cloudNetworkConfigService.saveBatch(insertNetworkList);
        }
        if (!updateNetworkList.isEmpty()) {
            cloudNetworkConfigService.updateBatchById(updateNetworkList);
        }
    }


    @Override
    public void deleteVdc(BaseVdcParam baseVdcParam, LoginUserVo loginUserVo) {
        //删除VDC条件：
        //1：无下级vdc
        boolean hasChildVdc = checkExistChild(baseVdcParam.getVdcId());
        if (hasChildVdc) {
            throw new KylinException(KylinHttpResponseVdcConstants.EXIST_CHILD_VDC);
        }
        //2:无组织和当前VDC绑定
        boolean bindVdc = checkOrgBindVdc(baseVdcParam.getVdcId());
        if (bindVdc) {
            throw new KylinException(KylinHttpResponseVdcConstants.VDC_BIND_ORG);
        }

        CloudVdcDo vdcDo = cloudVdcService.getById(baseVdcParam.getVdcId());
        vdcDo.setDeleteFlag(true);
        vdcDo.setDeleteBy(loginUserVo.getUserId());
        vdcDo.setDeleteTime(new Date());
        cloudVdcService.updateById(vdcDo);
        //删除资源
        cloudNetworkConfigService.deleteByVdcId(baseVdcParam.getVdcId(), loginUserVo.getUserId());
        cloudVdcCpuService.deleteByVdcId(baseVdcParam.getVdcId(), loginUserVo.getUserId());
        cloudVdcMemService.deleteByVdcId(baseVdcParam.getVdcId(), loginUserVo.getUserId());
        cloudVdcStorageService.deleteByVdcId(baseVdcParam.getVdcId(), loginUserVo.getUserId());

    }

    /**
     * 判断是否存在下级vdc
     *
     * @param vdcId
     * @return
     */
    private boolean checkExistChild(Integer vdcId) {
        CloudVdcDo queryChildDo = new CloudVdcDo();
        queryChildDo.setDeleteFlag(false);
        queryChildDo.setParentId(vdcId);
        QueryWrapper<CloudVdcDo> wrapper = new QueryWrapper<>(queryChildDo);
        List<CloudVdcDo> childList = cloudVdcService.getBaseMapper().selectList(wrapper);
        return !childList.isEmpty();
    }


    /**
     * 检查是否有组织绑定该vdc
     *
     * @param vdcId
     * @return
     */
    private boolean checkOrgBindVdc(Integer vdcId) {
        CloudOrgVdcDo queryBindDo = new CloudOrgVdcDo();
        queryBindDo.setDeleteFlag(false);
        queryBindDo.setVdcId(vdcId);
        QueryWrapper<CloudOrgVdcDo> wrapper = new QueryWrapper<>(queryBindDo);
        List<CloudOrgVdcDo> bindList = cloudOrgVdcService.getBaseMapper().selectList(wrapper);
        return !bindList.isEmpty();
    }


    @Override
    public CloudVdcDo getUserOrgBindVdc(Integer userId) {
        CloudOrganizationDo loginUserOrgDo = orgService.getByUserId(userId);
        CloudOrgVdcDo orgVdcDo = new CloudOrgVdcDo();
        orgVdcDo.setOrgId(loginUserOrgDo.getId());
        orgVdcDo.setDeleteFlag(false);
        Wrapper<CloudOrgVdcDo> orgVdcDoWrapper =
                new QueryWrapper<>(orgVdcDo);
        List<CloudOrgVdcDo> orgVdcList = cloudOrgVdcService.getBaseMapper().selectList(orgVdcDoWrapper);
        return orgVdcList.isEmpty() ? null : cloudVdcService.getById(orgVdcList.get(0).getVdcId());
    }

    @Override
    public CloudVdcDo getVdcByOrgId(Integer orgId) {

        return cloudVdcService.getVdcByOrgId(orgId);
    }

    @Override
    public List<CloudVdcDo> visibleVdcListByUserId(Integer userId) {
        boolean ifPlatformUser = userService.judgeIfPlatformUser(userId);
        if (ifPlatformUser) {
            return cloudVdcService.list();
        }
        //如果不是则 查询登录用户对应组织拥有的VDC
        CloudOrganizationDo userOrg = orgService.getByUserId(userId);
        CloudVdcDo loginUserOrgVdc = getVdcByOrgId(userOrg.getId());
        return cloudVdcService.getAllChildVdcList(loginUserOrgVdc.getId());
    }


    @Override
    public List<CloudVdcDo> visibleVdcListByUserIdAndZoneId(Integer userId, Integer zoneId) {
        boolean ifPlatformUser = userService.judgeIfPlatformUser(userId);
        if (ifPlatformUser) {
            return cloudVdcService.vdcListByZone(zoneId);
        }
        //如果不是则 查询登录用户对应组织拥有的VDC
        CloudOrganizationDo userOrg = orgService.getByUserId(userId);
        CloudVdcDo loginUserOrgVdc = getVdcByOrgId(userOrg.getId());
        return cloudVdcService.getAllChildVdcList(loginUserOrgVdc.getId());
    }

    @Override
    public VdcModifyResourceRespDto modifyVdcResourceDetail(Integer vdcId, LoginUserVo loginUserVo) {

        CloudVdcDo vdcDo = cloudVdcService.getById(vdcId);
        if (Objects.equals(vdcDo.getParentId(), KylinCommonConstants.TOP_PARENT_ID)) {
            //可用区资源
            VdcResourceRespDto zoneClusterResource = createZoneResource(vdcDo.getZoneId(), loginUserVo);
            VdcResourceRespDto firstVdcResource = createVdcResource(vdcDo.getId(), loginUserVo);
            return createFirstVdcModifyResource(vdcDo.getId(), zoneClusterResource, firstVdcResource);
        } else {
            //上级VDC资源
            VdcResourceRespDto parentVdcResource = createVdcResource(vdcDo.getParentId(), loginUserVo);
            //本级VDC资源
            VdcResourceRespDto sameVdcResource = createVdcResource(vdcDo.getId(), loginUserVo);

            return createNotFirstVdcModifyResource(vdcDo.getId(), parentVdcResource, sameVdcResource, loginUserVo);

        }

    }

    /**
     * 创建一级VDC -资源变更申请详情
     *
     * @param vdcId
     * @param zoneClusterResource
     * @param firstVdcResource
     * @return
     */
    private VdcModifyResourceRespDto createFirstVdcModifyResource(Integer vdcId,
                                                                  VdcResourceRespDto zoneClusterResource,
                                                                  VdcResourceRespDto firstVdcResource) {
        VdcModifyResourceRespDto firstVdcModifyResource = new VdcModifyResourceRespDto();
        //获取当前VDC存储
        CloudVdcStorageDo vdcTotalStorage = cloudVdcStorageService.getTotalStorageByVdcId(vdcId);
        firstVdcModifyResource.setCurrentStorage(vdcTotalStorage.getStorage());
        firstVdcModifyResource.setStorageUnit(vdcTotalStorage.getUnit());
        firstVdcModifyResource.setUsedStorage(firstVdcResource.getUsedStorage());
        firstVdcModifyResource.setParentUsableStorage(zoneClusterResource.getUsableStorage());

        List<VdcModifyArchitectureResourceRespDto> architectureResourceList = new ArrayList<>();
        List<VdcArchitectureResourceRespDto> zoneClusterArchitectureResourceList =
                zoneClusterResource.getArchitectureResourceList();
        List<VdcArchitectureResourceRespDto> firstVdcArchitectureResourceList =
                firstVdcResource.getArchitectureResourceList();
        //遍历架构获取架构资源
        List<ArchitectureType> architectureTypeList = Arrays.asList(ArchitectureType.values());
        architectureTypeList.forEach(architectureType -> {
            VdcArchitectureResourceRespDto zoneClusterArchitectureResource =
                    zoneClusterArchitectureResourceList.stream().filter(item -> Objects.equals(item.getArchitectureType(), architectureType))
                            .findFirst().orElse(null);

            VdcArchitectureResourceRespDto firstArchitectureResource =
                    firstVdcArchitectureResourceList.stream().filter(item -> Objects.equals(item.getArchitectureType(), architectureType))
                            .findFirst().orElse(null);

            //当前VDC cpu值
            Integer currentCpu = cloudVdcCpuService.totalCpuByVdcIdAndArchitectureType(vdcId,
                    architectureType);

            //当前VDC内存值
            CloudVdcMemDo currentMem = cloudVdcMemService.totalMemByVdcIdAndArchitectureType(vdcId,
                    architectureType);

            VdcModifyArchitectureResourceRespDto firstVdcArchitectureResource =
                    new VdcModifyArchitectureResourceRespDto();

            boolean addFlag = false;
            if (Objects.nonNull(currentCpu) & Objects.nonNull(currentMem)) {
                firstVdcArchitectureResource.setCurrentVcpu(currentCpu);
                firstVdcArchitectureResource.setCurrentMem(currentMem.getMem());
                firstVdcArchitectureResource.setMemUnit(currentMem.getMemUnit());
                firstVdcArchitectureResource.setArchitectureType(architectureType);
                if (Objects.nonNull(firstArchitectureResource)) {
                    firstVdcArchitectureResource.setUsedCpu(firstArchitectureResource.getUsedCpu());
                    firstVdcArchitectureResource.setUsedMem(firstArchitectureResource.getUsedMem());
                }
                addFlag = true;
            }

            if (Objects.nonNull(zoneClusterArchitectureResource)) {
                firstVdcArchitectureResource.setParentUsableMem(zoneClusterArchitectureResource.getUsableMem());
                firstVdcArchitectureResource.setParentUsableCpu(zoneClusterArchitectureResource.getUsableVcpu());
                addFlag = true;
            }
            if (addFlag) {
                architectureResourceList.add(firstVdcArchitectureResource);
                firstVdcArchitectureResource.setArchitectureType(architectureType);
            }

        });
        firstVdcModifyResource.setArchitectureResourceList(architectureResourceList);
        return firstVdcModifyResource;
    }


    /**
     * 创建非一级VDC -资源变更申请详情
     *
     * @param parentVdcResource
     * @param sameVdcResource
     * @return
     */
    private VdcModifyResourceRespDto createNotFirstVdcModifyResource(Integer vdcId,
                                                                     VdcResourceRespDto parentVdcResource,
                                                                     VdcResourceRespDto sameVdcResource,
                                                                     LoginUserVo loginUserVo) {
        //获取当前VDC存储
        CloudVdcStorageDo vdcTotalStorage = cloudVdcStorageService.getTotalStorageByVdcId(vdcId);

        VdcModifyResourceRespDto vdcModifyResource = new VdcModifyResourceRespDto();
        vdcModifyResource.setCurrentStorage(vdcTotalStorage.getStorage());
        vdcModifyResource.setStorageUnit(vdcTotalStorage.getUnit());
        vdcModifyResource.setParentUsableStorage(parentVdcResource.getUsableStorage() + vdcTotalStorage.getStorage());
        vdcModifyResource.setUsedStorage(sameVdcResource.getUsedStorage());


        List<VdcModifyArchitectureResourceRespDto> architectureResourceList = new ArrayList<>();
        List<VdcArchitectureResourceRespDto> parentArchitectureResourceList =
                parentVdcResource.getArchitectureResourceList();
        List<VdcArchitectureResourceRespDto> sameVdcArchitectureResourceList =
                sameVdcResource.getArchitectureResourceList();
        //遍历架构获取架构资源
        List<ArchitectureType> architectureTypeList = Arrays.asList(ArchitectureType.values());
        architectureTypeList.forEach(architectureType -> {
            //当前VDC cpu值
            Integer currentCpu = cloudVdcCpuService.totalCpuByVdcIdAndArchitectureType(vdcId,
                    architectureType);

            //当前VDC内存值
            CloudVdcMemDo currentMem = cloudVdcMemService.totalMemByVdcIdAndArchitectureType(vdcId,
                    architectureType);
            if (Objects.nonNull(currentCpu) && Objects.nonNull(currentMem)) {
                VdcModifyArchitectureResourceRespDto architectureResource = new VdcModifyArchitectureResourceRespDto();
                architectureResource.setCurrentVcpu(currentCpu);
                architectureResource.setCurrentMem(currentMem.getMem());
                architectureResource.setMemUnit(currentMem.getMemUnit());
                architectureResource.setArchitectureType(architectureType);
                VdcArchitectureResourceRespDto parentArchitectureResource =
                        parentArchitectureResourceList.stream().filter(item -> Objects.equals(item.getArchitectureType(), architectureType))
                                .findFirst().orElse(null);

                if (Objects.nonNull(parentArchitectureResource)) {
                    architectureResource.setParentUsableCpu(parentArchitectureResource.getUsableVcpu() + currentCpu);
                    architectureResource.setParentUsableMem(parentArchitectureResource.getUsableMem() + currentMem.getMem());
                }
                VdcArchitectureResourceRespDto sameVdcArchitectureResource =
                        sameVdcArchitectureResourceList.stream().filter(item -> Objects.equals(item.getArchitectureType(), architectureType))
                                .findFirst().orElse(null);
                if (Objects.nonNull(sameVdcArchitectureResource)) {
                    architectureResource.setUsedCpu(sameVdcArchitectureResource.getUsedCpu());
                    architectureResource.setUsedMem(sameVdcArchitectureResource.getUsedMem());
                }
                architectureResourceList.add(architectureResource);
            }
        });
        vdcModifyResource.setArchitectureResourceList(architectureResourceList);
        //获取当前用户是否是修改申请修改下级VDC
        boolean ifPlatformUser = userService.judgeIfPlatformUser(loginUserVo.getUserId());
        if (ifPlatformUser) {
            vdcModifyResource.setModifyChild(true);
        } else {
            //vdc-对应的组织
            CloudOrganizationDo vdcBindOrg = orgService.getOrgByVdcId(vdcId);
            if (Objects.isNull(vdcBindOrg)) {
                //Vdc  还未绑定组织，则说明来变更改VDC只能是上级用户
                vdcModifyResource.setModifyChild(false);
            } else {
                CloudOrganizationDo loginUserOrg = orgService.getByUserId(loginUserVo.getUserId());
                //如果登录用户对应的组织和vdc对应的组织是同一个，则工单是待审核，
                if (Objects.equals(vdcBindOrg.getId(), loginUserOrg.getId())) {
                    vdcModifyResource.setModifyChild(false);
                } else {
                    vdcModifyResource.setModifyChild(true);
                }
            }
        }
        return vdcModifyResource;
    }


    /**
     * 申请VDC-资源 工单
     * 登录用户如果是该VDC上级用户，工单则直接通过
     * 如果不是上级用户，则工单需要在审核
     *
     * @param applyModifyVdcResourceParam
     * @param loginUserVo
     */
    @Override
    @Transactional
    public BaseResult<String> applyModifyVdcResource(ApplyModifyVdcResourceParam applyModifyVdcResourceParam,
                                                     LoginUserVo loginUserVo) {

        Integer vdcId = applyModifyVdcResourceParam.getVdcId();

        CloudVdcDo vdcDo = cloudVdcService.getById(vdcId);

        WorkOrderStatus workOrderStatus = WorkOrderStatus.WAIT_CHECK;

        boolean ifPlatformUser = userService.judgeIfPlatformUser(loginUserVo.getUserId());
        if (ifPlatformUser) {
            workOrderStatus = WorkOrderStatus.CHECK_PASS;
        } else {
            //vdc-对应的组织
            CloudOrganizationDo vdcBindOrg = orgService.getOrgByVdcId(vdcId);
            if (Objects.isNull(vdcBindOrg)) {
                //Vdc  还未绑定组织，则说明来变更改VDC只能是上级用户
                workOrderStatus = WorkOrderStatus.CHECK_PASS;
            } else {
                CloudOrganizationDo loginUserOrg = orgService.getByUserId(loginUserVo.getUserId());
                //如果登录用户对应的组织和vdc对应的组织是同一个，则工单是待审核，
                if (Objects.equals(vdcBindOrg.getId(), loginUserOrg.getId())) {
                    workOrderStatus = WorkOrderStatus.WAIT_CHECK;
                } else {
                    workOrderStatus = WorkOrderStatus.CHECK_PASS;
                }
            }
        }


        Date now = new Date();
        CloudWorkOrderDo workOrderDo = new CloudWorkOrderDo();
        workOrderDo.setType(WorkOrderType.MODIFY_VDC);
        workOrderDo.setUserId(loginUserVo.getUserId());
        workOrderDo.setStatus(workOrderStatus);
        workOrderDo.setTarget(vdcDo.getVdcName());
        workOrderDo.setApplyReason(applyModifyVdcResourceParam.getApplyReason());
        if (Objects.equals(workOrderStatus, WorkOrderStatus.CHECK_PASS)) {
            workOrderDo.setAuditBy(loginUserVo.getUserId());
            workOrderDo.setAuditTime(now);
            workOrderDo.setAuditOpinion("auto pass");
        }
        formatWorkOrderCommon(workOrderDo, loginUserVo, workOrderStatus);
        cloudWorkOrderService.save(workOrderDo);

        //插入VDC工单详情
        insertWorkOrderVdcDetail(applyModifyVdcResourceParam, workOrderDo.getId(), loginUserVo, workOrderStatus);

        //插入VDC工单-架构资源详情
        insertWorkOrderVdcCpuMem(applyModifyVdcResourceParam, workOrderDo.getId(), loginUserVo, workOrderStatus);

        if (Objects.equals(workOrderStatus, WorkOrderStatus.CHECK_PASS)) {
            return BaseResult.customSuccess(KylinHttpResponseVdcConstants.APPLY_MODIFY_VDC_CHECK_PASS, null);
        }
        return BaseResult.customSuccess(KylinHttpResponseVdcConstants.APPLY_MODIFY_VDC_CHECK_WAIT, null);
    }

    /**
     * 插入VDC工单详情
     *
     * @param applyModifyVdcResourceParam
     * @param workOrderId
     * @param loginUserVo
     * @param workOrderStatus
     */
    private void insertWorkOrderVdcDetail(ApplyModifyVdcResourceParam applyModifyVdcResourceParam,
                                          Integer workOrderId, LoginUserVo loginUserVo,
                                          WorkOrderStatus workOrderStatus) {
        //工单-VDC详情表
        Integer vdcId = applyModifyVdcResourceParam.getVdcId();

        CloudWorkOrderVdcDo cloudWorkOrderVdcDo = new CloudWorkOrderVdcDo();
        cloudWorkOrderVdcDo.setWorkOrderId(workOrderId);
        cloudWorkOrderVdcDo.setVdcId(vdcId);
        formatWorkOrderCommon(cloudWorkOrderVdcDo, loginUserVo, workOrderStatus);

        cloudWorkOrderVdcDo.setApplyStorage(applyModifyVdcResourceParam.getApplyStorage());
        cloudWorkOrderVdcDo.setStorageUnit(applyModifyVdcResourceParam.getStorageUnit());
        CloudVdcStorageDo cloudVdcStorageDo =
                cloudVdcStorageService.getByVdcId(vdcId);
        cloudWorkOrderVdcDo.setOldStorage(cloudVdcStorageDo.getStorage());
        cloudWorkOrderVdcDo.setRealStorage(0);

        if (Objects.equals(workOrderStatus, WorkOrderStatus.CHECK_PASS)) {
            cloudWorkOrderVdcDo.setRealStorage(applyModifyVdcResourceParam.getApplyStorage());
            //如果工单是直接通过状态，则删除VDC-原来的存储资源，删除新的存储资源
            cloudVdcStorageDo.setUpdateBy(loginUserVo.getUserId());
            cloudVdcStorageDo.setUpdateTime(new Date());
            cloudVdcStorageDo.setStorage(applyModifyVdcResourceParam.getApplyStorage());
            cloudVdcStorageService.updateById(cloudVdcStorageDo);
        }
        cloudWorkOrderVdcService.save(cloudWorkOrderVdcDo);
    }

    /**
     * 插入VDC资源申请-架构资源详情
     *
     * @param applyModifyVdcResourceParam
     * @param workOrderId
     * @param loginUserVo
     * @param workOrderStatus
     */
    private void insertWorkOrderVdcCpuMem(ApplyModifyVdcResourceParam applyModifyVdcResourceParam,
                                          Integer workOrderId, LoginUserVo loginUserVo,
                                          WorkOrderStatus workOrderStatus) {

        Integer vdcId = applyModifyVdcResourceParam.getVdcId();

        List<CloudVdcCpuDo> oldVdcCpuList = cloudVdcCpuService.listVDdcCpuByVdc(vdcId);
        List<CloudVdcMemDo> oldVdcMemList = cloudVdcMemService.listVdcMemByVdcId(vdcId);


        //更新vdc的cpu和资源信息
        List<CloudVdcCpuDo> batchUpdateVdcCpuList = new ArrayList<>();
        List<CloudVdcMemDo> batchUpdateVdcMemList = new ArrayList<>();

        //批量新插入的
        List<CloudVdcCpuDo> batchNewInsertVdcCpuList = new ArrayList<>();
        List<CloudVdcMemDo> batchNewInsertVdcMemList = new ArrayList<>();

        List<CloudWorkOrderVdcCpuMemDo> workOrderVdcCpuMemDoList = new ArrayList<>();
        List<ApplyModifyArchitectureResourceParam> applyArchitectureList =
                applyModifyVdcResourceParam.getArchitectureResourceList();
        applyArchitectureList.forEach(architectureResource -> {

            //cpu
            CloudWorkOrderVdcCpuMemDo cpuDo = new CloudWorkOrderVdcCpuMemDo();
            formatWorkOrderCommon(cpuDo, loginUserVo, workOrderStatus);
            cpuDo.setWorkOrderId(workOrderId);
            cpuDo.setArchitecture(architectureResource.getArchitectureType());
            cpuDo.setResourceType(ArchitectureResourceType.CPU);
            cpuDo.setUnit(MemUnit.GB);
            cpuDo.setApplySize(architectureResource.getApplyCpu());
            cpuDo.setOldSize(0);
            CloudVdcCpuDo oldVdcCpuDo = oldVdcCpuList.stream().filter(item -> Objects.equals(item.getArchitecture(),
                    architectureResource.getArchitectureType()))
                    .findFirst().orElse(null);
            if (Objects.nonNull(oldVdcCpuDo)) {
                cpuDo.setOldSize(oldVdcCpuDo.getVcpus());
            } else {
                //变更前vdc该架构的车贷cpu资源不存在，则新增
                CloudVdcCpuDo newInsertVdcCpuDo = new CloudVdcCpuDo();
                newInsertVdcCpuDo.setVdcId(vdcId);
                newInsertVdcCpuDo.setVcpus(architectureResource.getApplyCpu());
                newInsertVdcCpuDo.setArchitecture(architectureResource.getArchitectureType());
                newInsertVdcCpuDo.setCreateBy(loginUserVo.getUserId());
                newInsertVdcCpuDo.setCreateTime(new Date());
                batchNewInsertVdcCpuList.add(newInsertVdcCpuDo);
            }

            if (Objects.equals(workOrderStatus, WorkOrderStatus.CHECK_PASS)) {
                cpuDo.setRealSize(architectureResource.getApplyCpu());

                if (Objects.nonNull(oldVdcCpuDo)) {
                    oldVdcCpuDo.setVcpus(architectureResource.getApplyCpu());
                    oldVdcCpuDo.setUpdateBy(loginUserVo.getUserId());
                    oldVdcCpuDo.setUpdateTime(new Date());
                    batchUpdateVdcCpuList.add(oldVdcCpuDo);
                }
            }
            workOrderVdcCpuMemDoList.add(cpuDo);


            //内存信息
            CloudWorkOrderVdcCpuMemDo memDo = new CloudWorkOrderVdcCpuMemDo();
            formatWorkOrderCommon(memDo, loginUserVo, workOrderStatus);
            memDo.setWorkOrderId(workOrderId);
            memDo.setArchitecture(architectureResource.getArchitectureType());
            memDo.setResourceType(ArchitectureResourceType.MEM);
            memDo.setApplySize(architectureResource.getApplyMem());
            memDo.setUnit(MemUnit.GB);
            memDo.setApplySize(architectureResource.getApplyMem());
            memDo.setOldSize(0);
            CloudVdcMemDo oldVdcMemDo = oldVdcMemList.stream().filter(item -> Objects.equals(item.getArchitecture(),
                    architectureResource.getArchitectureType()))
                    .findFirst().orElse(null);
            if (Objects.nonNull(oldVdcMemDo)) {
                memDo.setOldSize(oldVdcMemDo.getMem());
            } else {
                //变更前vdc该架构的车贷cpu资源不存在，则新增
                CloudVdcMemDo newInsertVdcMemDo = new CloudVdcMemDo();
                newInsertVdcMemDo.setVdcId(vdcId);
                newInsertVdcMemDo.setMem(architectureResource.getApplyMem());
                newInsertVdcMemDo.setMemUnit(MemUnit.GB);
                newInsertVdcMemDo.setArchitecture(architectureResource.getArchitectureType());
                newInsertVdcMemDo.setCreateBy(loginUserVo.getUserId());
                newInsertVdcMemDo.setCreateTime(new Date());
                batchNewInsertVdcMemList.add(newInsertVdcMemDo);
            }
            if (Objects.equals(workOrderStatus, WorkOrderStatus.CHECK_PASS)) {
                memDo.setRealSize(architectureResource.getApplyMem());

                if (Objects.nonNull(oldVdcCpuDo)) {
                    oldVdcMemDo.setMem(architectureResource.getApplyMem());
                    oldVdcMemDo.setUpdateBy(loginUserVo.getUserId());
                    oldVdcMemDo.setUpdateTime(new Date());
                    batchUpdateVdcMemList.add(oldVdcMemDo);
                }
            }
            workOrderVdcCpuMemDoList.add(memDo);
        });

        if (!workOrderVdcCpuMemDoList.isEmpty()) {
            cloudWorkOrderVdcCpuMemService.saveBatch(workOrderVdcCpuMemDoList);
        }

        if (Objects.equals(workOrderStatus, WorkOrderStatus.CHECK_PASS)) {
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
    }

    /**
     * 封装申请VDC工单相同部分
     *
     * @param baseDo
     * @param loginUserVo
     * @param workOrderStatus
     */
    private void formatWorkOrderCommon(BaseDo baseDo, LoginUserVo loginUserVo, WorkOrderStatus workOrderStatus) {
        Date now = new Date();
        baseDo.setCreateTime(now);
        baseDo.setCreateBy(loginUserVo.getUserId());
        if (Objects.equals(workOrderStatus, WorkOrderStatus.CHECK_PASS)) {
            baseDo.setUpdateBy(loginUserVo.getUserId());
            baseDo.setUpdateTime(now);
        }
    }
}
