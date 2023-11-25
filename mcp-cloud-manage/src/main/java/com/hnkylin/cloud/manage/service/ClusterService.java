package com.hnkylin.cloud.manage.service;


import com.hnkylin.cloud.core.common.BaseResult;
import com.hnkylin.cloud.core.common.PageData;
import com.hnkylin.cloud.core.common.servervm.ServerVmBatchReq;
import com.hnkylin.cloud.core.domain.CloudClusterDo;
import com.hnkylin.cloud.manage.entity.LoginUserVo;
import com.hnkylin.cloud.manage.entity.mc.resp.*;
import com.hnkylin.cloud.manage.entity.req.cluster.*;
import com.hnkylin.cloud.manage.entity.req.workorder.QueryPortGroupParam;
import com.hnkylin.cloud.manage.entity.req.zone.BaseZoneParam;
import com.hnkylin.cloud.manage.entity.resp.cluster.*;
import com.hnkylin.cloud.manage.entity.mc.resp.McServerVmUseRatioData;

import java.util.List;

/**
 * 集群管理
 */
public interface ClusterService {


    /**
     * 校验集群账号密码
     *
     * @param checkClusterNameAndPasswordParam
     * @param loginUserVo
     * @return
     */
    BaseResult<String> checkClusterUserNameAndPassword(CheckClusterNameAndPasswordParam checkClusterNameAndPasswordParam,
                                                       LoginUserVo loginUserVo);

    /**
     * 分页查询数据存储列表
     *
     * @param clusterStoragePageParam
     * @param loginUserVo
     * @return
     */
    PageData<McClusterStorageResp> pageStorage(ClusterStoragePageParam clusterStoragePageParam,
                                               LoginUserVo loginUserVo);


    /**
     * 创建集群
     *
     * @param createClusterParam
     * @param loginUserVo
     */
    void creatCluster(CreateClusterParam createClusterParam, LoginUserVo loginUserVo);

    /**
     * 分页查询集群
     *
     * @param clusterPageParam
     * @param loginUserVo
     * @return
     */
    PageData<PageClusterDetailDto> pageCluster(ClusterPageParam clusterPageParam, LoginUserVo loginUserVo);

    /**
     * 获取快捷登录集群地址
     *
     * @param baseClusterParam
     * @param loginUserVo
     * @return
     */
    LoginClusterRespDto getQuickLoginUrl(BaseClusterParam baseClusterParam, LoginUserVo loginUserVo);


    /**
     * 集群详情
     */
    ClusterInfoDto clusterInfo(BaseClusterParam baseClusterParam, LoginUserVo loginUserVo);


    /**
     * 获取集群物理主机列表
     *
     * @param clusterPhysicalHostPageParam
     * @param loginUserVo
     * @return
     */
    PageData<McClusterPhysicalHostResp> pagePhysicalHost(ClusterPhysicalHostPageParam clusterPhysicalHostPageParam,
                                                         LoginUserVo loginUserVo);


    /**
     * 编辑时获取详情
     *
     * @param baseClusterParam
     * @param loginUserVo
     * @return
     */
    ClusterDetailDto clusterDetail(BaseClusterParam baseClusterParam, LoginUserVo loginUserVo);


    /**
     * 编辑集群
     *
     * @param modifyClusterParam
     * @param loginUserVo
     */
    void modifyCluster(ModifyClusterParam modifyClusterParam, LoginUserVo loginUserVo);

    /**
     * 删除集群
     *
     * @param baseClusterParam
     * @param loginUserVo
     */
    void deleteCluster(BaseClusterParam baseClusterParam, LoginUserVo loginUserVo);

    /**
     * 可用区获取可以绑定的五路集群
     *
     * @param canBindClusterParam
     * @param loginUserVo
     * @return
     */
    List<PageClusterDetailDto> canBindCluster(CanBindClusterParam canBindClusterParam, LoginUserVo loginUserVo);


    /**
     * 获取可用区-已绑定的物理集群
     *
     * @param baseZoneParam
     * @param loginUserVo
     * @return
     */
    List<PageClusterDetailDto> clusterListByZone(BaseZoneParam baseZoneParam, LoginUserVo loginUserVo);


    /**
     * 获取集群网络配置
     *
     * @param baseClusterParam
     * @param loginUserVo
     * @return
     */
    NetworkConfigResp mcClusterNetWorkConfig(BaseClusterParam baseClusterParam, LoginUserVo loginUserVo);


    /**
     * 根据虚拟交换机获取端口组
     *
     * @param queryPortGroupParam
     * @param loginUserVo
     * @return
     */
    List<NetworkPortGroupResp> mcPortGroupListByVirtualSwitch(QueryPortGroupParam queryPortGroupParam,
                                                              LoginUserVo loginUserVo);


    /**
     * 获取可用区集群列表
     *
     * @param zoneId
     * @return
     */
    List<ClusterDto> zoneClusterList(Integer zoneId);


    /**
     * 用户可见的物理集群
     *
     * @param userId
     * @return
     */
    List<CloudClusterDo> visibleClusterByUserId(Integer userId);


    /**
     * 调用mc接口获取mc集群基础资源
     *
     * @param cloudClusterDo
     * @param loginUserName
     * @return
     */
    McClusterBaseResource getMcClusterBaseResource(CloudClusterDo cloudClusterDo, String loginUserName);


    /**
     * 获取集群资源
     */
    McClusterBaseResource getMcBaseResource(Integer clusterId, String loginUserName);

    /**
     * 获取多个集群中mc的资源
     *
     * @param clusterDoList
     * @param loginUserName
     * @return
     */
    List<McClusterBaseResource> moreClusterGetMcResource(List<CloudClusterDo> clusterDoList, String loginUserName);


    /**
     * 获取多个集群中 云服务器 cpu，内存使用率TOP
     *
     * @param clusterDoList
     * @param loginUserName
     * @return
     */
    List<McServerVmRunningInfoDto> moreClusterServerVmRunningInfo(List<Object> serverVmUuidList,
                                                                  List<CloudClusterDo> clusterDoList,
                                                                  String loginUserName);

    /**
     * 获取多个集群中物理主机
     *
     * @param clusterDoList
     * @param loginUserName
     * @return
     */
    List<McClusterPhysicalHostResp> moreClusterPhysicalHost(List<CloudClusterDo> clusterDoList,
                                                            String loginUserName);


}
