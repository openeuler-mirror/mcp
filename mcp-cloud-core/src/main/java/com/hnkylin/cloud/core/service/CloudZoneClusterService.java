package com.hnkylin.cloud.core.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hnkylin.cloud.core.domain.CloudZoneClusterDo;

import java.util.List;


public interface CloudZoneClusterService extends IService<CloudZoneClusterDo> {

    /**
     * 根据集群id获取集群和可用区关联关系
     *
     * @param clusterId
     * @return
     */
    CloudZoneClusterDo getByClusterId(Integer clusterId);


    List<CloudZoneClusterDo> listZoneClusterByZone(Integer zoneId);


}
