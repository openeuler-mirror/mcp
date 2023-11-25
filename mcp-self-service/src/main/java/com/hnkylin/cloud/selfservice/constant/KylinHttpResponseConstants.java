package com.hnkylin.cloud.selfservice.constant;

public interface KylinHttpResponseConstants {


    //------token
    String NOT_EXIST_TOKEN = "token不存在";
    String TOKEN_EXPIRE = "token已过期，请重新登录";


    //--------用户
    String ALREADY_EXIST_USER = "该用户名已存在，不能重复注册";

    String NOT_EXIST_USER = "用户不存在，请重新输入";

    String PASSWORD_ERR = "密码错误，请重新输入";

    String USER_NOT_ACTIVATE = "你的账号处于未激活状态，请联系管理员";

    String NOT_LOING_PERMISSION = "你的账号不能登录自服务平台";

    String OLD_PASSWORD_ERR = "原密码错误，请重新输入";

    String UPDATE_REAL_NAME = "修改真实姓名";


    //虚拟机
    String TEMPLATE_LIST_ERR = "获取虚拟机列表失败";


    String TEMPLATE_DEFAULT = "模板默认自带";

    String SERVERVM_LIST_ERR = "查询云服务器列表失败";


    String OPERATE_ERR = "操作失败";

    String START_VM_ERR = "开机失败，请联系管理员";

    String SHUTDOWN_VM_ERR = "关机失败，请联系管理员";

    String FORCED_SHUTDOWN_VM_ERR = "强制关机失败，请联系管理员";

    String RESTART_VM_ERR = "重启失败，请联系管理员";

    String FORCED_RESTART_VM_ERR = "强制重启失败，请联系管理员";

    String SERVERVM_OVERDUE_NOT_OPERATE = "云服务器已过期不能操作";

    String BATCH_OPERATE_ERR = "批量操作失败";

    String SERVER_VM_NAME_EXIST = "云服务器名称已存在，请修改云服务器名称";


}
