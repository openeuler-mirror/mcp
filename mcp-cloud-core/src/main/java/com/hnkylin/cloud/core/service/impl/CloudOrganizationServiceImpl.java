package com.hnkylin.cloud.core.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hnkylin.cloud.core.common.KylinCommonConstants;
import com.hnkylin.cloud.core.domain.CloudOrgVdcDo;
import com.hnkylin.cloud.core.domain.CloudOrganizationDo;
import com.hnkylin.cloud.core.mapper.CloudOrganizationMapper;
import com.hnkylin.cloud.core.service.CloudOrgVdcService;
import com.hnkylin.cloud.core.service.CloudOrganizationService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CloudOrganizationServiceImpl extends ServiceImpl<CloudOrganizationMapper, CloudOrganizationDo>
        implements CloudOrganizationService {

    @Resource
    private CloudOrgVdcService cloudOrgVdcService;
    @Override
    public List<CloudOrganizationDo> queryAllOrgList() {
        CloudOrganizationDo organizationDo = new CloudOrganizationDo();
        organizationDo.setDeleteFlag(Boolean.FALSE);
        Wrapper<CloudOrganizationDo> wrapper = new QueryWrapper<>(organizationDo);
        return baseMapper.selectList(wrapper);
    }


    @Override
    public CloudOrganizationDo queryByOrganizationNameName(String organizationName) {
        CloudOrganizationDo orgDo = new CloudOrganizationDo();
        orgDo.setOrganizationName(organizationName);
        orgDo.setDeleteFlag(Boolean.FALSE);
        Wrapper<CloudOrganizationDo> wrapper = new QueryWrapper<>(orgDo);
        List<CloudOrganizationDo> list = baseMapper.selectList(wrapper);

        return list.isEmpty() ? null : list.get(0);
    }

    @Override
    public List<CloudOrganizationDo> listChildOrgByOrgId(Integer orgId) {
        List<CloudOrganizationDo> allOrgList = queryAllOrgList();
        CloudOrganizationDo parentDo = getById(orgId);
        List<CloudOrganizationDo> childOrgList = new ArrayList<>();
        childOrgList.add(parentDo);
        List<Integer> parentIds = new ArrayList<>();
        parentIds.add(orgId);
        getChildList(parentIds, childOrgList, allOrgList);
        return childOrgList;

    }

    /**
     * 递归获取子部门
     *
     * @param parentIds
     * @param childOrgList
     * @param allOrgList
     */
    private void getChildList(List<Integer> parentIds, List<CloudOrganizationDo> childOrgList,
                              List<CloudOrganizationDo> allOrgList) {
        List<CloudOrganizationDo> childList =
                allOrgList.stream().filter(item -> parentIds.contains(item.getParentId())).collect(Collectors.toList());
        if (childList.isEmpty()) {
            return;
        }
        List<Integer> newParentIds = childList.stream().map(CloudOrganizationDo::getId).collect(Collectors.toList());
        childOrgList.addAll(childList);
        getChildList(newParentIds, childOrgList, allOrgList);
    }

    @Override
    public CloudOrganizationDo getDefaultTopOrg() {
        CloudOrganizationDo userOrgDo = new CloudOrganizationDo();
        userOrgDo.setDeleteFlag(false);
        userOrgDo.setParentId(KylinCommonConstants.TOP_PARENT_ID);
        Wrapper<CloudOrganizationDo> wrapper =
                new QueryWrapper<>(userOrgDo);
        return getOne(wrapper);
    }

    @Override
    public List<CloudOrganizationDo> getOrgListByVdcList(List<Integer> vdcList) {
        CloudOrgVdcDo orgVdcDo = new CloudOrgVdcDo();
        orgVdcDo.setDeleteFlag(false);
        QueryWrapper<CloudOrgVdcDo> orgVdcDoWrapper =
                new QueryWrapper<>(orgVdcDo);
        orgVdcDoWrapper.in("vdc_id", vdcList);
        List<CloudOrgVdcDo> orgVdcList = cloudOrgVdcService.getBaseMapper().selectList(orgVdcDoWrapper);
        List<Integer> orgIdList = orgVdcList.stream().map(CloudOrgVdcDo::getOrgId).collect(Collectors.toList());
        if (orgIdList.isEmpty()) {
            return new ArrayList<>();
        }

        CloudOrganizationDo organizationDo = new CloudOrganizationDo();
        organizationDo.setDeleteFlag(Boolean.FALSE);
        QueryWrapper<CloudOrganizationDo> organizationDoWrapper = new QueryWrapper<>(organizationDo);
        organizationDoWrapper.in("id", orgIdList);
        return baseMapper.selectList(organizationDoWrapper);
    }
}
