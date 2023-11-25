package com.hnkylin.cloud.core.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hnkylin.cloud.core.common.KylinCommonConstants;
import com.hnkylin.cloud.core.domain.CloudOrgVdcDo;
import com.hnkylin.cloud.core.domain.CloudOrganizationDo;
import com.hnkylin.cloud.core.domain.CloudVdcDo;
import com.hnkylin.cloud.core.mapper.CloudVdcMapper;
import com.hnkylin.cloud.core.service.CloudOrgVdcService;
import com.hnkylin.cloud.core.service.CloudVdcService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CloudVdcServiceImpl extends ServiceImpl<CloudVdcMapper, CloudVdcDo>
        implements CloudVdcService {


    @Resource
    private CloudOrgVdcService cloudOrgVdcService;


    @Override
    public List<CloudVdcDo> getChildVdcList(Integer parentVdcId) {
        CloudVdcDo queryVdcDo = new CloudVdcDo();
        queryVdcDo.setDeleteFlag(false);
        queryVdcDo.setParentId(parentVdcId);
        QueryWrapper<CloudVdcDo> vdcWrapper = new QueryWrapper<>(queryVdcDo);
        return getBaseMapper().selectList(vdcWrapper);
    }

    @Override
    public List<Integer> getChildVdcIdList(Integer parentVdcId) {
        List<CloudVdcDo> childVdcLIst = getChildVdcList(parentVdcId);
        return childVdcLIst.stream().map(CloudVdcDo::getId).collect(Collectors.toList());
    }

    @Override
    public List<CloudVdcDo> vdcListByZone(Integer zoneId) {
        CloudVdcDo queryVdcDo = new CloudVdcDo();
        queryVdcDo.setDeleteFlag(false);
        queryVdcDo.setZoneId(zoneId);
        QueryWrapper<CloudVdcDo> vdcWrapper = new QueryWrapper<>(queryVdcDo);
        return getBaseMapper().selectList(vdcWrapper);
    }

    @Override
    public CloudVdcDo getVdcByOrgId(Integer orgId) {
        CloudOrgVdcDo orgVdc = cloudOrgVdcService.queryOrgVdcByOrgId(orgId);
        return Objects.nonNull(orgVdc) ? getById(orgVdc.getVdcId()) : null;
    }

    @Override
    public List<CloudVdcDo> getFirstVdcListByZone(Integer zoneId) {
        CloudVdcDo vdcDo = new CloudVdcDo();
        vdcDo.setZoneId(zoneId);
        vdcDo.setParentId(KylinCommonConstants.TOP_PARENT_ID);
        Wrapper<CloudVdcDo> wrapper = new QueryWrapper<>(vdcDo);
        return getBaseMapper().selectList(wrapper);
    }

    @Override
    public List<CloudVdcDo> getAllChildVdcList(Integer parentVdcId) {
        CloudVdcDo queryVdcDo = new CloudVdcDo();
        queryVdcDo.setDeleteFlag(false);
        QueryWrapper<CloudVdcDo> vdcWrapper = new QueryWrapper<>(queryVdcDo);
        List<CloudVdcDo> allVdcList = list(vdcWrapper);
        List<CloudVdcDo> allChildVdcList = new ArrayList<>();
        List<Integer> parents = new ArrayList<>();
        parents.add(parentVdcId);
        allChildVdcList.add(getById(parentVdcId));
        getChildList(parents, allChildVdcList, allVdcList);
        return allChildVdcList;
    }

    /**
     * 递归获取子VDC
     *
     * @param parentIds
     * @param childVdcList
     * @param allVdcList
     */
    private void getChildList(List<Integer> parentIds, List<CloudVdcDo> childVdcList,
                              List<CloudVdcDo> allVdcList) {
        List<CloudVdcDo> childList =
                allVdcList.stream().filter(item -> parentIds.contains(item.getParentId())).collect(Collectors.toList());
        if (childList.isEmpty()) {
            return;
        }
        List<Integer> newParentIds = childList.stream().map(CloudVdcDo::getId).collect(Collectors.toList());
        childVdcList.addAll(childList);
        getChildList(newParentIds, childVdcList, allVdcList);
    }
}
