package com.hnkylin.cloud.core.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hnkylin.cloud.core.domain.CloudRoleDo;
import com.hnkylin.cloud.core.domain.CloudUserRoleDo;
import com.hnkylin.cloud.core.mapper.CloudUserRoleMapper;
import com.hnkylin.cloud.core.service.CloudUserRoleService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CloudUserRoleServiceImpl extends ServiceImpl<CloudUserRoleMapper, CloudUserRoleDo>
        implements CloudUserRoleService {


    @Override
    public CloudUserRoleDo getUserRoleByUserId(Integer userId) {
        CloudUserRoleDo userRoleDo = new CloudUserRoleDo();
        userRoleDo.setDeleteFlag(Boolean.FALSE);
        userRoleDo.setUserId(userId);
        Wrapper<CloudUserRoleDo> wrapper = new QueryWrapper<>(userRoleDo);
        List<CloudUserRoleDo> userRoleDoList = baseMapper.selectList(wrapper);

        return userRoleDoList.isEmpty() ? null : userRoleDoList.get(0);
    }
}
