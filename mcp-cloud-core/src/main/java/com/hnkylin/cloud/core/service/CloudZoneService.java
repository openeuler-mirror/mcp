package com.hnkylin.cloud.core.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hnkylin.cloud.core.domain.CloudZoneDo;


public interface CloudZoneService extends IService<CloudZoneDo> {


    /**
     * 通过集群ID获取可用区
     *
     * @param clusterId
     * @return
     */
    CloudZoneDo getByClusterId(Integer clusterId);

}
