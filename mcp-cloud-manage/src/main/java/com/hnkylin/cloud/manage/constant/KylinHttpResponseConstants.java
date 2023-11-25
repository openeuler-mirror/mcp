package com.hnkylin.cloud.manage.constant;

public interface KylinHttpResponseConstants {


    //------token
    String NOT_EXIST_TOKEN = "token不存在";
    String TOKEN_EXPIRE = "token已过期，请重新登录";


    //--------用户


    String UPDATE_REAL_NAME = "修改真实姓名";


    String NOT_LOGIN_KCP_PERMISSION = "你的账号不能登录云管平台";

    //网络名称重复
    String EXIST_NETWORK_NAME = "网络名称已存在，请核对后重新输入";


    String SERVER_VM_NAME_EXIST = "云服务器名称已存在，请修改云服务器名称";

    String OPERATE_ERR = "操作失败，请联系管理员";

}
