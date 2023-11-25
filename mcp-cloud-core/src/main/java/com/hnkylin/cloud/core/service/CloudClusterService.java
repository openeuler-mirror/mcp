package com.hnkylin.cloud.core.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hnkylin.cloud.core.domain.CloudClusterDo;

import java.util.List;


public interface CloudClusterService extends IService<CloudClusterDo> {

    /**
     * 封装集群请求ip集合
     *
     * @param clusterId
     * @return
     */
    List<String> formatClusterNodeList(Integer clusterId);

    /**
     * 根据用户获取，用户拥有的集群权限
     *
     * @param userId
     * @return
     */
    List<CloudClusterDo> clusterListByUserId(Integer userId);


    /**
     * 根据可用区ID获取集群列表
     *
     * @param zoneId
     * @return
     */
    List<CloudClusterDo> listClusterBYZoneId(Integer zoneId);


}
