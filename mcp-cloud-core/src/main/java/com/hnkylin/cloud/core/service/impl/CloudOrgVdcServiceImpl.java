package com.hnkylin.cloud.core.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hnkylin.cloud.core.domain.CloudOrgVdcDo;
import com.hnkylin.cloud.core.domain.CloudVdcDo;
import com.hnkylin.cloud.core.mapper.CloudOrgVdcMapper;
import com.hnkylin.cloud.core.service.CloudOrgVdcService;
import com.hnkylin.cloud.core.service.CloudVdcService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CloudOrgVdcServiceImpl extends ServiceImpl<CloudOrgVdcMapper, CloudOrgVdcDo>
        implements CloudOrgVdcService {


    @Resource
    private CloudVdcService cloudVdcService;

    @Override
    public CloudOrgVdcDo queryOrgVdcByOrgId(Integer orgId) {
        CloudOrgVdcDo orgVdcDo = new CloudOrgVdcDo();
        orgVdcDo.setOrgId(orgId);
        orgVdcDo.setDeleteFlag(Boolean.FALSE);
        Wrapper<CloudOrgVdcDo> wrapper = new QueryWrapper<>(orgVdcDo);
        List<CloudOrgVdcDo> orgVdcDoList = baseMapper.selectList(wrapper);

        return orgVdcDoList.isEmpty() ? null : orgVdcDoList.get(0);
    }


    @Override
    public List<Integer> orgIdListByZoneId(Integer zoneId) {
        List<CloudVdcDo> vdcList = cloudVdcService.vdcListByZone(zoneId);
        List<Integer> vdcIdList = vdcList.stream().map(CloudVdcDo::getId).collect(Collectors.toList());
        if (vdcIdList.isEmpty()) {
            return new ArrayList<>();
        }
        CloudOrgVdcDo orgVdcDo = new CloudOrgVdcDo();
        orgVdcDo.setDeleteFlag(Boolean.FALSE);
        QueryWrapper<CloudOrgVdcDo> wrapper = new QueryWrapper<>(orgVdcDo);
        wrapper.in("vdc_id", vdcIdList);
        List<CloudOrgVdcDo> orgVdcDoList = baseMapper.selectList(wrapper);
        return orgVdcDoList.isEmpty() ? new ArrayList<>() :
                orgVdcDoList.stream().map(CloudOrgVdcDo::getOrgId).collect(Collectors.toList());
    }
}
