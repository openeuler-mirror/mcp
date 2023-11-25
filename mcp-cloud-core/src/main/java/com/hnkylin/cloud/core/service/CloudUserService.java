package com.hnkylin.cloud.core.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hnkylin.cloud.core.domain.CloudUserDo;

public interface CloudUserService extends IService<CloudUserDo> {

    CloudUserDo queryUserByUserName(String userName);


}
