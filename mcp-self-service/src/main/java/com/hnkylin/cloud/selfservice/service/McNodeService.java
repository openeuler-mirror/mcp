package com.hnkylin.cloud.selfservice.service;


import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hnkylin.cloud.core.domain.CloudClusterDo;
import com.hnkylin.cloud.core.service.CloudClusterService;
import com.hnkylin.cloud.selfservice.config.MCConfigProperties;
import com.hnkylin.cloud.selfservice.constant.SelfServiceRedisConstant;
import com.hnkylin.cloud.selfservice.service.cache.McLeaderNodeCacheService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


@Service
public class McNodeService {

    @Resource
    private McLeaderNodeCacheService mcLeaderNodeCacheService;

    @Resource
    private CloudClusterService cloudClusterService;


    /**
     * 初始化将mcNode节点列表，放入缓存中
     */
    public void initMcNodeListToCache() {
        CloudClusterDo clusterDo = new CloudClusterDo();
        clusterDo.setDeleteFlag(false);
        Wrapper<CloudClusterDo> wrapper = new QueryWrapper<>(clusterDo);
        List<CloudClusterDo> clusterDoList = cloudClusterService.list(wrapper);
        if (!clusterDoList.isEmpty()) {
            clusterDoList.forEach(item -> {
                Integer clusterId = item.getId();
                List<String> mcNodeList = cloudClusterService.formatClusterNodeList(item.getId());
                if (!mcNodeList.isEmpty()) {
                    setMcNodeListToCache(mcNodeList, clusterId);
                }
                String mcLeaderUrl = getMcLeaderUrlFromCache(clusterId);
                if (!mcNodeList.contains(mcLeaderUrl)) {
                    mcLeaderNodeCacheService.vDelete(SelfServiceRedisConstant.MC_LEADER_URL_KEY + clusterId);
                }
                if (!mcNodeList.isEmpty()) {
                    setMcLeaderUrlToCache(mcNodeList.get(0), clusterId);
                }
            });

        }

    }


    /**
     * 获取缓存中的mc主节点的url
     */
    public String getMcLeaderUrlFromCache(Integer clusterId) {
        Object mcLeaderUrl = mcLeaderNodeCacheService.vGet(SelfServiceRedisConstant.MC_LEADER_URL_KEY + clusterId);
        if (Objects.nonNull(mcLeaderUrl)) {
            return mcLeaderUrl.toString();
        }
        List<String> mcNodeList = cloudClusterService.formatClusterNodeList(clusterId);
        return mcNodeList.isEmpty() ? null : mcNodeList.get(0);
    }

    /**
     * 设置mc主节点的url到缓存中
     */
    public void setMcLeaderUrlToCache(String mcLeaderUrl, Integer clusterId) {
        mcLeaderNodeCacheService.vSet(SelfServiceRedisConstant.MC_LEADER_URL_KEY + clusterId, mcLeaderUrl);
        //修改缓存中的mcNodeList  将当前可用的节点放在list中第一位
        setLeaderUrlFirst(mcLeaderUrl, clusterId);
    }


    /**
     * 删除缓存中mc主节点额url
     *
     * @param clusterId
     */
    public void deleteMcLeaderUrlToCache(Integer clusterId) {
        mcLeaderNodeCacheService.vDelete(SelfServiceRedisConstant.MC_LEADER_URL_KEY + clusterId);
    }

    /**
     * 设置mc的CM节点到缓存中
     */
    public void setMcNodeListToCache(List<String> mcNodeList, Integer clusterId) {
        mcLeaderNodeCacheService.vSet(SelfServiceRedisConstant.MC_NODE_LIST_KEY + clusterId, mcNodeList);
    }

    /**
     * 从缓存中读取mc的cm节点列表
     */
    public List<String> getMcNodeListFromCache(Integer clusterId) {
        Object mcNodeList = mcLeaderNodeCacheService.vGet(SelfServiceRedisConstant.MC_NODE_LIST_KEY + clusterId);
        if (Objects.isNull(mcNodeList)) {
            return cloudClusterService.formatClusterNodeList(clusterId);
        }
        return (List<String>) mcNodeList;
    }

    /**
     * 修改缓存中的mcNodeList  将当前可用的节点放在list中第一位
     */
    private void setLeaderUrlFirst(String mcLeaderUrl, Integer clusterId) {
        List<String> mcNodeList = getMcNodeListFromCache(clusterId);
        if (!mcNodeList.isEmpty()) {
            mcNodeList.remove(mcLeaderUrl);
            mcNodeList.add(0, mcLeaderUrl);
            setMcNodeListToCache(mcNodeList, clusterId);
        }
    }


}
