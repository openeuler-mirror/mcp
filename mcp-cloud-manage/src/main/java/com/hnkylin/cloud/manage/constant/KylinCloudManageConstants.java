package com.hnkylin.cloud.manage.constant;


public interface KylinCloudManageConstants {


    /**
     * JWT加密密钥
     */
    public final static String KYLIN_TOKEN_SECRET = "kylin-ksvd-cloud-manage";

    /**
     * 请求头中token标识
     */
    String KYLIN_ACCESS_TOKEN = "KYLIN_ACCESS_TOKEN";
    /**
     * 网卡-网络类型
     */
    String NETWORK_TYPE = "网络类型";

    /**
     * 网卡-网卡类型
     */
    String MODEL_TYPE = "网卡类型";
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

    String CHECK_PASS = "审核通过";

    String OLD_PASSWORD_ERR = "原生密码错误";


    Integer TOP_PARENT_ID = 0;

    Integer TOP_ORG_ID = 1;

    String BACKSLASH = "/";

    String DHCP = "dhcp";

    //初始页码
    int FIRST_PAGE = 1;

    String BRIDGE = "bridge";

    String SYSADMINUSER = "sysadmin";

    String EXCEED_STORAGE = ",存储";

    String ARCHITECTURE = "架构";

    String EXCEED_CPU = ",CPU";

    String EXCEED_MEM = ",内存";

    String EXCEED_ALLOCATE = ",分配资源不足";


    //用户管理标识
    String MANAGE_USER_PERMISSION = "user";

    //权限管理标识
    String MANAGE_CLUSTER_PERMISSION = "cluster";


}