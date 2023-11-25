package com.hnkylin.cloud.selfservice.constant;


public interface KylinSelfConstants {


    /**
     * JWT加密密钥
     */
    public final static String KYLIN_TOKEN_SECRET = "kylin-ksvd-self-service";

    /**
     * 请求头中token标识
     */
    String KYLIN_ACCESS_TOKEN = "KYLIN_ACCESS_TOKEN";

    /**
     * 请求头中用户标识云服务器列表定时刷新，定时刷新的不会去更新缓存中redis的过期时间
     */
    String TIME_REFRESH = "TIME_REFRESH";

    /**
     * 网卡-网络类型
     */
    String NETWORK_TYPE = "网络类型";

    /**
     * 网卡-交换机
     */
    String NETWORK_SWITCH = "交换机";


    /**
     * 网卡-端口组
     */
    String NETWORK_PORT = "端口组";

    String LEFT_BRACKET = "(";
    String RIGHT_BRACKET = ")";

    String SPACE = "  ";

    String GB = "GB";

}