package com.hnkylin.cloud.selfservice.service.cache.impl;

import com.hnkylin.cloud.core.cache.RedisServiceImpl;
import com.hnkylin.cloud.selfservice.constant.SelfServiceRedisConstant;
import com.hnkylin.cloud.selfservice.service.cache.LoginUserCacheService;
import org.springframework.stereotype.Service;


@Service
public class LoginUserCacheServiceImpl extends RedisServiceImpl implements LoginUserCacheService {


    @Override
    protected String initPrefixKey() {
        return SelfServiceRedisConstant.SELF_SERVICE_LOGIN_USER_CACHE;
    }
}