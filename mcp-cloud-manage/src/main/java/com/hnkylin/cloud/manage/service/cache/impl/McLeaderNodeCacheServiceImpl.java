package com.hnkylin.cloud.manage.service.cache.impl;

import com.hnkylin.cloud.core.cache.RedisServiceImpl;
import com.hnkylin.cloud.manage.constant.CloudManageRedisConstant;
import com.hnkylin.cloud.manage.service.cache.LoginUserCacheService;
import com.hnkylin.cloud.manage.service.cache.McLeaderNodeCacheService;
import org.springframework.stereotype.Service;


@Service
public class McLeaderNodeCacheServiceImpl extends RedisServiceImpl implements McLeaderNodeCacheService {


    @Override
    protected String initPrefixKey() {
        return CloudManageRedisConstant.CLOUD_MANAGE_MC_LEADER_NODE;
    }
}