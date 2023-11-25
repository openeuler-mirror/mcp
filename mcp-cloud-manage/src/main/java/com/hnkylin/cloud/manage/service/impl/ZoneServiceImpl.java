package com.hnkylin.cloud.manage.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.hnkylin.cloud.core.common.DateUtils;
import com.hnkylin.cloud.core.common.MCResponseData;
import com.hnkylin.cloud.core.common.MCServerVmConstants;
import com.hnkylin.cloud.core.common.PageData;
import com.hnkylin.cloud.core.config.exception.KylinException;
import com.hnkylin.cloud.core.domain.*;
import com.hnkylin.cloud.core.service.*;
import com.hnkylin.cloud.manage.config.MCConfigProperties;
import com.hnkylin.cloud.manage.constant.KylinHttpResponseClusterConstants;
import com.hnkylin.cloud.manage.constant.KylinHttpResponseZoneConstants;
import com.hnkylin.cloud.manage.entity.LoginUserVo;
import com.hnkylin.cloud.manage.entity.mc.resp.McClusterBaseResource;
import com.hnkylin.cloud.manage.entity.mc.resp.McClusterServerResourceResp;
import com.hnkylin.cloud.manage.entity.req.zone.BaseZoneParam;
import com.hnkylin.cloud.manage.entity.req.zone.CreateZoneParam;
import com.hnkylin.cloud.manage.entity.req.zone.ModifyZoneParam;
import com.hnkylin.cloud.manage.entity.req.zone.PageZoneParam;
import com.hnkylin.cloud.manage.entity.resp.cluster.PageClusterDetailDto;
import com.hnkylin.cloud.manage.entity.resp.vdc.VdcSameLevelUserUsedArchitectureResourceRespDto;
import com.hnkylin.cloud.manage.entity.resp.vdc.VdcUsedResourceDto;
import com.hnkylin.cloud.manage.entity.resp.zone.*;
import com.hnkylin.cloud.manage.enums.ClusterStatus;
import com.hnkylin.cloud.manage.service.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class ZoneServiceImpl implements ZoneService {


    @Resource
    private CloudZoneService cloudZoneService;

    @Resource
    private CloudZoneClusterService cloudZoneClusterService;


    @Resource
    private RoleService roleService;

    @Resource
    private OrgService orgService;


    @Resource
    private CloudOrgVdcService cloudOrgVdcService;

    @Resource
    private CloudVdcService cloudVdcService;

    @Resource
    private UserService userService;

    @Resource
    private VdcService vdcService;


    @Resource
    private ClusterService clusterService;


    @Resource
    private CloudClusterService cloudClusterService;

    @Resource
    private CloudVdcCpuService cloudVdcCpuService;

    @Resource
    private CloudVdcMemService cloudVdcMemService;

    @Resource
    private CloudVdcStorageService cloudVdcStorageService;


    @Override
    @Transactional
    public void createZone(CreateZoneParam createZoneParam, LoginUserVo loginUserVo) {
        //先查询是否重名
        checkIfExistZoneName(createZoneParam.getName());
        Date now = new Date();
        CloudZoneDo zoneDo = new CloudZoneDo();
        BeanUtils.copyProperties(createZoneParam, zoneDo);
        zoneDo.setCreateBy(loginUserVo.getUserId());
        zoneDo.setCreateTime(new Date());

        cloudZoneService.save(zoneDo);
        List<CloudZoneClusterDo> zoneClusterDoList = createZoneClusterList(createZoneParam.getClusterIdList(),
                zoneDo.getId(), now, loginUserVo.getUserId());

        if (!zoneClusterDoList.isEmpty()) {
            cloudZoneClusterService.saveBatch(zoneClusterDoList);
        }
    }

    /**
     * 创建可用区集群关联关系
     *
     * @param now
     * @param createUserId
     * @return
     */
    private List<CloudZoneClusterDo> createZoneClusterList(Set<Integer> clusterIdList, Integer zoneId,
                                                           Date now, Integer createUserId) {
        List<CloudZoneClusterDo> zoneClusterDoList = new ArrayList<>();
        clusterIdList.stream().forEach(clusterId -> {
            CloudZoneClusterDo zoneClusterDo = new CloudZoneClusterDo();
            zoneClusterDo.setZoneId(zoneId);
            zoneClusterDo.setClusterId(clusterId);
            zoneClusterDo.setCreateTime(now);
            zoneClusterDo.setCreateBy(createUserId);
            zoneClusterDoList.add(zoneClusterDo);
        });
        return zoneClusterDoList;
    }

    /**
     * 检查是否存在同名集群
     */
    private void checkIfExistZoneName(String zoneName) {
        //判断名称是否已经存在
        CloudZoneDo zoneDo = new CloudZoneDo();
        zoneDo.setName(zoneName);
        zoneDo.setDeleteFlag(false);
        Wrapper<CloudZoneDo> wrapper = new QueryWrapper<>(zoneDo);
        List<CloudZoneDo> zoneDoList = cloudZoneService.getBaseMapper().selectList(wrapper);
        if (!zoneDoList.isEmpty()) {
            throw new KylinException(KylinHttpResponseZoneConstants.EXIST_ZONE_NAME);
        }
    }


    @Override
    @Transactional
    public void modifyZone(ModifyZoneParam modifyZoneParam, LoginUserVo loginUserVo) {


        if (Objects.isNull(modifyZoneParam.getClusterIdList()) || modifyZoneParam.getClusterIdList().isEmpty()) {
            throw new KylinException(KylinHttpResponseZoneConstants.CLUSTER_IS_NOT_EMPTY);
        }

        CloudZoneDo zoneDo = cloudZoneService.getById(modifyZoneParam.getZoneId());
        if (!Objects.equals(zoneDo.getName(), modifyZoneParam.getName())) {
            checkIfExistZoneName(modifyZoneParam.getName());
        }
        Date now = new Date();
        zoneDo.setName(modifyZoneParam.getName());
        zoneDo.setRemark(modifyZoneParam.getRemark());
        zoneDo.setUpdateBy(loginUserVo.getUserId());
        zoneDo.setUpdateTime(now);

        cloudZoneService.updateById(zoneDo);

        List<CloudZoneClusterDo> zoneClusterDoList = getZoneClusterListByZone(modifyZoneParam.getZoneId());
        List<Integer> zoneClusterIdList =
                zoneClusterDoList.stream().map(CloudZoneClusterDo::getClusterId).collect(Collectors.toList());
        //如果有可用区下有VDC，则不能解绑集群
        List<CloudVdcDo> vdcDoList = cloudVdcService.vdcListByZone(modifyZoneParam.getZoneId());
        List<Integer> canNotUnbindClusterId = new ArrayList<>();
        if (!vdcDoList.isEmpty()) {
            zoneClusterIdList.forEach(clusterId -> {
                if (!modifyZoneParam.getClusterIdList().contains(clusterId)) {
                    canNotUnbindClusterId.add(clusterId);
                }

            });
            if (!canNotUnbindClusterId.isEmpty()) {
                throw new KylinException(KylinHttpResponseZoneConstants.ZONE_BIND_VDC_NOT_UNBIND_CLUSTER);
            }
        }


        //先把原来的节点删除
        zoneClusterDoList.stream().forEach(item -> {
            item.setDeleteFlag(Boolean.TRUE);
            item.setDeleteBy(loginUserVo.getUserId());
            item.setDeleteTime(now);
        });
        if (!zoneClusterDoList.isEmpty()) {
            cloudZoneClusterService.updateBatchById(zoneClusterDoList);
        }

        List<CloudZoneClusterDo> newInsertZoneClusterDoList = createZoneClusterList(modifyZoneParam.getClusterIdList(),
                zoneDo.getId(), now, loginUserVo.getUserId());

        if (!newInsertZoneClusterDoList.isEmpty()) {
            cloudZoneClusterService.saveBatch(newInsertZoneClusterDoList);
        }
    }


    /**
     * 获取可用区和集群绑定关系
     *
     * @param zoneId
     * @return
     */
    private List<CloudZoneClusterDo> getZoneClusterListByZone(Integer zoneId) {
        //获取已经被绑定过得物理集群
        CloudZoneClusterDo zoneClusterDo = new CloudZoneClusterDo();
        zoneClusterDo.setDeleteFlag(false);
        zoneClusterDo.setZoneId(zoneId);
        Wrapper<CloudZoneClusterDo> zoneClusterWrapper = new QueryWrapper<>(zoneClusterDo);
        List<CloudZoneClusterDo> zoneClusterList =
                cloudZoneClusterService.getBaseMapper().selectList(zoneClusterWrapper);
        return zoneClusterList;
    }


    @Override
    public PageData<PageZoneRespDto> pageZone(PageZoneParam pageZoneParam, LoginUserVo loginUserVo) {

        List<CloudZoneDo> userVisibleZoneList = visibleZoneListByUserId(loginUserVo.getUserId());
        if (userVisibleZoneList.isEmpty()) {
            return new PageData<>(null);
        }

        CloudZoneDo queryZoneDo = new CloudZoneDo();
        queryZoneDo.setDeleteFlag(false);
        QueryWrapper<CloudZoneDo> wrapper = new QueryWrapper<>(queryZoneDo);
        wrapper.in("id", userVisibleZoneList.stream().map(CloudZoneDo::getId).collect(Collectors.toList()));
        PageHelper.startPage(pageZoneParam.getPageNo(), pageZoneParam.getPageSize());
        List<CloudZoneDo> zoneList = cloudZoneService.getBaseMapper().selectList(wrapper);
        PageInfo<CloudZoneDo> pageInfo = new PageInfo<>(zoneList);
        List<PageZoneRespDto> pageZoneRespList = new ArrayList<>();
        zoneList.forEach(item -> {
            PageZoneRespDto pageZoneRespDto = new PageZoneRespDto();
            BeanUtils.copyProperties(item, pageZoneRespDto);
            pageZoneRespDto.setZoneId(item.getId());
            //可用区数据统计
            createZoneCommonDataStatistics(pageZoneRespDto, item.getId(), loginUserVo);

            //获取可用区下物理集群名称
            List<CloudClusterDo> clusterDoList = cloudClusterService.listClusterBYZoneId(item.getId());
            if (!clusterDoList.isEmpty()) {
                List<String> clusterNameList =
                        clusterDoList.stream().map(CloudClusterDo::getName).collect(Collectors.toList());
                pageZoneRespDto.setClusterNames(StringUtils.join(clusterNameList, ","));
            }

            pageZoneRespList.add(pageZoneRespDto);
        });

        PageData pageData = new PageData(pageInfo);
        pageData.setList(pageZoneRespList);
        return pageData;

    }

    /**
     * 可用区-通用数据统计
     */
    private void createZoneCommonDataStatistics(PageZoneRespDto pageZoneRespDto, Integer zoneId,
                                                LoginUserVo loginUserVo) {
        //获取可用区下物理集群列表
        List<CloudClusterDo> clusterDoList = cloudClusterService.listClusterBYZoneId(zoneId);
        //从mc获取物理集群数据字眼
        List<McClusterBaseResource> mcClusterResourceList = clusterService.moreClusterGetMcResource(clusterDoList,
                loginUserVo.getUserName());
        //获取可用区下所有物理集群总资源
        //将mcClusterResourceList  可用区下所有物理集群资源进行汇总
        mcClusterResourceList.forEach(mcClusterBaseResource -> {
            int totalStorage =
                    mcClusterBaseResource.getResourceInfo().getStorageTotal().setScale(0, BigDecimal.ROUND_HALF_UP).intValue();
            pageZoneRespDto.setStorageTotal(pageZoneRespDto.getStorageTotal() + totalStorage);

            List<McClusterServerResourceResp> mcSevers = mcClusterBaseResource.getSevers();
            if (!mcSevers.isEmpty()) {
                int totalCpu = mcSevers.stream().mapToInt(McClusterServerResourceResp::getVcpus).sum();
                //转换成GB
                int totalMem =
                        mcSevers.stream().mapToInt(item -> item.getMemory().divide(new BigDecimal("1024")).setScale(0
                                , BigDecimal.ROUND_HALF_UP).intValue()).sum();
                pageZoneRespDto.setCpuTotal(pageZoneRespDto.getCpuTotal() + totalCpu);
                pageZoneRespDto.setMemTotal(pageZoneRespDto.getMemTotal() + totalMem);
            }

        });

//        //获取可用区下所有一级VDC，计算所有一级VDC资源
//        List<CloudVdcDo> zoneFirstVdcDoList = cloudVdcService.getFirstVdcListByZone(zoneId);
//        zoneFirstVdcDoList.forEach(firstVdc -> {
//
//            //获取分配给一级VDC的cpu
//            int firstVdcCpu=cloudVdcCpuService.totalCpuByVdcId(firstVdc.getId());
//            //获取分配给一级VDC的内存
//            CloudVdcMemDo vdcTotalMemDo = cloudVdcMemService.totalMemByVdcId(firstVdc.getId());
//            //获取分配给一级VDC的存储
//            CloudVdcStorageDo vdcTotalStorage = cloudVdcStorageService.getTotalStorageByVdcId(firstVdc.getId());
//
//            zoneDataStatisticsDto.setCpuUsed(zoneDataStatisticsDto.getCpuUsed()+firstVdcCpu);
//            zoneDataStatisticsDto.setMemUsed(zoneDataStatisticsDto.getMemUsed()+vdcTotalMemDo.getMem());
//            zoneDataStatisticsDto.setStorageUsed(zoneDataStatisticsDto.getStorageUsed()+vdcTotalStorage.getStorage());
//
//        });
    }


    @Override
    public ZoneInfoDto modifyZoneDetail(BaseZoneParam baseZoneParam, LoginUserVo loginUserVo) {
        CloudZoneDo queryZoneDo = cloudZoneService.getById(baseZoneParam.getZoneId());
        ZoneInfoDto zoneInfoDto = new ZoneInfoDto();
        BeanUtils.copyProperties(queryZoneDo, zoneInfoDto);
        zoneInfoDto.setZoneId(queryZoneDo.getId());
        return zoneInfoDto;
    }

    @Override
    public ZoneDetailDto zoneDetail(BaseZoneParam baseZoneParam, LoginUserVo loginUserVo) {
        CloudZoneDo queryZoneDo = cloudZoneService.getById(baseZoneParam.getZoneId());
        ZoneDetailDto zoneDetail = new ZoneDetailDto();
        BeanUtils.copyProperties(queryZoneDo, zoneDetail);
        zoneDetail.setZoneId(queryZoneDo.getId());
        zoneDetail.setCreateTime(DateUtils.format(queryZoneDo.getCreateTime(), DateUtils.DATE_ALL_PATTEN));
        //可用区已绑定的物理资源
        List<CloudClusterDo> clusterDoList = cloudClusterService.listClusterBYZoneId(baseZoneParam.getZoneId());
        //从mc获取物理集群数据资源
        List<McClusterBaseResource> mcClusterResourceList = clusterService.moreClusterGetMcResource(clusterDoList,
                loginUserVo.getUserName());


        //在线集群，离线物理集群
        zoneDetail.setClusterTotal(clusterDoList.size());
        zoneDetail.setClusterOnline(mcClusterResourceList.size());
        zoneDetail.setClusterOffline(zoneDetail.getClusterTotal() - zoneDetail.getClusterOnline());

        //将mcClusterResourceList  可用区下所有物理集群资源进行汇总
        mcClusterResourceList.forEach(mcClusterBaseResource -> {
            int totalStorage =
                    mcClusterBaseResource.getResourceInfo().getStorageTotal().setScale(0, BigDecimal.ROUND_HALF_UP).intValue();
            zoneDetail.setStorageTotal(zoneDetail.getStorageTotal() + totalStorage);

            List<McClusterServerResourceResp> mcSevers = mcClusterBaseResource.getSevers();
            if (!mcSevers.isEmpty()) {
                int totalCpu = mcSevers.stream().mapToInt(McClusterServerResourceResp::getVcpus).sum();
                //转换成GB
                int totalMem =
                        mcSevers.stream().mapToInt(item -> item.getMemory().divide(new BigDecimal("1024")).setScale(0
                                , BigDecimal.ROUND_HALF_UP).intValue()).sum();
                zoneDetail.setCpuTotal(zoneDetail.getCpuTotal() + totalCpu);
                zoneDetail.setMemTotal(zoneDetail.getMemTotal() + totalMem);
            }

        });

        //获取可用区下所有一级VDC，计算所有一级VDC资源
        List<CloudVdcDo> zoneFirstVdcDoList = cloudVdcService.getFirstVdcListByZone(queryZoneDo.getId());
        zoneFirstVdcDoList.forEach(firstVdc -> {

            //获取分配给一级VDC的cpu
            int firstVdcCpu = cloudVdcCpuService.totalCpuByVdcId(firstVdc.getId());
            //获取分配给一级VDC的内存
            CloudVdcMemDo vdcTotalMemDo = cloudVdcMemService.totalMemByVdcId(firstVdc.getId());
            //获取分配给一级VDC的存储
            CloudVdcStorageDo vdcTotalStorage = cloudVdcStorageService.getTotalStorageByVdcId(firstVdc.getId());

            zoneDetail.setCpuUsed(zoneDetail.getCpuUsed() + firstVdcCpu);
            zoneDetail.setMemUsed(zoneDetail.getMemUsed() + vdcTotalMemDo.getMem());
            zoneDetail.setStorageUsed(zoneDetail.getStorageUsed() + vdcTotalStorage.getStorage());

        });

        if (!mcClusterResourceList.isEmpty()) {
            //物理主机个数
            Integer physicalHostTotal =
                    mcClusterResourceList.stream().collect(Collectors.summingInt(McClusterBaseResource::getTotalPhysicalHost));
            //在线物理主机个数
            Integer physicalHostOnline =
                    mcClusterResourceList.stream().collect(Collectors.summingInt(McClusterBaseResource::getOnlinePhysicalHost));
            ;
            //离线物理主机个数
            Integer physicalHostOffline =
                    mcClusterResourceList.stream().collect(Collectors.summingInt(McClusterBaseResource::getOfflinePhysicalHost));
            ;


            //总云服务器个数
            Integer machineTotal =
                    mcClusterResourceList.stream().collect(Collectors.summingInt(McClusterBaseResource::getTotalMachine));
            ;
            //在线云服务器
            Integer machineOnline =
                    mcClusterResourceList.stream().collect(Collectors.summingInt(McClusterBaseResource::getOnlineMachine));
            ;
            //离线云服务器
            Integer machineOffline =
                    mcClusterResourceList.stream().collect(Collectors.summingInt(McClusterBaseResource::getOfflineMachine));
            ;

            zoneDetail.setPhysicalHostTotal(physicalHostTotal);
            zoneDetail.setPhysicalHostOnline(physicalHostOnline);
            zoneDetail.setPhysicalHostOffline(physicalHostOffline);
            zoneDetail.setMachineTotal(machineTotal);
            zoneDetail.setMachineOnline(machineOnline);
            zoneDetail.setMachineOffline(machineOffline);
        }

        return zoneDetail;
    }


    @Override
    public List<ZoneInfoDto> zoneList(LoginUserVo loginUserVo) {

        CloudRoleDo roleDo = roleService.getUserRole(loginUserVo.getUserId());


        if (Objects.isNull(roleDo)) {
            return new ArrayList<>();
        }
        List<ZoneInfoDto> zoneInfoDtoList = new ArrayList<>();

        //如果是平台管理用户，则全部可用区为可见
        boolean ifPlatformUser = userService.judgeIfPlatformUser(loginUserVo.getUserId());
        if (ifPlatformUser) {
            return createZoneList(new ArrayList<>());
        }
        CloudOrganizationDo loginUserOrg = orgService.getByUserId(loginUserVo.getUserId());

        //如果不是则 查询登录用户对应组织拥有的VDC所关联的可用区
        CloudOrgVdcDo queryCloudOrgVdcDo = new CloudOrgVdcDo();
        queryCloudOrgVdcDo.setDeleteFlag(false);
        queryCloudOrgVdcDo.setOrgId(loginUserOrg.getId());
        Wrapper<CloudOrgVdcDo> orgVdcWrapper = new QueryWrapper<>(queryCloudOrgVdcDo);
        List<CloudOrgVdcDo> orgVdcList = cloudOrgVdcService.getBaseMapper().selectList(orgVdcWrapper);
        if (orgVdcList.isEmpty()) {
            //如果组织没有VDC则返回无可用区
            return zoneInfoDtoList;
        }

        CloudVdcDo queryVdcDo = new CloudVdcDo();
        queryVdcDo.setDeleteFlag(false);
        QueryWrapper<CloudVdcDo> vdcWrapper = new QueryWrapper<>(queryVdcDo);
        vdcWrapper.in("id", orgVdcList.stream().map(CloudOrgVdcDo::getVdcId).collect(Collectors.toList()));
        List<CloudVdcDo> vdcList = cloudVdcService.getBaseMapper().selectList(vdcWrapper);

        if (vdcList.isEmpty()) {
            //如果组织没有VDC则返回无可用区
            return zoneInfoDtoList;
        }

        return createZoneList(vdcList.stream().map(CloudVdcDo::getZoneId).collect(Collectors.toList()));

    }


    /**
     * 创建可用区列表
     *
     * @param zoneIdList
     */
    private List<ZoneInfoDto> createZoneList(List<Integer> zoneIdList) {
        List<ZoneInfoDto> zoneInfoDtoList = new ArrayList<>();
        CloudZoneDo queryZoneDo = new CloudZoneDo();
        queryZoneDo.setDeleteFlag(false);
        QueryWrapper<CloudZoneDo> wrapper = new QueryWrapper<>(queryZoneDo);
        if (!zoneIdList.isEmpty()) {
            wrapper.in("id", zoneIdList);
        }
        List<CloudZoneDo> zoneList = cloudZoneService.getBaseMapper().selectList(wrapper);

        zoneList.forEach(item -> {
            ZoneInfoDto zoneInfoDto = new ZoneInfoDto();
            zoneInfoDto.setZoneId(item.getId());
            zoneInfoDto.setName(item.getName());
            zoneInfoDtoList.add(zoneInfoDto);
        });
        return zoneInfoDtoList;
    }

    @Override
    public CloudZoneDo getZoneByClusterId(Integer clusterId) {
        return cloudZoneService.getByClusterId(clusterId);
    }

    @Override
    public List<CloudZoneDo> visibleZoneListByUserId(Integer userId) {
        //平台管理用户，则全部可用区可见
        boolean ifPlatformUser = userService.judgeIfPlatformUser(userId);
        if (ifPlatformUser) {
            CloudZoneDo zoneDo = new CloudZoneDo();
            zoneDo.setDeleteFlag(false);
            Wrapper<CloudZoneDo> wrapper = new QueryWrapper<>(zoneDo);
            return cloudZoneService.list(wrapper);
        }
        CloudVdcDo vdcDo = vdcService.getUserOrgBindVdc(userId);
        if (Objects.isNull(vdcDo)) {
            return new ArrayList<>();
        }
        List<CloudZoneDo> zoneDoList = new ArrayList<>();
        CloudZoneDo userZone = cloudZoneService.getById(vdcDo.getZoneId());
        zoneDoList.add(userZone);
        return zoneDoList;
    }

    @Override
    @Transactional
    public void deleteZone(BaseZoneParam baseZoneParam, LoginUserVo loginUserVo) {

        //删除可用区限制条件
        //1:没有VDC绑定该可用区
        CloudVdcDo vdcDo = new CloudVdcDo();
        vdcDo.setDeleteFlag(false);
        vdcDo.setZoneId(baseZoneParam.getZoneId());
        QueryWrapper<CloudVdcDo> wrapper = new QueryWrapper<>(vdcDo);
        int zoneVdcCount = cloudVdcService.count(wrapper);
        if (zoneVdcCount > 0) {
            throw new KylinException(KylinHttpResponseZoneConstants.EXIST_VDC_NOT_DELETE);
        }
        CloudZoneDo zoneDo = cloudZoneService.getById(baseZoneParam.getZoneId());
        zoneDo.setDeleteFlag(true);
        zoneDo.setDeleteBy(loginUserVo.getUserId());
        zoneDo.setDeleteTime(new Date());
        cloudZoneService.updateById(zoneDo);

        //删除可用区绑定的集群的关系记录
        List<CloudZoneClusterDo> zoneClusterDoList =
                cloudZoneClusterService.listZoneClusterByZone(baseZoneParam.getZoneId());
        zoneClusterDoList.forEach(item -> {
            item.setDeleteFlag(true);
            item.setDeleteBy(loginUserVo.getUserId());
            item.setDeleteTime(new Date());
        });
        if (!zoneClusterDoList.isEmpty()) {
            cloudZoneClusterService.updateBatchById(zoneClusterDoList);
        }
    }
}
