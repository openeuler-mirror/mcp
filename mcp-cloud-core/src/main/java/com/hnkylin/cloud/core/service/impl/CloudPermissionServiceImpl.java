package com.hnkylin.cloud.core.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hnkylin.cloud.core.common.KylinCommonConstants;
import com.hnkylin.cloud.core.domain.CloudPermissionDo;
import com.hnkylin.cloud.core.domain.CloudRolePermissionDo;
import com.hnkylin.cloud.core.mapper.CloudPermissionMapper;
import com.hnkylin.cloud.core.service.CloudPermissionService;
import com.hnkylin.cloud.core.service.CloudRolePermissionService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class CloudPermissionServiceImpl extends ServiceImpl<CloudPermissionMapper, CloudPermissionDo>
        implements CloudPermissionService {


    @Override
    public List<CloudPermissionDo> allPermissions() {

        CloudPermissionDo permissionDo = new CloudPermissionDo();
        permissionDo.setDeleteFlag(Boolean.FALSE);
        Wrapper<CloudPermissionDo> wrapper = new QueryWrapper<>(permissionDo);
        return baseMapper.selectList(wrapper);
    }

    @Override
    public List<CloudPermissionDo> customPlatformRoleMaxPermission() {
        CloudPermissionDo permissionDo = new CloudPermissionDo();
        permissionDo.setDeleteFlag(Boolean.FALSE);
        permissionDo.setPlatformRolePermission(Boolean.TRUE);
        Wrapper<CloudPermissionDo> wrapper = new QueryWrapper<>(permissionDo);
        return baseMapper.selectList(wrapper);
    }
}
