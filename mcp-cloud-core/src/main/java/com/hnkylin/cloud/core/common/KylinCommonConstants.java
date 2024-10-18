package com.hnkylin.cloud.core.common;


public interface KylinCommonConstants {


    long TOKEN_EXPIRE_TIME = 2 * 60 * 60 * 1000;


    String MC_TOKEN_KEY = "ksvd_token_secret";


    /**
     * 文件路径标识符
     */

    String FILE_SEPARATOR = "/";

    /**
     * 时间分隔符
     */
    String DATE_SPLIT = " ";


    /**
     * 天开始时间
     */
    String DAY_START = " 00:00:00";

    /**
     * 天结束时间
     */
    String DAY_END = " 23:59:59";


    Integer TOP_PARENT_ID = 0;

    //初始页码
    int FIRST_PAGE = 1;

    int DEFAULT_MAX_SIZE = 10 * 10000;

    //集群主几点最大数
    int MAX_CLUSTER_MASTER_NODE_NUM = 1000;

    int MC_MASTER_PORT = 8443;


    String KCP_OPEN_API_SECRET = "kylinsec@kcp";

    String KCP_OPEN_API_INVOKE_TIME = "INVOKE_TIME";

    String KCP_OPEN_API_TOKEN = "KCP_OPEN_API_TOKEN";

    String PERCENT_100 = "100";

    String PERCENT_0 = "0";

    String PERCENT_10 = "10";

    String KCP_PORT = ":5001";
}