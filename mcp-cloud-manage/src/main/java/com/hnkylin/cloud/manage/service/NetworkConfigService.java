package com.hnkylin.cloud.manage.service;

import com.hnkylin.cloud.core.common.BasePageParam;
import com.hnkylin.cloud.core.common.PageData;
import com.hnkylin.cloud.core.domain.CloudNetworkConfigDo;
import com.hnkylin.cloud.manage.entity.LoginUserVo;
import com.hnkylin.cloud.manage.entity.req.network.CreateNetworkParam;
import com.hnkylin.cloud.manage.entity.req.network.ModifyNetworkParam;
import com.hnkylin.cloud.manage.entity.resp.network.NetworkConfigRespDto;

import java.util.List;

public interface NetworkConfigService {

    /**
     * 分页查询网络设置
     */
    PageData<NetworkConfigRespDto> pageNetworkConfig(BasePageParam basePageParam, LoginUserVo loginUserVo);

    /**
     * 创建网络设置
     */
    void createNetwork(CreateNetworkParam createNetworkParam, LoginUserVo loginUserVo);


    /**
     * 获取网络配置列表
     */
    List<NetworkConfigRespDto> listNetworkListByVdcId(Integer vdcId);

    /**
     * 获取网络配置列表
     */
    List<NetworkConfigRespDto> listNetworkListByVdcIdAndClusterId(Integer vdcId, Integer clusterId);

    /**
     * 通过网络id，获取网络配置详情
     */
    CloudNetworkConfigDo getById(Integer networkId);


    /**
     * 根据VDC获取网络数量
     *
     * @param vdcId
     * @return
     */
    int getNetWorkCountByVdcId(Integer vdcId);

    /**
     * 网卡详情
     *
     * @param networkId
     * @return
     */
    NetworkConfigRespDto networkDetail(Integer networkId);

    /**
     * 变更网络
     *
     * @param modifyNetworkParam
     * @param loginUserVo
     */
    void modifyNetwork(ModifyNetworkParam modifyNetworkParam, LoginUserVo loginUserVo);


}
