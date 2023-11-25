package com.hnkylin.cloud.core.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hnkylin.cloud.core.domain.CloudUserRoleDo;


public interface CloudUserRoleService extends IService<CloudUserRoleDo> {


    CloudUserRoleDo getUserRoleByUserId(Integer userId);

}
