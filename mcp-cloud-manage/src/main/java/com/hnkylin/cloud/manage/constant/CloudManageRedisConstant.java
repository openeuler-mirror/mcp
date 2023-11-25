package com.hnkylin.cloud.manage.constant;

public interface CloudManageRedisConstant {

    /**
     * 自服务用户Token令牌缓存KEY前缀
     */
    String CLOUD_MANAGE_LOGIN_USER_CACHE = "kcp:logintoken";

    String CLOUD_MANAGE_MC_LEADER_NODE = "kcp:mcnode";


    String MC_LEADER_URL_KEY = "mcLeaderUrl_";

    String CLOUD_MANAGE_MC_NODE_LIST_KEY = "nodeList_";


    String UID = "uid:";

    long CLOUD_MANAGE_LOGIN_USER_CACHE_EXPIRE = 2 * 60 * 60 * 1000;
}
