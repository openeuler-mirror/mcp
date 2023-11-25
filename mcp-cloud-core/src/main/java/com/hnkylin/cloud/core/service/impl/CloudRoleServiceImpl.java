package com.hnkylin.cloud.core.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hnkylin.cloud.core.domain.CloudRoleDo;
import com.hnkylin.cloud.core.enums.RoleType;
import com.hnkylin.cloud.core.mapper.CloudRoleMapper;
import com.hnkylin.cloud.core.service.CloudRoleService;
import org.springframework.stereotype.Service;

@Service
public class CloudRoleServiceImpl extends ServiceImpl<CloudRoleMapper, CloudRoleDo>
        implements CloudRoleService {


    @Override
    public CloudRoleDo getOrgRole() {
        CloudRoleDo cloudRoleDo = new CloudRoleDo();
        cloudRoleDo.setRoleType(RoleType.ORG);
        cloudRoleDo.setDeleteFlag(false);
        QueryWrapper<CloudRoleDo> queryWrapper = new QueryWrapper(cloudRoleDo);
        return getOne(queryWrapper);
    }
}
