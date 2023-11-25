package com.hnkylin.cloud.core.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hnkylin.cloud.core.common.KylinCommonConstants;
import com.hnkylin.cloud.core.domain.*;
import com.hnkylin.cloud.core.mapper.CloudClusterMapper;
import com.hnkylin.cloud.core.service.*;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class CloudClusterServiceImpl extends ServiceImpl<CloudClusterMapper, CloudClusterDo>
        implements CloudClusterService {

    @Resource
    private CloudClusterNodeService cloudClusterNodeService;

    @Resource
    private CloudUserService cloudUserService;

    @Resource
    private CloudOrganizationService organizationService;

    @Resource
    private CloudVdcService cloudVdcService;

    @Resource
    private CloudZoneClusterService cloudZoneClusterService;

    /**
     * 封装集群请求ip集合
     *
     * @param clusterId
     * @return
     */
    @Override
    public List<String> formatClusterNodeList(Integer clusterId) {

        List<CloudClusterNodeDo> clusterNodeDoList = cloudClusterNodeService.getClusterNodeListByClusterId(clusterId);
        List<String> mcNodeList = new ArrayList<>(clusterNodeDoList.size());
        clusterNodeDoList.stream().forEach(item -> {
            StringBuilder sb = new StringBuilder(item.getHttpType());
            sb.append(item.getIpAddress()).append(":").append(item.getPort());
            mcNodeList.add(sb.toString());
        });

        return mcNodeList;
    }

    @Override
    public List<CloudClusterDo> clusterListByUserId(Integer userId) {
        //先根据用户得到组织，组织获取VDC，VDC在获取可用区，可用区在获取集群列表
        CloudUserDo userDo = cloudUserService.getById(userId);
        CloudOrganizationDo organizationDo = organizationService.getById(userDo.getOrganizationId());
        if (Objects.equals(organizationDo.getParentId(), KylinCommonConstants.TOP_PARENT_ID)) {
            //用户属于顶级组织，则可见为全部集群
            CloudClusterDo clusterDo = new CloudClusterDo();
            clusterDo.setDeleteFlag(false);
            Wrapper<CloudClusterDo> wrapper = new QueryWrapper<>(clusterDo);
            return getBaseMapper().selectList(wrapper);
        }
        CloudVdcDo vdcDo = cloudVdcService.getVdcByOrgId(organizationDo.getId());
        return listClusterBYZoneId(vdcDo.getZoneId());
    }

    @Override
    public List<CloudClusterDo> listClusterBYZoneId(Integer zoneId) {
        List<Integer> zoneClusterIdList =
                cloudZoneClusterService.listZoneClusterByZone(zoneId).stream().map(CloudZoneClusterDo::getClusterId
                ).collect(Collectors.toList());
        List<CloudClusterDo> clusterDoList = new ArrayList<>();
        if (!zoneClusterIdList.isEmpty()) {
            CloudClusterDo queryClusterDo = new CloudClusterDo();
            queryClusterDo.setDeleteFlag(false);
            QueryWrapper<CloudClusterDo> wrapper = new QueryWrapper<>(queryClusterDo);
            wrapper.in("id", zoneClusterIdList);
            clusterDoList = getBaseMapper().selectList(wrapper);
        }
        return clusterDoList;
    }
}
