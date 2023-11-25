package com.hnkylin.cloud.manage.constant;

/**
 * 组织管理-响应信息
 */
public interface KylinHttpResponseOrgConstants {


    String EXIST_ORG = "组织名已存在，请核对组织名";

    String HAS_CHILD_NOT_DELETE = "组织存在下级组织，不能删除";

    String HAS_USER_NOT_DELETE = "组织中存在用户，不能删除";

    String HAS_SERVERVM_NOT_DELETE = "组织中存在云服务器，不能删除";


}
