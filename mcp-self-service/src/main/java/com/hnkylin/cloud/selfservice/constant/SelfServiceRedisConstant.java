package com.hnkylin.cloud.selfservice.constant;

public interface SelfServiceRedisConstant {

    /**
     * 自服务用户Token令牌缓存KEY前缀
     */
    String SELF_SERVICE_LOGIN_USER_CACHE = "portal:logintoken";


    String MC_LEADER_NODE = "portal:mcnode";


    String MC_LEADER_URL_KEY = "mcLeaderUrl_";

    String MC_NODE_LIST_KEY = "nodeList_";

    String UID = "uid:";

    long SELF_SERVICE_LOGIN_USER_CACHE_EXPIRE = 2 * 60 * 60 * 1000;
}
