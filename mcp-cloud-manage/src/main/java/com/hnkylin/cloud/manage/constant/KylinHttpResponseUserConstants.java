package com.hnkylin.cloud.manage.constant;

/**
 * 组织管理-响应信息
 */
public interface KylinHttpResponseUserConstants {


    String EXIST_USER = "该用户名已存在,请核对用户姓名";

    String EXIST_REAI_NAME = "该真实姓名存在,请核对真实姓名";

    String NOT_EXIST_USER = "用户不存在，请重新输入";

    String PASSWORD_ERR = "密码错误，请重新输入";

    String USER_NOT_ACTIVATE = "你的账号处于未激活状态，请联系管理员";

    String OLD_PASSWORD_ERR = "原密码错误，请重新输入";

    String USER_HAS_MACHINE_NO_DELETE = "拥有云服务器，禁止删除";

    String SELF_SERVICE_USER_NOT_LOGIN = "你的自服务账号不能管理管理平台";

    String ORG_EXIST_LEADER_USER = "该组织已存在组织管理员，不能重新添加";


}
