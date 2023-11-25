package com.hnkylin.cloud.core.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hnkylin.cloud.core.domain.CloudClusterNodeDo;
import com.hnkylin.cloud.core.mapper.CloudClusterNodeMapper;
import com.hnkylin.cloud.core.service.CloudClusterNodeService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CloudClusterNodeServiceImpl extends ServiceImpl<CloudClusterNodeMapper, CloudClusterNodeDo>
        implements CloudClusterNodeService {


    @Override
    public List<CloudClusterNodeDo> getClusterNodeListByClusterId(Integer clusterId) {
        CloudClusterNodeDo clusterNodeDo = new CloudClusterNodeDo();
        clusterNodeDo.setDeleteFlag(false);
        clusterNodeDo.setClusterId(clusterId);
        Wrapper<CloudClusterNodeDo> wrapper = new QueryWrapper<>(clusterNodeDo);
        List<CloudClusterNodeDo> clusterNodeDoList = getBaseMapper().selectList(wrapper);
        return clusterNodeDoList;
    }
}
