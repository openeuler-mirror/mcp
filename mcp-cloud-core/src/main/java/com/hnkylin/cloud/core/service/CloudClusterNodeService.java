package com.hnkylin.cloud.core.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hnkylin.cloud.core.domain.CloudClusterNodeDo;

import java.util.List;


public interface CloudClusterNodeService extends IService<CloudClusterNodeDo> {


    List<CloudClusterNodeDo> getClusterNodeListByClusterId(Integer clusterId);


}
