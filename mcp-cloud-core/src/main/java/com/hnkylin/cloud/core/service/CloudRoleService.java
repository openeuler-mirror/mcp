package com.hnkylin.cloud.core.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hnkylin.cloud.core.domain.CloudRoleDo;


public interface CloudRoleService extends IService<CloudRoleDo> {


    /**
     * 获取组织管理员角色
     *
     * @return
     */
    CloudRoleDo getOrgRole();


}
