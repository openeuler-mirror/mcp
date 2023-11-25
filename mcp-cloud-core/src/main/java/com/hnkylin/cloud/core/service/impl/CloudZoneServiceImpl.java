package com.hnkylin.cloud.core.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hnkylin.cloud.core.domain.CloudZoneClusterDo;
import com.hnkylin.cloud.core.domain.CloudZoneDo;
import com.hnkylin.cloud.core.mapper.CloudZoneMapper;
import com.hnkylin.cloud.core.service.CloudZoneClusterService;
import com.hnkylin.cloud.core.service.CloudZoneService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Objects;

@Service
public class CloudZoneServiceImpl extends ServiceImpl<CloudZoneMapper, CloudZoneDo>
        implements CloudZoneService {


    @Resource
    private CloudZoneClusterService zoneClusterService;

    @Override
    public CloudZoneDo getByClusterId(Integer clusterId) {
        CloudZoneClusterDo zoneClusterDo = zoneClusterService.getByClusterId(clusterId);
        return Objects.nonNull(zoneClusterDo) ? getById(zoneClusterDo.getZoneId()) : null;
    }
}
