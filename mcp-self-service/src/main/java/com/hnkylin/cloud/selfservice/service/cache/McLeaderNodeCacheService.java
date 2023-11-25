package com.hnkylin.cloud.selfservice.service.cache;

import com.hnkylin.cloud.core.cache.RedisService;


/**
 * mc主节点缓存，记录切主后的mc主节点，避免频繁重试
 */
public interface McLeaderNodeCacheService extends RedisService {

}
