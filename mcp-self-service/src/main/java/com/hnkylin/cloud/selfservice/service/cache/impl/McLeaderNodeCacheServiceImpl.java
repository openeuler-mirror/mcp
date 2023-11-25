package com.hnkylin.cloud.selfservice.service.cache.impl;

import com.hnkylin.cloud.core.cache.RedisServiceImpl;

import com.hnkylin.cloud.selfservice.constant.SelfServiceRedisConstant;
import com.hnkylin.cloud.selfservice.service.cache.McLeaderNodeCacheService;
import org.springframework.stereotype.Service;


@Service
public class McLeaderNodeCacheServiceImpl extends RedisServiceImpl implements McLeaderNodeCacheService {


    @Override
    protected String initPrefixKey() {
        return SelfServiceRedisConstant.MC_LEADER_NODE;
    }
}