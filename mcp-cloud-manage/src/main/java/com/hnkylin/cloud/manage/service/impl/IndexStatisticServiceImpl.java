package com.hnkylin.cloud.manage.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hnkylin.cloud.core.common.servervm.ServerVmBatchReq;
import com.hnkylin.cloud.core.domain.*;
import com.hnkylin.cloud.core.enums.MemUnit;
import com.hnkylin.cloud.core.enums.RoleType;
import com.hnkylin.cloud.core.enums.StorageUnit;
import com.hnkylin.cloud.core.service.*;
import com.hnkylin.cloud.manage.config.MCConfigProperties;
import com.hnkylin.cloud.manage.entity.LoginUserVo;
import com.hnkylin.cloud.manage.entity.mc.req.QueryVdcUsedResourceParam;
import com.hnkylin.cloud.manage.entity.mc.resp.*;
import com.hnkylin.cloud.manage.entity.resp.index.*;
import com.hnkylin.cloud.manage.entity.resp.vdc.VdcUsedResourceDto;
import com.hnkylin.cloud.manage.mapper.UserMapper;
import com.hnkylin.cloud.manage.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class IndexStatisticServiceImpl implements IndexStatisticService {

    @Resource
    private CloudZoneService cloudZoneService;

    @Resource
    private ClusterService clusterService;

    @Resource
    private CloudVdcService cloudVdcService;

    @Resource
    private CloudOrgVdcService cloudOrgVdcService;


    @Resource
    private CloudClusterService cloudClusterService;

    @Resource
    private UserMapper userMapper;

    @Resource
    private WorkOrderService workOrderService;

    @Resource
    private MonitorService monitorService;

    @Resource
    private CloudUserService cloudUserService;

    @Resource
    private VdcService vdcService;

    @Resource
    private UserService userService;

    @Resource
    private UserMachineService userMachineService;

    @Resource
    private OrgService orgService;


    @Override
    public IndexStatisticData statisticData(LoginUserVo loginUserVo) {

        IndexStatisticData indexStatisticData = new IndexStatisticData();
        indexStatisticData.setZoneData(statisticZoneDate());
        statisticClusterData(indexStatisticData, loginUserVo);
        indexStatisticData.setVdcData(getVdcData());
        indexStatisticData.setUserData(getUserData());
        //提醒数据
        IndexNoticeData noticeData = new IndexNoticeData();
        noticeData.setWaitCheckCount(workOrderService.getWaitCheckCount(loginUserVo).getWaitCheckCount());
        noticeData.setAlarmCount(monitorService.alarmNotifications(loginUserVo).getNotificationsCount());
        indexStatisticData.setNoticeData(noticeData);
        //云服务器top数据
        CloudClusterDo clusterDo = new CloudClusterDo();
        clusterDo.setDeleteFlag(false);
        QueryWrapper<CloudClusterDo> wrapper = new QueryWrapper(clusterDo);
        List<CloudClusterDo> clusterDoList = cloudClusterService.list(wrapper);
        serverVmRunningInfo(clusterDoList, new ArrayList<>(), indexStatisticData, loginUserVo);
        //物理主机top数据
        physicalHostUsedRatioData(indexStatisticData, loginUserVo);
        return indexStatisticData;
    }

    /**
     * 可用区数据统计
     *
     * @return
     */
    private IndexZoneData statisticZoneDate() {
        IndexZoneData zoneData = new IndexZoneData();
        CloudZoneDo cloudZoneDo = new CloudZoneDo();
        cloudZoneDo.setDeleteFlag(false);
        QueryWrapper<CloudZoneDo> wrapper = new QueryWrapper(cloudZoneDo);
        Integer zoneCount = cloudZoneService.getBaseMapper().selectCount(wrapper);
        zoneData.setTotalZone(zoneCount);
        return zoneData;
    }

    /**
     * 集群统计
     *
     * @return
     */
    private void statisticClusterData(IndexStatisticData indexStatisticData, LoginUserVo loginUserVo) {
        IndexClusterData clusterData = new IndexClusterData();
        CloudClusterDo clusterDo = new CloudClusterDo();
        clusterDo.setDeleteFlag(false);
        QueryWrapper<CloudClusterDo> wrapper = new QueryWrapper(clusterDo);
        List<CloudClusterDo> clusterDoList = cloudClusterService.list(wrapper);
        clusterData.setTotalCluster(clusterDoList.size());

        if (!clusterDoList.isEmpty()) {
            List<McClusterBaseResource> mcClusterBaseResourceList =
                    clusterService.moreClusterGetMcResource(clusterDoList,
                            loginUserVo.getUserName());
            clusterData.setOnlineCluster(mcClusterBaseResourceList.size());
            if (!mcClusterBaseResourceList.isEmpty()) {
                //物理主机个数
                Integer physicalHostTotal =
                        mcClusterBaseResourceList.stream().collect(Collectors.summingInt(McClusterBaseResource::getTotalPhysicalHost));
                //在线物理主机个数
                Integer physicalHostOnline =
                        mcClusterBaseResourceList.stream().collect(Collectors.summingInt(McClusterBaseResource::getOnlinePhysicalHost));
                ;
                //离线物理主机个数
                Integer physicalHostOffline =
                        mcClusterBaseResourceList.stream().collect(Collectors.summingInt(McClusterBaseResource::getOfflinePhysicalHost));
                ;
                //物理主机数据
                IndexPhysicalHostData physicalHostData = new IndexPhysicalHostData();
                physicalHostData.setTotalPhysicalHost(physicalHostTotal);
                physicalHostData.setOnlinePhysicalHost(physicalHostOnline);
                physicalHostData.setOfflinePhysicalHost(physicalHostOffline);
                indexStatisticData.setPhysicalHostData(physicalHostData);


                //cpu数据
                IndexCpuData cpuData = new IndexCpuData();
                //内存数据
                IndexMemData memData = new IndexMemData();
                //储存数据
                IndexStorageData storageData = new IndexStorageData();

                mcClusterBaseResourceList.forEach(item -> {
                    McClusterResourceUsedResp resourceUsedResp = item.getResourceInfo();
                    cpuData.setTotalCpu(cpuData.getTotalCpu().add(resourceUsedResp.getCpuTotal()).setScale(2,
                            BigDecimal.ROUND_HALF_UP));
                    cpuData.setUsedCpu(cpuData.getUsedCpu().add(resourceUsedResp.getCpuUsed()).setScale(2,
                            BigDecimal.ROUND_HALF_UP));
                    memData.setTotalMem(memData.getTotalMem().add(resourceUsedResp.getMemTotal()).setScale(2,
                            BigDecimal.ROUND_HALF_UP));
                    memData.setUsedMem(memData.getUsedMem().add(resourceUsedResp.getMemUsed()).setScale(2,
                            BigDecimal.ROUND_HALF_UP));
                    storageData.setTotalStorage(storageData.getTotalStorage().add(resourceUsedResp.getStorageTotal()).setScale(2, BigDecimal.ROUND_HALF_UP));
                    storageData.setUsedStorage(storageData.getUsedStorage().add(resourceUsedResp.getStorageUsed()).setScale(2,
                            BigDecimal.ROUND_HALF_UP));
                });
                cpuData.setUsableCpu(cpuData.getTotalCpu().subtract(cpuData.getUsableCpu()).setScale(2,
                        BigDecimal.ROUND_HALF_UP));
                memData.setUsableMem(memData.getTotalMem().subtract(memData.getUsedMem()).setScale(2,
                        BigDecimal.ROUND_HALF_UP));
                storageData.setUsableStorage(storageData.getTotalStorage().subtract(storageData.getUsedStorage()).setScale(2, BigDecimal.ROUND_HALF_UP));
                indexStatisticData.setCpuData(cpuData);
                indexStatisticData.setMemData(memData);
                indexStatisticData.setStorageData(storageData);
            }

        }
        clusterData.setOfflineCluster(clusterData.getTotalCluster() - clusterData.getOnlineCluster());
        indexStatisticData.setClusterData(clusterData);
    }


    /**
     * 获取VDC数据
     *
     * @return
     */
    private IndexVdcData getVdcData() {
        IndexVdcData indexVdcData = new IndexVdcData();
        CloudVdcDo queryVdc = new CloudVdcDo();
        queryVdc.setDeleteFlag(false);
        QueryWrapper<CloudVdcDo> vdcWrapper = new QueryWrapper(queryVdc);
        Integer totalVdc = cloudVdcService.count(vdcWrapper);
        indexVdcData.setTotalVdc(totalVdc);
        if (Objects.nonNull(totalVdc) && totalVdc > 0) {
            //获取已经绑定组织的VDC
            CloudOrgVdcDo queryOrgVdc = new CloudOrgVdcDo();
            queryOrgVdc.setDeleteFlag(false);
            QueryWrapper<CloudOrgVdcDo> orgVdcWrapper = new QueryWrapper(queryOrgVdc);
            Integer alreadyAllocateVdc = cloudOrgVdcService.count(orgVdcWrapper);
            indexVdcData.setAlreadyAllocateVdc(alreadyAllocateVdc);
            indexVdcData.setNoAllocateVdc(totalVdc - alreadyAllocateVdc);
        }
        return indexVdcData;
    }

    /**
     * 获取用户数量
     *
     * @return
     */
    private IndexUserData getUserData() {
        IndexUserData indexUserData = new IndexUserData();
        indexUserData.setOrgManageUserCount(userMapper.getUserCountByRoleType(null, RoleType.ORG.ordinal()));
        indexUserData.setSelfServiceUserCount(userMapper.getUserCountByRoleType(null, RoleType.SELF_SERVICE.ordinal()));
        indexUserData.setPlatformManageUserCount(userMapper.getUserCountByRoleType(null, RoleType.PLATFORM.ordinal()));
        return indexUserData;
    }

    /**
     * 获取物理集群中云服务器使用率top
     *
     * @param indexCommonData
     * @param loginUserVo
     */
    private void serverVmRunningInfo(List<CloudClusterDo> clusterDoList, List<Object> serverVmUuidList,
                                     IndexCommonData indexCommonData,
                                     LoginUserVo loginUserVo) {


        List<McServerVmRunningInfoDto> mcServerVmRunningInfoList =
                clusterService.moreClusterServerVmRunningInfo(serverVmUuidList, clusterDoList,
                        loginUserVo.getUserName());

        List<McServerVmUseRatioData> totalCpuTopList = new ArrayList<>();

        List<McServerVmUseRatioData> totalMemTopList = new ArrayList<>();

        mcServerVmRunningInfoList.forEach(item -> {
            if (Objects.nonNull(item.getCpuTopList()) && !item.getCpuTopList().isEmpty()) {
                totalCpuTopList.addAll(item.getCpuTopList());
            }
            if (Objects.nonNull(item.getMemTopList()) && !item.getMemTopList().isEmpty()) {
                totalMemTopList.addAll(item.getMemTopList());
            }
        });
        List<McServerVmUseRatioData> cpuTopList = new ArrayList<>();
        List<McServerVmUseRatioData> memTopList = new ArrayList<>();
        int topSize = 5;
        if (!totalCpuTopList.isEmpty()) {
            totalCpuTopList.sort((obj1, obj2) -> obj2.getCpuUseRatio().compareTo(obj1.getCpuUseRatio()));
            int subSize = (totalCpuTopList.size() > topSize) ? topSize : totalCpuTopList.size();
            cpuTopList = totalCpuTopList.subList(0, subSize);
        }
        if (!totalMemTopList.isEmpty()) {
            totalMemTopList.sort((obj1, obj2) -> obj2.getMemUseRatio().compareTo(obj1.getMemUseRatio()));
            int subSize = (totalMemTopList.size() > topSize) ? topSize : totalMemTopList.size();
            memTopList = totalMemTopList.subList(0, subSize);
        }

        indexCommonData.setServerVmCpuTopUseRatioList(cpuTopList);
        indexCommonData.setServerVmMemTopUseRatioList(memTopList);

        //总云服务器个数
        Integer machineTotal =
                mcServerVmRunningInfoList.stream().collect(Collectors.summingInt(McServerVmRunningInfoDto::getTotalMachine));
        //在线云服务器
        Integer machineOnline =
                mcServerVmRunningInfoList.stream().collect(Collectors.summingInt(McServerVmRunningInfoDto::getOnlineMachine));
        //离线云服务器
        Integer machineOffline =
                mcServerVmRunningInfoList.stream().collect(Collectors.summingInt(McServerVmRunningInfoDto::getOfflineMachine));
        IndexServerVmData serverVmData = new IndexServerVmData();
        //云服务器数据
        serverVmData.setTotalServerVm(machineTotal);
        serverVmData.setOnlineServerVm(machineOnline);
        serverVmData.setOfflineServerVm(machineOffline);
        indexCommonData.setServerVmData(serverVmData);

    }

    /**
     * 获取集群中物理主机top利用率
     *
     * @param indexStatisticData
     * @param loginUserVo
     */
    private void physicalHostUsedRatioData(IndexStatisticData indexStatisticData, LoginUserVo loginUserVo) {
        CloudClusterDo clusterDo = new CloudClusterDo();
        clusterDo.setDeleteFlag(false);
        QueryWrapper<CloudClusterDo> wrapper = new QueryWrapper(clusterDo);
        List<CloudClusterDo> clusterDoList = cloudClusterService.list(wrapper);

        List<McClusterPhysicalHostResp> mcClusterPhysicalHostList =
                clusterService.moreClusterPhysicalHost(clusterDoList,
                        loginUserVo.getUserName());

        List<IndexTopPhysicalHostUsedRatioData> physicalHostSortByCpuList = new ArrayList<>();
        List<IndexTopPhysicalHostUsedRatioData> physicalHostSortByMemList = new ArrayList<>();

        if (!mcClusterPhysicalHostList.isEmpty()) {
            mcClusterPhysicalHostList.forEach(item -> {
                IndexTopPhysicalHostUsedRatioData physicalHostCpuUsedRatio = new IndexTopPhysicalHostUsedRatioData();
                physicalHostCpuUsedRatio.setServerIp(item.getServerAddr());
                physicalHostCpuUsedRatio.setUsedRatio(item.getCpuUtil());
                physicalHostSortByCpuList.add(physicalHostCpuUsedRatio);

                IndexTopPhysicalHostUsedRatioData physicalHostMemUsedRatio = new IndexTopPhysicalHostUsedRatioData();
                physicalHostMemUsedRatio.setServerIp(item.getServerAddr());
                physicalHostMemUsedRatio.setUsedRatio(item.getMemUtil());
                physicalHostSortByMemList.add(physicalHostMemUsedRatio);
            });

            int topSize = 5;
            physicalHostSortByCpuList.sort((obj1, obj2) -> obj2.getUsedRatio().compareTo(obj1.getUsedRatio()));
            int subCpuSize = (physicalHostSortByCpuList.size() > topSize) ? topSize :
                    physicalHostSortByCpuList.size() - 1;
            List<IndexTopPhysicalHostUsedRatioData> topCpuList = physicalHostSortByCpuList.subList(0, subCpuSize);

            physicalHostSortByMemList.sort((obj1, obj2) -> obj2.getUsedRatio().compareTo(obj1.getUsedRatio()));
            int subMemSize = (physicalHostSortByMemList.size() > topSize) ? topSize :
                    physicalHostSortByMemList.size() - 1;
            List<IndexTopPhysicalHostUsedRatioData> topMemList = physicalHostSortByMemList.subList(0, subMemSize);

            indexStatisticData.setPhysicalHostCpuTopUseRatioList(topCpuList);
            indexStatisticData.setPhysicalHostMemTopUseRatioList(topMemList);
        }

    }


    @Override
    public OrgLeaderIndexStatisticData orgLeaderStatistic(LoginUserVo loginUserVo) {
        OrgLeaderIndexStatisticData orgLeaderIndexStatisticData = new OrgLeaderIndexStatisticData();
        //提醒数据
        IndexNoticeData noticeData = new IndexNoticeData();
        noticeData.setWaitCheckCount(workOrderService.getWaitCheckCount(loginUserVo).getWaitCheckCount());
        noticeData.setAlarmCount(monitorService.alarmNotifications(loginUserVo).getNotificationsCount());
        orgLeaderIndexStatisticData.setNoticeData(noticeData);

        //创建VDC-cpu，内存，存储数据
        createVdcResource(orgLeaderIndexStatisticData, loginUserVo);
        //创建云服务器数据，top利用率数据
        createOrgLeaderServerVmData(orgLeaderIndexStatisticData, loginUserVo);

        //创建VDC数据
        orgLeaderIndexStatisticData.setVdcData(createOrgLeaderVdcData(loginUserVo));
        orgLeaderIndexStatisticData.setUserData(createOrgLeaderUserData(loginUserVo));
        return orgLeaderIndexStatisticData;
    }

    /**
     * 创建VDC-cpu，内存，存储数据
     *
     * @param loginUserVo
     */
    private void createVdcResource(OrgLeaderIndexStatisticData orgLeaderIndexStatisticData, LoginUserVo loginUserVo) {
        //获取登录用户组织对应的VDC
        CloudUserDo loginUserDo = cloudUserService.getById(loginUserVo.getUserId());
        CloudVdcDo loginUserVdc = vdcService.getVdcByOrgId(loginUserDo.getOrganizationId());
        //更具vdc获取VDC资源数据
        VdcUsedResourceDto vdcUsedResource = vdcService.getVdcResourceInfo(loginUserVdc.getId(), loginUserVo);

        //vdc-cpu资源
        OrgLeaderVdcUsedData vdcCpuUsedData = new OrgLeaderVdcUsedData();
        //vdc-内存
        OrgLeaderVdcUsedData vdcMemUsedData = new OrgLeaderVdcUsedData();

        //vdc-存储
        OrgLeaderVdcUsedData vdcStorageUsedData = new OrgLeaderVdcUsedData();
        if (Objects.nonNull(vdcUsedResource)) {
            //封cpu数据
            vdcCpuUsedData.setTotal(vdcUsedResource.getTotalCpu());
            vdcCpuUsedData.setAllocateChild(vdcUsedResource.getAllocationCpu());
            vdcCpuUsedData.setSameUsed(vdcUsedResource.getSameUserUsedCpu());
            vdcCpuUsedData.setUsable(vdcUsedResource.getSurplusCpu());
            orgLeaderIndexStatisticData.setVdcCpuUsedData(vdcCpuUsedData);

            //封装内存数据
            vdcMemUsedData.setTotal(vdcUsedResource.getTotalMem());
            vdcMemUsedData.setAllocateChild(vdcUsedResource.getAllocationMem());
            vdcMemUsedData.setSameUsed(vdcUsedResource.getSameUserUsedMem());
            vdcMemUsedData.setUsable(vdcUsedResource.getSurplusMem());
            vdcMemUsedData.setUnit(MemUnit.GB.name());
            orgLeaderIndexStatisticData.setVdcMemUsedData(vdcMemUsedData);

            //封装存储数据
            vdcStorageUsedData.setTotal(vdcUsedResource.getTotalStorage());
            vdcStorageUsedData.setAllocateChild(vdcUsedResource.getAllocationStorage());
            vdcStorageUsedData.setSameUsed(vdcUsedResource.getSameUserUsedStorage());
            vdcStorageUsedData.setUsable(vdcUsedResource.getSurplusStorage());
            vdcStorageUsedData.setUnit(StorageUnit.GB.name());
            orgLeaderIndexStatisticData.setVdcStorageUsedData(vdcStorageUsedData);
        }
    }

    /**
     * 非系统管理员登录-云服务器数据，top利用率数据
     *
     * @param orgLeaderIndexStatisticData
     * @param loginUserVo
     */
    private void createOrgLeaderServerVmData(OrgLeaderIndexStatisticData orgLeaderIndexStatisticData,
                                             LoginUserVo loginUserVo) {

        CloudUserDo loginUserDo = cloudUserService.getById(loginUserVo.getUserId());
        List<Integer> orgIdList = orgService.getOrgChildIdList(loginUserDo.getOrganizationId());

        List<CloudUserDo> userDoList = userService.listUserByOrgList(orgIdList, null);

        //获取组织下用户列表
        if (userDoList.isEmpty()) {
            return;
        }

        //用户用户拥有的云服务器关联关系
        List<CloudUserMachineDo> userMachineDoList =
                userMachineService.listUserMachineByUserIdList(userDoList.stream().map(CloudUserDo::getId).collect(Collectors.toList()));
        if (userMachineDoList.isEmpty()) {
            return;
        }
        //将用户云服务器进行分组
        Map<Integer, List<CloudUserMachineDo>> clusterUserMachineMap =
                userMachineDoList.stream().collect(Collectors.groupingBy(CloudUserMachineDo::getClusterId));

        List<CloudClusterDo> clusterDoList = new ArrayList<>();
        List<Object> serverVmUuidList = new ArrayList<>();

        clusterUserMachineMap.forEach((clusterId, machineList) -> {
            CloudClusterDo clusterDo = cloudClusterService.getById(clusterId);
            clusterDoList.add(clusterDo);
            List<String> uuidList =
                    machineList.stream().map(CloudUserMachineDo::getMachineUuid).collect(Collectors.toList());
            QueryVdcUsedResourceParam queryVdcUsedResourceParam = new QueryVdcUsedResourceParam();
            queryVdcUsedResourceParam.setUuidList(uuidList);
            serverVmUuidList.add(queryVdcUsedResourceParam);
        });
        serverVmRunningInfo(clusterDoList, serverVmUuidList, orgLeaderIndexStatisticData, loginUserVo);
    }

    /**
     * 创建VDC数量及已分配VDC
     *
     * @param loginUserVo
     * @return
     */
    private IndexVdcData createOrgLeaderVdcData(LoginUserVo loginUserVo) {
        IndexVdcData indexVdcData = new IndexVdcData();
        //获取登录用户组织对应的VDC
        CloudUserDo loginUserDo = cloudUserService.getById(loginUserVo.getUserId());
        CloudVdcDo loginUserVdc = vdcService.getVdcByOrgId(loginUserDo.getOrganizationId());

        List<CloudVdcDo> allChildVdcList = cloudVdcService.getAllChildVdcList(loginUserVdc.getId());
        indexVdcData.setTotalVdc(allChildVdcList.size());
        if (!allChildVdcList.isEmpty()) {
            //获取已经绑定组织的VDC
            CloudOrgVdcDo queryOrgVdc = new CloudOrgVdcDo();
            queryOrgVdc.setDeleteFlag(false);
            QueryWrapper<CloudOrgVdcDo> orgVdcWrapper = new QueryWrapper(queryOrgVdc);
            orgVdcWrapper.in("vdc_id", allChildVdcList.stream().map(CloudVdcDo::getId).collect(Collectors.toList()));
            Integer alreadyAllocateVdc = cloudOrgVdcService.count(orgVdcWrapper);
            indexVdcData.setAlreadyAllocateVdc(alreadyAllocateVdc);
            indexVdcData.setNoAllocateVdc(indexVdcData.getTotalVdc() - indexVdcData.getAlreadyAllocateVdc());
        }
        return indexVdcData;
    }


    /**
     * 获取用户数量
     *
     * @return
     */
    private IndexUserData createOrgLeaderUserData(LoginUserVo loginUserVo) {
        IndexUserData indexUserData = new IndexUserData();

        CloudUserDo loginUserDo = cloudUserService.getById(loginUserVo.getUserId());
        //获取下级组织列表
        List<Integer> orgIdList = orgService.getOrgChildIdList(loginUserDo.getOrganizationId());

        //获取当前组织及下级组织中组织管理员数量
        indexUserData.setOrgManageUserCount(userMapper.getUserCountByRoleType(orgIdList,
                RoleType.ORG.ordinal()));
        //获取当前组织及下级组织中自服务用户
        indexUserData.setSelfServiceUserCount(userMapper.getUserCountByRoleType(orgIdList,
                RoleType.SELF_SERVICE.ordinal()));


        return indexUserData;
    }


}
