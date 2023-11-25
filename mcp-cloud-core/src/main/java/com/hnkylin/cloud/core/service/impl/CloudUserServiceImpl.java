package com.hnkylin.cloud.core.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hnkylin.cloud.core.domain.CloudRoleDo;
import com.hnkylin.cloud.core.domain.CloudUserDo;
import com.hnkylin.cloud.core.domain.CloudUserRoleDo;
import com.hnkylin.cloud.core.enums.RoleType;
import com.hnkylin.cloud.core.mapper.CloudUserMapper;
import com.hnkylin.cloud.core.service.CloudRoleService;
import com.hnkylin.cloud.core.service.CloudUserRoleService;
import com.hnkylin.cloud.core.service.CloudUserService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;

@Service
public class CloudUserServiceImpl extends ServiceImpl<CloudUserMapper, CloudUserDo> implements CloudUserService {


    @Override
    public CloudUserDo queryUserByUserName(String userName) {
        CloudUserDo cloudUserDo = CloudUserDo.builder().userName(userName).build();
        cloudUserDo.setDeleteFlag(false);
        Wrapper<CloudUserDo> wrapper = new QueryWrapper<>(cloudUserDo);
        List<CloudUserDo> list = baseMapper.selectList(wrapper);
        return list.isEmpty() ? null : list.get(0);
    }


}
