package com.hnkylin.cloud.core.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hnkylin.cloud.core.domain.CloudVdcCpuDo;
import com.hnkylin.cloud.core.domain.CloudZoneClusterDo;
import com.hnkylin.cloud.core.mapper.CloudZoneClusterMapper;
import com.hnkylin.cloud.core.service.CloudZoneClusterService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CloudZoneClusterServiceImpl extends ServiceImpl<CloudZoneClusterMapper, CloudZoneClusterDo>
        implements CloudZoneClusterService {

    @Override
    public CloudZoneClusterDo getByClusterId(Integer clusterId) {
        CloudZoneClusterDo zoneClusterDo = new CloudZoneClusterDo();
        zoneClusterDo.setDeleteFlag(false);
        zoneClusterDo.setClusterId(clusterId);
        QueryWrapper<CloudZoneClusterDo> wrapper = new QueryWrapper<>(zoneClusterDo);
        List<CloudZoneClusterDo> zoneClusterList = getBaseMapper().selectList(wrapper);
        return zoneClusterList.isEmpty() ? null : zoneClusterList.get(0);

    }

    @Override
    public List<CloudZoneClusterDo> listZoneClusterByZone(Integer zoneId) {
        CloudZoneClusterDo queryZoneClusterDo = new CloudZoneClusterDo();
        queryZoneClusterDo.setDeleteFlag(false);
        queryZoneClusterDo.setZoneId(zoneId);
        Wrapper<CloudZoneClusterDo> zoneClusterWrapper = new QueryWrapper<>(queryZoneClusterDo);
        return getBaseMapper().selectList(zoneClusterWrapper);
    }
}
