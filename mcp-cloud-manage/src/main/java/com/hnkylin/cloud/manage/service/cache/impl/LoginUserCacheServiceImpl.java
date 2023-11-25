package com.hnkylin.cloud.manage.service.cache.impl;

import com.hnkylin.cloud.core.cache.RedisServiceImpl;
import com.hnkylin.cloud.manage.constant.CloudManageRedisConstant;
import com.hnkylin.cloud.manage.service.cache.LoginUserCacheService;
import org.springframework.stereotype.Service;


@Service
public class LoginUserCacheServiceImpl extends RedisServiceImpl implements LoginUserCacheService {


    @Override
    protected String initPrefixKey() {
        return CloudManageRedisConstant.CLOUD_MANAGE_LOGIN_USER_CACHE;
    }
}