package com.hnkylin.cloud.manage.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.hnkylin.cloud.core.common.*;
import com.hnkylin.cloud.core.common.servervm.ServerVmBatchReq;
import com.hnkylin.cloud.core.common.servervm.ServerVmListReq;
import com.hnkylin.cloud.core.config.exception.KylinException;
import com.hnkylin.cloud.core.domain.*;
import com.hnkylin.cloud.core.enums.HttpTypes;
import com.hnkylin.cloud.core.enums.McKsvdServerType;
import com.hnkylin.cloud.core.enums.RoleType;
import com.hnkylin.cloud.core.service.CloudClusterNodeService;
import com.hnkylin.cloud.core.service.CloudClusterService;
import com.hnkylin.cloud.core.service.CloudUserMachineService;
import com.hnkylin.cloud.core.service.CloudZoneClusterService;
import com.hnkylin.cloud.manage.config.MCConfigProperties;
import com.hnkylin.cloud.manage.constant.KylinCloudManageConstants;
import com.hnkylin.cloud.manage.constant.KylinHttpResponseClusterConstants;
import com.hnkylin.cloud.manage.entity.LoginUserVo;
import com.hnkylin.cloud.manage.entity.mc.req.CommonPageParamReq;
import com.hnkylin.cloud.manage.entity.mc.req.ListPortGroupParam;
import com.hnkylin.cloud.manage.entity.mc.req.McCheckClusterNameAndPasswordParamReq;
import com.hnkylin.cloud.manage.entity.mc.resp.*;
import com.hnkylin.cloud.manage.entity.req.cluster.*;
import com.hnkylin.cloud.manage.entity.req.workorder.QueryPortGroupParam;
import com.hnkylin.cloud.manage.entity.req.zone.BaseZoneParam;
import com.hnkylin.cloud.manage.entity.resp.cluster.*;
import com.hnkylin.cloud.manage.entity.mc.resp.McServerVmUseRatioData;
import com.hnkylin.cloud.manage.enums.ClusterStatus;
import com.hnkylin.cloud.manage.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.swing.text.html.parser.Entity;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Slf4j
public class ClusterServiceImpl implements ClusterService {

    @Resource
    private McHttpService mcHttpService;

    @Resource
    private MCConfigProperties mcConfigProperties;

    @Resource
    private CloudClusterService cloudClusterService;


    @Resource
    private CloudClusterNodeService cloudClusterNodeService;

    @Resource
    private CloudZoneClusterService cloudZoneClusterService;

    @Resource
    private McClusterThreadService mcClusterThreadService;


    @Resource
    private McServerVmService mcServerVmService;

    @Resource
    private McNodeService mcNodeService;

    @Resource
    private UserService userService;

    @Resource
    private VdcService vdcService;

    @Resource
    private ZoneService zoneService;

    @Resource
    private CloudUserMachineService cloudUserMachineService;

    @Override
    public BaseResult<String> checkClusterUserNameAndPassword(CheckClusterNameAndPasswordParam checkClusterNameAndPasswordParam, LoginUserVo loginUserVo) {

        McCheckClusterNameAndPasswordParamReq mcCheckClusterNameAndPasswordParamReq =
                new McCheckClusterNameAndPasswordParamReq();
        mcCheckClusterNameAndPasswordParamReq.setClusterAdminName(checkClusterNameAndPasswordParam.getClusterAdminName());
        try {
            mcCheckClusterNameAndPasswordParamReq.setClusterAdminPassword(AESUtil.obfuscate(checkClusterNameAndPasswordParam.getClusterAdminPassword()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        List<String> mcNodeList = new ArrayList<>();
        for (ClusterNodeParams nodeParams : checkClusterNameAndPasswordParam.getClusterNodeList()) {
            StringBuilder sb = new StringBuilder(nodeParams.getHttpType());
            sb.append(nodeParams.getIpAddress()).append(":").append(nodeParams.getPort());
            mcNodeList.add(sb.toString());
        }
        try {
            MCResponseData<Object> mcResponse =
                    mcHttpService.noCacheHasDataMcRequest(mcCheckClusterNameAndPasswordParamReq, mcNodeList,
                            mcConfigProperties.getCheckClusterUserNameAndPasswordUrl(), loginUserVo.getUserName(), 0);
            if (Objects.nonNull(mcResponse) && Objects.equals(MCServerVmConstants.SUCCESS, mcResponse.getStatus())) {
                CheckClusterNameAndPasswordResp checkClusterNameAndPasswordResp =
                        JSONObject.parseObject(JSON.toJSONString(mcResponse.getData()),
                                CheckClusterNameAndPasswordResp.class);
                if (Objects.nonNull(checkClusterNameAndPasswordParam) && checkClusterNameAndPasswordResp.getUserNameAndPassword()) {
                    return BaseResult.success("success");
                }
                return BaseResult.error(KylinHttpResponseClusterConstants.CLUSTER_NAME_AND_PASSWORD_ERR);
            }
            return BaseResult.error(KylinHttpResponseClusterConstants.CLUSTER_NODE_NOT_VISIT);
        } catch (KylinException kylinException) {
            log.error("checkClusterUserNameAndPassword-,cluster not exist");
            return BaseResult.error(KylinHttpResponseClusterConstants.CLUSTER_NODE_NOT_VISIT);
        }

    }

    @Override
    public PageData<McClusterStorageResp> pageStorage(ClusterStoragePageParam clusterStoragePageParam,
                                                      LoginUserVo loginUserVo) {
        CommonPageParamReq commonPageParamReq = new CommonPageParamReq();
        commonPageParamReq.setPage(clusterStoragePageParam.getPageNo());
        commonPageParamReq.setRows(clusterStoragePageParam.getPageSize());

        MCResponseData<Object> mcResponse = null;
        List<String> mcNodeList = new ArrayList<>();
        if (Objects.nonNull(clusterStoragePageParam.getClusterNodeList()) && !clusterStoragePageParam.getClusterNodeList().isEmpty()) {
            //新增,编辑 集群时
            for (ClusterNodeParams nodeParams : clusterStoragePageParam.getClusterNodeList()) {
                StringBuilder sb = new StringBuilder(nodeParams.getHttpType());
                sb.append(nodeParams.getIpAddress()).append(":").append(nodeParams.getPort());
                mcNodeList.add(sb.toString());
            }
            mcResponse = mcHttpService.noCacheHasDataMcRequest(commonPageParamReq, mcNodeList,
                    mcConfigProperties.getPageStorageUrl(), loginUserVo.getUserName(), 0);
        } else if (Objects.nonNull(clusterStoragePageParam.getClusterId()) && clusterStoragePageParam.getClusterId() > 0) {
            mcResponse = mcHttpService.hasDataCommonMcRequest(clusterStoragePageParam.getClusterId(),
                    commonPageParamReq, mcConfigProperties.getPageStorageUrl(), loginUserVo.getUserName(), 0);
        }

        if (Objects.nonNull(mcResponse) && Objects.equals(MCServerVmConstants.SUCCESS, mcResponse.getStatus())) {
            String serverListTxt = JSON.toJSONString(mcResponse.getData());
            McPageResp<McClusterStorageResp> mcPageResp = JSONObject.parseObject(serverListTxt, new
                    TypeReference<McPageResp<McClusterStorageResp>>() {
                    });

            McPageInfo mcPageInfo = new McPageInfo();
            mcPageInfo.setPager(mcPageResp.getPager());
            mcPageInfo.setPageSize(clusterStoragePageParam.getPageSize());
            mcPageInfo.setRecords(mcPageResp.getRecords());
            mcPageInfo.setTotal(mcPageResp.getTotal());
            return new PageData(mcPageInfo, mcPageResp.getRows());
        }
        return new PageData(null);
    }


    @Override
    @Transactional
    public void creatCluster(CreateClusterParam createClusterParam, LoginUserVo loginUserVo) {

        if (Objects.isNull(createClusterParam.getClusterNodeList()) || createClusterParam.getClusterNodeList().isEmpty()) {
            throw new KylinException(KylinHttpResponseClusterConstants.CLUSTER_NODE_IS_NOT_EMPTY);
        }

        //先查询是否重名
        checkIfExistClusterName(createClusterParam.getName());

        //检查集群节点是否已经存在
        checkIfExistClusterNode(createClusterParam.getClusterNodeList(), new ArrayList<>());

        Date now = new Date();
        CloudClusterDo clusterDo = new CloudClusterDo();
        BeanUtils.copyProperties(createClusterParam, clusterDo);
        clusterDo.setCreateBy(loginUserVo.getUserId());
        clusterDo.setCreateTime(new Date());
        try {
            String password = AESUtil.obfuscate(createClusterParam.getClusterAdminPassword());
            clusterDo.setClusterAdminPassword(password);
        } catch (Exception e) {
            e.printStackTrace();
        }
        cloudClusterService.save(clusterDo);
        //获取mc物理主机列表,获取CM/CM_VDI节点,防止在页面少输入CM/CM_VDI节点
        handleRealMasterNode(createClusterParam.getClusterNodeList(), loginUserVo, clusterDo.getId());
    }

    /**
     * 获取mc可以成为主CM的节点
     *
     * @param mcNodeList
     * @param loginUserVo
     * @return
     */
    private List<String> getClusterMasterNodeList(List<String> mcNodeList, LoginUserVo loginUserVo) {
        List<String> mcMasterNodeList = new ArrayList<>();
        CommonPageParamReq commonPageParamReq = new CommonPageParamReq();
        commonPageParamReq.setPage(KylinCommonConstants.FIRST_PAGE);
        commonPageParamReq.setRows(KylinCommonConstants.MAX_CLUSTER_MASTER_NODE_NUM);
        MCResponseData<Object> mcResponse =
                mcHttpService.noCacheHasDataMcRequest(commonPageParamReq, mcNodeList,
                        mcConfigProperties.getPagePhysicalHostUrl(), loginUserVo.getUserName(), 0);
        if (Objects.nonNull(mcResponse) && Objects.equals(MCServerVmConstants.SUCCESS, mcResponse.getStatus())) {
            String hostList = JSON.toJSONString(mcResponse.getData());

            McPageResp<McClusterPhysicalHostResp> mcPageResp = JSONObject.parseObject(hostList, new
                    TypeReference<McPageResp<McClusterPhysicalHostResp>>() {
                    });
            List<McClusterPhysicalHostResp> mcPhysicalHostRespList = mcPageResp.getRows();
            mcPhysicalHostRespList.forEach(item -> {
                if (Objects.equals(item.getServerType(), McKsvdServerType.CM.name()) ||
                        Objects.equals(item.getServerType(), McKsvdServerType.CM_VDI.name())) {
                    mcMasterNodeList.add(item.getServerAddr());
                }
            });
        }
        return mcMasterNodeList;

    }

    /**
     * 真实的mc主节点处理
     *
     * @param clusterNodeList
     * @param loginUserVo
     * @param clusterId
     */
    private void handleRealMasterNode(List<ClusterNodeParams> clusterNodeList,
                                      LoginUserVo loginUserVo, Integer clusterId) {


        List<String> mcNodeList = new ArrayList<>();
        clusterNodeList.stream().forEach(item -> {
            StringBuilder sb = new StringBuilder(item.getHttpType());
            sb.append(item.getIpAddress()).append(":").append(item.getPort());
            mcNodeList.add(sb.toString());
        });

        //获取mc物理主机列表,获取CM/CM_VDI节点,防止在页面少输入CM/CM_VDI节点
        List<String> mcMasterNodeList = getClusterMasterNodeList(mcNodeList, loginUserVo);
        List<ClusterNodeParams> mcRealNodeMasterList = new ArrayList<>(mcMasterNodeList.size());
        List<String> cacheMcNodeList = new ArrayList<>(mcMasterNodeList.size());
        if (!mcMasterNodeList.isEmpty()) {
            mcMasterNodeList.forEach(masterIp -> {
                ClusterNodeParams clusterNodeParams = new ClusterNodeParams();
                clusterNodeParams.setHttpType(HttpTypes.HTTPS.getValue());
                clusterNodeParams.setIpAddress(masterIp);
                clusterNodeParams.setPort(KylinCommonConstants.MC_MASTER_PORT);
                mcRealNodeMasterList.add(clusterNodeParams);

                StringBuilder sb = new StringBuilder(HttpTypes.HTTPS.getValue());
                sb.append(masterIp).append(":").append(KylinCommonConstants.MC_MASTER_PORT);
                cacheMcNodeList.add(sb.toString());
            });
        }

        List<CloudClusterNodeDo> insertNodeList = createNodeList(mcRealNodeMasterList,
                clusterId, new Date(), loginUserVo.getUserId());

        if (!insertNodeList.isEmpty()) {
            cloudClusterNodeService.saveBatch(insertNodeList);
        }

        if (!mcNodeList.isEmpty()) {
            mcNodeService.deleteMcLeaderUrlToCache(clusterId);
            mcNodeService.setMcNodeListToCache(cacheMcNodeList, clusterId);
        }
    }


    /**
     * 检查是否存在同名集群
     */
    private void checkIfExistClusterName(String clusterName) {
        //判断名称是否已经存在
        CloudClusterDo clusterDo = new CloudClusterDo();
        clusterDo.setName(clusterName);
        clusterDo.setDeleteFlag(false);
        Wrapper<CloudClusterDo> wrapper = new QueryWrapper<>(clusterDo);
        List<CloudClusterDo> clusterDoList = cloudClusterService.getBaseMapper().selectList(wrapper);
        if (!clusterDoList.isEmpty()) {
            throw new KylinException(KylinHttpResponseClusterConstants.EXIST_CLUSTER_NAME);
        }
    }

    /**
     * 变更集群时检查集群节点是否已经存在
     */
    private void checkIfExistClusterNode(List<ClusterNodeParams> clusterNodeList,
                                         List<CloudClusterNodeDo> clusterHasNodeList) {
        CloudClusterNodeDo clusterNodeDo = new CloudClusterNodeDo();
        clusterNodeDo.setDeleteFlag(false);
        Wrapper<CloudClusterNodeDo> wrapper = new QueryWrapper<>(clusterNodeDo);
        List<CloudClusterNodeDo> nodeDoList = cloudClusterNodeService.getBaseMapper().selectList(wrapper);

        //数据库中所有的ip节点
        List<String> allIpAddressList =
                nodeDoList.stream().map(CloudClusterNodeDo::getIpAddress).collect(Collectors.toList());
        //新增的IP节点
        List<String> insertIpAddressList =
                clusterNodeList.stream().map(ClusterNodeParams::getIpAddress).collect(Collectors.toList());
        //集群已经已经拥有额节点
        List<String> clusterAlreadyHasIpList =
                clusterHasNodeList.stream().map(CloudClusterNodeDo::getIpAddress).collect(Collectors.toList());

        insertIpAddressList.forEach(item -> {
            if (!clusterAlreadyHasIpList.contains(item)) {
                if (allIpAddressList.contains(item)) {
                    throw new KylinException(KylinHttpResponseClusterConstants.CLUSTER_NODE_IS_EXIST);
                }
            }
        });
    }

    @Override
    @Transactional
    public void modifyCluster(ModifyClusterParam modifyClusterParam, LoginUserVo loginUserVo) {

        if (Objects.isNull(modifyClusterParam.getClusterNodeList()) || modifyClusterParam.getClusterNodeList().isEmpty()) {
            throw new KylinException(KylinHttpResponseClusterConstants.CLUSTER_NODE_IS_NOT_EMPTY);
        }

        CloudClusterDo clusterDo = cloudClusterService.getById(modifyClusterParam.getClusterId());
        if (!Objects.equals(clusterDo.getName(), modifyClusterParam.getName())) {
            checkIfExistClusterName(modifyClusterParam.getName());
        }

        List<CloudClusterNodeDo> clusterNodeDoList =
                cloudClusterNodeService.getClusterNodeListByClusterId(modifyClusterParam.getClusterId());


        //查询集群节点是否已经存在
        checkIfExistClusterNode(modifyClusterParam.getClusterNodeList(), clusterNodeDoList);

        //获取当前主节点，当前主节点不能减少
        String mcLeaderUrl = mcNodeService.getMcLeaderUrlFromCache(modifyClusterParam.getClusterId());
        canNotDeleteNowMasterNode(mcLeaderUrl, modifyClusterParam.getClusterNodeList());

        Date now = new Date();
        clusterDo.setName(modifyClusterParam.getName());
        clusterDo.setRemark(modifyClusterParam.getRemark());
        clusterDo.setClusterAdminName(modifyClusterParam.getClusterAdminName());
        if (modifyClusterParam.getIfModifyPassword()) {
            try {
                String password = AESUtil.obfuscate(modifyClusterParam.getClusterAdminPassword());
                clusterDo.setClusterAdminPassword(password);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        clusterDo.setUpdateBy(loginUserVo.getUserId());
        clusterDo.setUpdateTime(now);
        try {
            if (!Objects.equals(modifyClusterParam.getClusterAdminPassword(), clusterDo.getClusterAdminPassword())) {
                String password = AESUtil.obfuscate(modifyClusterParam.getClusterAdminPassword());
                clusterDo.setClusterAdminPassword(password);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        cloudClusterService.updateById(clusterDo);


        //先把原来的节点删除
        clusterNodeDoList.stream().forEach(item -> {
            item.setDeleteFlag(Boolean.TRUE);
            item.setDeleteBy(loginUserVo.getUserId());
            item.setDeleteTime(now);
        });
        if (!clusterNodeDoList.isEmpty()) {
            cloudClusterNodeService.updateBatchById(clusterNodeDoList);
        }
        handleRealMasterNode(modifyClusterParam.getClusterNodeList(), loginUserVo, clusterDo.getId());

    }


    /**
     * 检验当前主节点不能减少。
     */
    private void canNotDeleteNowMasterNode(String mcLeaderUrl, List<ClusterNodeParams> clusterNodeList) {
        //获取主节点Ip
        if (Objects.nonNull(mcLeaderUrl)) {
            String[] masterIpInfo = mcLeaderUrl.split("//");
            String masterIp = masterIpInfo[1].split(":")[0];

            ClusterNodeParams masterNode = clusterNodeList.stream().filter(item -> Objects.equals(item.getIpAddress(),
                    masterIp)).findFirst().orElse(null);
            if (Objects.isNull(masterNode)) {
                throw new KylinException(KylinHttpResponseClusterConstants.MASTER_NODE_CANNOT_DELETE);
            }

        }
    }

    @Override
    @Transactional
    public void deleteCluster(BaseClusterParam baseClusterParam, LoginUserVo loginUserVo) {
        //限制条件：
        //1：改集群下没有云服务器
        CloudUserMachineDo cloudUserMachineDo = new CloudUserMachineDo();
        cloudUserMachineDo.setDeadlineFlag(false);
        cloudUserMachineDo.setClusterId(baseClusterParam.getClusterId());
        Wrapper<CloudUserMachineDo> wrapper = new QueryWrapper<>(cloudUserMachineDo);
        int clusterMachine = cloudUserMachineService.count(wrapper);
        if (clusterMachine > 0) {
            throw new KylinException(KylinHttpResponseClusterConstants.CLUSTER_HAS_MACHINE_NOT_DELETE);
        }
        //2:没有可用区绑定该集群
        CloudZoneClusterDo cloudZoneClusterDo = cloudZoneClusterService.getByClusterId(baseClusterParam.getClusterId());
        if (Objects.nonNull(cloudZoneClusterDo)) {
            throw new KylinException(KylinHttpResponseClusterConstants.ZONE_BIND_CLUSTER_NOT_DELETE);
        }
        CloudClusterDo clusterDo = cloudClusterService.getById(baseClusterParam.getClusterId());
        clusterDo.setDeleteFlag(true);
        clusterDo.setDeleteBy(loginUserVo.getUserId());
        clusterDo.setDeleteTime(new Date());
        cloudClusterService.updateById(clusterDo);

        //删除集群节点数据
        List<CloudClusterNodeDo> clusterNodeDoList =
                cloudClusterNodeService.getClusterNodeListByClusterId(baseClusterParam.getClusterId());
        clusterNodeDoList.forEach(item -> {
            item.setDeleteFlag(true);
            item.setDeleteBy(loginUserVo.getUserId());
            item.setDeleteTime(new Date());
        });
        if (!clusterNodeDoList.isEmpty()) {
            cloudClusterNodeService.updateBatchById(clusterNodeDoList);
        }


        //删除缓存数据
        mcNodeService.deleteMcLeaderUrlToCache(baseClusterParam.getClusterId());
        mcNodeService.deleteMcNodeListToCache(baseClusterParam.getClusterId());


    }


    @Override
    public LoginClusterRespDto getQuickLoginUrl(BaseClusterParam baseClusterParam, LoginUserVo loginUserVo) {
        LoginClusterRespDto loginClusterRespDto = new LoginClusterRespDto();

        CloudClusterDo clusterDo = cloudClusterService.getById(baseClusterParam.getClusterId());

        String mcLeaderUrl = mcNodeService.getMcLeaderUrlFromCache(baseClusterParam.getClusterId());
        String quickLoginUrl =
                mcLeaderUrl + mcConfigProperties.getMcPrefix() + mcConfigProperties.getMcQuickLogin()
                        + "?username=" + clusterDo.getClusterAdminName() + "&password=" + clusterDo.getClusterAdminPassword();
        loginClusterRespDto.setQuickLoginUrl(quickLoginUrl);

        return loginClusterRespDto;
    }

    @Override
    public PageData<PageClusterDetailDto> pageCluster(ClusterPageParam clusterPageParam, LoginUserVo loginUserVo) {

        List<CloudClusterDo> userVisibleClusterList = visibleClusterByUserId(loginUserVo.getUserId());
        if (userVisibleClusterList.isEmpty()) {
            return new PageData<>(null);
        }
        CloudClusterDo queryClusterDo = new CloudClusterDo();
        queryClusterDo.setDeleteFlag(false);
        QueryWrapper<CloudClusterDo> wrapper = new QueryWrapper<>(queryClusterDo);
        wrapper.in("id", userVisibleClusterList.stream().map(CloudClusterDo::getId).collect(Collectors.toList()));
        PageHelper.startPage(clusterPageParam.getPageNo(), clusterPageParam.getPageSize());
        List<CloudClusterDo> clusterDoList = cloudClusterService.getBaseMapper().selectList(wrapper);
        PageInfo<CloudClusterDo> pageInfo = new PageInfo<>(clusterDoList);
        List<PageClusterDetailDto> pageClusterDetailList = createPageClusterDetailList(clusterDoList, loginUserVo);
        PageData pageData = new PageData(pageInfo);
        pageData.setList(pageClusterDetailList);
        return pageData;
    }


    /**
     * 创建集群分页实体
     *
     * @param clusterDoList
     * @param loginUserVo
     * @return
     */
    private List<PageClusterDetailDto> createPageClusterDetailList(List<CloudClusterDo> clusterDoList,
                                                                   LoginUserVo loginUserVo) {
        List<PageClusterDetailDto> pageClusterDetailList = new ArrayList<>();

        if (!clusterDoList.isEmpty()) {
            //获取mc资源情况
            List<McClusterBaseResource> mcClusterBaseResourceList = moreClusterGetMcResource(clusterDoList,
                    loginUserVo.getUserName());
            clusterDoList.forEach(clusterDo -> {
                PageClusterDetailDto clusterRespDto = new PageClusterDetailDto();
                BeanUtils.copyProperties(clusterDo, clusterRespDto);
                clusterRespDto.setClusterId(clusterDo.getId());
                List<String> mcNodeList = cloudClusterService.formatClusterNodeList(clusterDo.getId());
                clusterRespDto.setClusterUrl(mcNodeList.get(0) + mcConfigProperties.getMcPrefix());
                CloudZoneDo zoneDo = zoneService.getZoneByClusterId(clusterDo.getId());
                if (Objects.nonNull(zoneDo)) {
                    clusterRespDto.setZoneName(zoneDo.getName());
                    clusterRespDto.setZoneId(zoneDo.getId());
                }

                McClusterBaseResource mcClusterBaseResource =
                        mcClusterBaseResourceList.stream().filter(item -> Objects.equals(item.getClusterId(),
                                clusterDo.getId())).findFirst().orElse(null);
                if (Objects.nonNull(mcClusterBaseResource)) {
                    clusterRespDto.setStatus(ClusterStatus.ONLINE);
                    String quickLoginUrl =
                            mcNodeList.get(0) + mcConfigProperties.getMcPrefix() + mcConfigProperties.getMcQuickLogin()
                                    + "?username=" + clusterDo.getClusterAdminName() + "&password=" + clusterDo.getClusterAdminPassword();
                    clusterRespDto.setQuickLoginUrl(quickLoginUrl);
                    if (Objects.nonNull(mcClusterBaseResource.getResourceInfo())) {
                        clusterRespDto.setCpuTotal(mcClusterBaseResource.getResourceInfo().getCpuTotal());
                        clusterRespDto.setCpuUsed(mcClusterBaseResource.getResourceInfo().getCpuUsed());
                        clusterRespDto.setMemTotal(mcClusterBaseResource.getResourceInfo().getMemTotal());
                        clusterRespDto.setMemUsed(mcClusterBaseResource.getResourceInfo().getMemUsed());
                        clusterRespDto.setStorageTotal(mcClusterBaseResource.getResourceInfo().getStorageTotal());
                        clusterRespDto.setStorageUsed(mcClusterBaseResource.getResourceInfo().getStorageUsed());
                    }
                    if (Objects.nonNull(mcClusterBaseResource.getLicenseExpire())) {
                        if (judgeLicenseExpire(mcClusterBaseResource.getLicenseExpire())) {
                            clusterRespDto.setStatus(ClusterStatus.EXCEPTION);
                        }
                    }

                }
                pageClusterDetailList.add(clusterRespDto);
            });
        }

        return pageClusterDetailList;
    }


    /**
     * 判断集群是否过期
     *
     * @param licenseExpire
     * @return
     */
    private boolean judgeLicenseExpire(String licenseExpire) {

        Long licenseExpireTime =
                DateUtils.parse(licenseExpire + DateUtils.DAY_END, DateUtils.DATE_ALL_PATTEN).getTime();
        Long now = System.currentTimeMillis();

        return licenseExpireTime < now;
    }


    /**
     * 获取mc集群资源使用情况
     *
     * @param loginUserName
     * @return
     */
    @Override
    public McClusterBaseResource getMcBaseResource(Integer clusterId, String loginUserName) {
        try {
            MCResponseData<Object> mcResponse = mcHttpService.hasDataCommonMcRequest(clusterId, null,
                    mcConfigProperties.getMcClusterBaseResourceUrl(), loginUserName, 0);
            McClusterBaseResource mcClusterBaseResource = null;
            if (Objects.nonNull(mcResponse) && Objects.equals(MCServerVmConstants.SUCCESS, mcResponse.getStatus())) {
                String mcClusterInfoStr = JSON.toJSONString(mcResponse.getData());
                mcClusterBaseResource = JSON.parseObject(mcClusterInfoStr, McClusterBaseResource.class);

            }
            return mcClusterBaseResource;
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return null;

    }

    @Override
    public List<McClusterBaseResource> moreClusterGetMcResource(List<CloudClusterDo> clusterDoList,
                                                                String loginUserName) {

        List<String> resultList =
                mcClusterThreadService.threadGetMcResponse(clusterDoList.stream().map(CloudClusterDo::getId).collect(Collectors.toList()),
                        loginUserName,
                        mcConfigProperties.getMcClusterBaseResourceUrl(), new ArrayList<>());
        List<McClusterBaseResource> mcClusterBaseResourceList = new ArrayList<>();
        Stream.iterate(0, i -> i + 1).limit(clusterDoList.size()).forEach(i -> {
            String mcClusterResource = resultList.get(i);
            if (Objects.nonNull(mcClusterResource)) {
                McClusterBaseResource mcClusterBaseResource = JSON.parseObject(mcClusterResource,
                        McClusterBaseResource.class);
                mcClusterBaseResource.setClusterId(clusterDoList.get(i).getId());
                mcClusterBaseResourceList.add(mcClusterBaseResource);
            }
        });
        return mcClusterBaseResourceList;
    }

    @Override
    public List<McServerVmRunningInfoDto> moreClusterServerVmRunningInfo(List<Object> serverVmUuidList,
                                                                         List<CloudClusterDo> clusterDoList,
                                                                         String loginUserName) {
        List<String> resultList =
                mcClusterThreadService.threadGetMcResponse(clusterDoList.stream().map(CloudClusterDo::getId).collect(Collectors.toList()),
                        loginUserName,
                        mcConfigProperties.getServerVmRunningInfoUrl(), serverVmUuidList);
        List<McServerVmRunningInfoDto> mcServerVmRunningInfoList = new ArrayList<>();
        Stream.iterate(0, i -> i + 1).limit(clusterDoList.size()).forEach(i -> {
            String mcServerVmRunningInfoStr = resultList.get(i);
            if (Objects.nonNull(mcServerVmRunningInfoStr)) {
                McServerVmRunningInfoDto mcServerVmRunningInfoDto = JSON.parseObject(mcServerVmRunningInfoStr,
                        McServerVmRunningInfoDto.class);
                if (Objects.nonNull(mcServerVmRunningInfoDto)) {
                    mcServerVmRunningInfoDto.getCpuTopList().forEach(item -> {
                        item.setClusterName(clusterDoList.get(i).getName());
                    });
                    mcServerVmRunningInfoDto.getMemTopList().forEach(item -> {
                        item.setClusterName(clusterDoList.get(i).getName());
                    });
                    mcServerVmRunningInfoList.add(mcServerVmRunningInfoDto);
                }

            }
        });
        return mcServerVmRunningInfoList;
    }

    @Override
    public List<McClusterPhysicalHostResp> moreClusterPhysicalHost(List<CloudClusterDo> clusterDoList,
                                                                   String loginUserName) {

        List<Object> mcRequestObjectList = new ArrayList<>();
        clusterDoList.forEach(clusterId -> {

            CommonPageParamReq commonPageParamReq = new CommonPageParamReq();
            commonPageParamReq.setPage(KylinCommonConstants.FIRST_PAGE);
            commonPageParamReq.setRows(KylinCommonConstants.DEFAULT_MAX_SIZE);
            mcRequestObjectList.add(commonPageParamReq);
        });
        List<String> resultList =
                mcClusterThreadService.threadGetMcResponse(clusterDoList.stream().map(CloudClusterDo::getId).collect(Collectors.toList()),
                        loginUserName,
                        mcConfigProperties.getPagePhysicalHostUrl(), mcRequestObjectList);

        List<McClusterPhysicalHostResp> totalClusterPhysicalHostList = new ArrayList<>();
        Stream.iterate(0, i -> i + 1).limit(clusterDoList.size()).forEach(i -> {
            String clusterPhysicalHostStr = resultList.get(i);
            if (Objects.nonNull(clusterPhysicalHostStr)) {

                McPageResp<McClusterPhysicalHostResp> mcPageResp = JSONObject.parseObject(clusterPhysicalHostStr, new
                        TypeReference<McPageResp<McClusterPhysicalHostResp>>() {
                        });


                if (Objects.nonNull(mcPageResp) && Objects.nonNull(mcPageResp.getRows()) && !mcPageResp.getRows().isEmpty()) {
                    totalClusterPhysicalHostList.addAll(mcPageResp.getRows());
                }
            }
        });
        return totalClusterPhysicalHostList;
    }

    @Override
    public ClusterInfoDto clusterInfo(BaseClusterParam baseClusterParam, LoginUserVo loginUserVo) {
        CloudClusterDo clusterDo = cloudClusterService.getById(baseClusterParam.getClusterId());
        ClusterInfoDto clusterInfoDto = new ClusterInfoDto();
        clusterInfoDto.setName(clusterDo.getName());
        clusterInfoDto.setRemark(clusterDo.getRemark());
        clusterInfoDto.setClusterAdminUser(clusterDo.getClusterAdminName());
        CloudZoneDo zoneDo = zoneService.getZoneByClusterId(baseClusterParam.getClusterId());
        if (Objects.nonNull(zoneDo)) {
            clusterInfoDto.setZoneName(zoneDo.getName());
            clusterInfoDto.setZoneId(zoneDo.getId());
        }
        clusterInfoDto.setStatus(ClusterStatus.OFFLINE);
        clusterInfoDto.setType(clusterDo.getType());
        clusterInfoDto.setCreateTime(DateUtils.format(clusterDo.getCreateTime(), DateUtils.DATE_ALL_PATTEN));

        List<String> mcNodeList = cloudClusterService.formatClusterNodeList(clusterDo.getId());
        clusterInfoDto.setClusterUrl(mcNodeList.get(0) + mcConfigProperties.getMcPrefix());

        McClusterBaseResource mcClusterBaseResource = getMcClusterBaseResource(clusterDo, loginUserVo.getUserName());

        if (Objects.nonNull(mcClusterBaseResource)) {
            McClusterResourceUsedResp resourceUsedResp = mcClusterBaseResource.getResourceInfo();
            resourceUsedResp.reCalculate();
            clusterInfoDto.setResourceUsedInfo(resourceUsedResp);

            clusterInfoDto.setStatus(ClusterStatus.ONLINE);
            clusterInfoDto.setClusterVersion(mcClusterBaseResource.getKsvdVersion());
            String quickLoginUrl =
                    mcNodeList.get(0) + mcConfigProperties.getMcPrefix() + mcConfigProperties.getMcQuickLogin()
                            + "?username=" + clusterDo.getClusterAdminName() + "&password=" + clusterDo.getClusterAdminPassword();
            clusterInfoDto.setQuickLoginUrl(quickLoginUrl);
        }
        if (Objects.nonNull(mcClusterBaseResource.getLicenseExpire())) {
            if (judgeLicenseExpire(mcClusterBaseResource.getLicenseExpire())) {
                clusterInfoDto.setStatus(ClusterStatus.EXCEPTION);
            }
        }

        return clusterInfoDto;
    }


    @Override
    public PageData<McClusterPhysicalHostResp> pagePhysicalHost(ClusterPhysicalHostPageParam clusterPhysicalHostPageParam, LoginUserVo loginUserVo) {
        CommonPageParamReq commonPageParamReq = new CommonPageParamReq();
        commonPageParamReq.setPage(clusterPhysicalHostPageParam.getPageNo());
        commonPageParamReq.setRows(clusterPhysicalHostPageParam.getPageSize());


        MCResponseData<Object> mcResponse =
                mcHttpService.hasDataCommonMcRequest(clusterPhysicalHostPageParam.getClusterId(), commonPageParamReq,
                        mcConfigProperties.getPagePhysicalHostUrl(), loginUserVo.getUserName(), 0);
        if (Objects.nonNull(mcResponse) && Objects.equals(MCServerVmConstants.SUCCESS, mcResponse.getStatus())) {
            String hostList = JSON.toJSONString(mcResponse.getData());

            McPageResp<McClusterPhysicalHostResp> mcPageResp = JSONObject.parseObject(hostList, new
                    TypeReference<McPageResp<McClusterPhysicalHostResp>>() {
                    });

            McPageInfo mcPageInfo = new McPageInfo();
            mcPageInfo.setPager(mcPageResp.getPager());
            mcPageInfo.setPageSize(clusterPhysicalHostPageParam.getPageSize());
            mcPageInfo.setRecords(mcPageResp.getRecords());
            mcPageInfo.setTotal(mcPageResp.getTotal());
            return new PageData(mcPageInfo, mcPageResp.getRows());
        }
        return new PageData(null);
    }

    @Override
    public ClusterDetailDto clusterDetail(BaseClusterParam baseClusterParam, LoginUserVo loginUserVo) {
        CloudClusterDo clusterDo = cloudClusterService.getById(baseClusterParam.getClusterId());
        ClusterDetailDto clusterDetailDto = new ClusterDetailDto();

        BeanUtils.copyProperties(clusterDo, clusterDetailDto);
        clusterDetailDto.setClusterId(clusterDo.getId());
        List<CloudClusterNodeDo> clusterNodeDoList =
                cloudClusterNodeService.getClusterNodeListByClusterId(baseClusterParam.getClusterId());
        List<ClusterNodeInfoDto> clusterNodeList = new ArrayList<>(clusterNodeDoList.size());
        clusterNodeDoList.stream().forEach(item -> {
            ClusterNodeInfoDto nodeInfoDto = new ClusterNodeInfoDto();
            BeanUtils.copyProperties(item, nodeInfoDto);
            clusterNodeList.add(nodeInfoDto);
        });
        clusterDetailDto.setClusterNodeList(clusterNodeList);

        return clusterDetailDto;
    }


    /**
     * 创建集群节点列表
     *
     * @param clusterNodeList
     * @param clusterId
     * @param now
     * @param createUserId
     * @return
     */
    private List<CloudClusterNodeDo> createNodeList(List<ClusterNodeParams> clusterNodeList, Integer clusterId,
                                                    Date now, Integer createUserId) {
        List<CloudClusterNodeDo> nodeDoList = new ArrayList<>();
        clusterNodeList.stream().forEach(item -> {
            CloudClusterNodeDo clusterNodeDo = new CloudClusterNodeDo();
            BeanUtils.copyProperties(item, clusterNodeDo);
            clusterNodeDo.setClusterId(clusterId);
            clusterNodeDo.setCreateTime(now);
            clusterNodeDo.setCreateBy(createUserId);
            nodeDoList.add(clusterNodeDo);
        });
        return nodeDoList;
    }


    @Override
    public List<PageClusterDetailDto> canBindCluster(CanBindClusterParam canBindClusterParam, LoginUserVo loginUserVo) {
        //获取可用区可绑定的物理集群
        //相同的资源类型，排除已经被其他可用区绑定的物理集群


        //查询所有的相同资源的物理集群
        CloudClusterDo queryClusterDo = new CloudClusterDo();
        queryClusterDo.setDeleteFlag(false);
        queryClusterDo.setType(canBindClusterParam.getType());
        Wrapper<CloudClusterDo> wrapper = new QueryWrapper<>(queryClusterDo);
        List<CloudClusterDo> clusterDoList = cloudClusterService.getBaseMapper().selectList(wrapper);


        //获取已经被绑定过得物理集群
        CloudZoneClusterDo queryZoneClusterDo = new CloudZoneClusterDo();
        queryZoneClusterDo.setDeleteFlag(false);
        Wrapper<CloudZoneClusterDo> zoneClusterWrapper = new QueryWrapper<>(queryZoneClusterDo);
        List<CloudZoneClusterDo> alreadyBindClusterList =
                cloudZoneClusterService.getBaseMapper().selectList(zoneClusterWrapper);
        List<Integer> alreadyBindClusterIdList = alreadyBindClusterList.stream().map(CloudZoneClusterDo::getClusterId
        ).collect(Collectors.toList());


        //获取可以绑定的列表
        List<CloudClusterDo> canBindList =
                clusterDoList.stream().filter(item -> !alreadyBindClusterIdList.contains(item.getId())).collect(Collectors.toList());

        List<Integer> zoneAlreadyBindList = new ArrayList<>();
        if (canBindClusterParam.getZoneId() > 0) {
            //获取可用区已经绑定的物理集群
            zoneAlreadyBindList =
                    alreadyBindClusterList.stream().filter(item -> Objects.equals(item.getZoneId(),
                            canBindClusterParam.getZoneId())).map(CloudZoneClusterDo::getClusterId
                    ).collect(Collectors.toList());

            zoneAlreadyBindList.forEach(clusterId -> {
                CloudClusterDo bindClusterDo = clusterDoList.stream().filter(item -> Objects.equals(item.getId(),
                        clusterId)).findFirst().orElse(null);

                if (Objects.nonNull(bindClusterDo)) {
                    canBindList.add(0, bindClusterDo);
                }
            });
        }

        List<PageClusterDetailDto> pageClusterDetailList = createPageClusterDetailList(canBindList, loginUserVo);
        if (pageClusterDetailList.size() > 0) {
            //将当前可用区已绑定的物理集群设置成已选择
            for (PageClusterDetailDto item : pageClusterDetailList) {
                if (zoneAlreadyBindList.contains(item.getClusterId())) {
                    item.setSelected(true);
                    item.setZoneId(canBindClusterParam.getZoneId());
                } else {
                    item.setZoneId(0);
                }

            }
            pageClusterDetailList.stream().sorted(Comparator.comparing(PageClusterDetailDto::getZoneId).reversed()).collect(Collectors.toList());

        }

        return pageClusterDetailList;
    }


    @Override
    public List<PageClusterDetailDto> clusterListByZone(BaseZoneParam baseZoneParam, LoginUserVo loginUserVo) {
        List<CloudClusterDo> clusterDoList = cloudClusterService.listClusterBYZoneId(baseZoneParam.getZoneId());
        List<PageClusterDetailDto> pageClusterDetailList = createPageClusterDetailList(clusterDoList, loginUserVo);
        return pageClusterDetailList;
    }


    @Override
    public NetworkConfigResp mcClusterNetWorkConfig(BaseClusterParam baseClusterParam, LoginUserVo loginUserVo) {
        return mcServerVmService.mcClusterNetworkConfig(baseClusterParam.getClusterId(), loginUserVo);
    }

    @Override
    public List<NetworkPortGroupResp> mcPortGroupListByVirtualSwitch(QueryPortGroupParam queryPortGroupParam,
                                                                     LoginUserVo loginUserVo) {

        ListPortGroupParam listPortGroupParam = new ListPortGroupParam();
        listPortGroupParam.setVsname(queryPortGroupParam.getVirtualSwitchName());
        return mcServerVmService.mcClusterPortGroupListByVirtualSwitch(queryPortGroupParam.getClusterId(),
                listPortGroupParam, loginUserVo);
    }


    @Override
    public List<ClusterDto> zoneClusterList(Integer zoneId) {
        List<CloudClusterDo> clusterDoList = cloudClusterService.listClusterBYZoneId(zoneId);
        List<ClusterDto> zoneCusterLIst = new ArrayList<>();
        clusterDoList.forEach(item -> {
            ClusterDto clusterDto = new ClusterDto();
            clusterDto.setClusterId(item.getId());
            clusterDto.setClusterName(item.getName());
            zoneCusterLIst.add(clusterDto);
        });
        return zoneCusterLIst;
    }


    @Override
    public List<CloudClusterDo> visibleClusterByUserId(Integer userId) {


        //平台管理类型角色，
        boolean platformUser = userService.judgeIfPlatformUser(userId);
        if (platformUser) {
            CloudClusterDo clusterDo = new CloudClusterDo();
            clusterDo.setDeleteFlag(false);
            Wrapper<CloudClusterDo> wrapper = new QueryWrapper<>(clusterDo);
            return cloudClusterService.list(wrapper);
        }
        //组织管理员用户对应组织，组织对应vdc，vdc对应可用区，可用区对应的物理集群
        CloudVdcDo vdcDo = vdcService.getUserOrgBindVdc(userId);
        if (Objects.isNull(vdcDo)) {
            return new ArrayList<>();
        }
        return cloudClusterService.listClusterBYZoneId(vdcDo.getZoneId());
    }


    @Override
    public McClusterBaseResource getMcClusterBaseResource(CloudClusterDo cloudClusterDo, String loginUserName) {
        McClusterBaseResource mcClusterBaseResource = null;
        MCResponseData<Object> mcResponse = mcHttpService.hasDataCommonMcRequest(cloudClusterDo.getId(), null,
                mcConfigProperties.getMcClusterBaseResourceUrl(), loginUserName, 0);

        if (Objects.nonNull(mcResponse) && Objects.equals(MCServerVmConstants.SUCCESS, mcResponse.getStatus())) {
            String mcBaseRseourceStr = JSON.toJSONString(mcResponse.getData());
            mcClusterBaseResource = new McClusterBaseResource();
            mcClusterBaseResource = JSON.parseObject(mcBaseRseourceStr, McClusterBaseResource.class);
        }
        return mcClusterBaseResource;
    }
}
