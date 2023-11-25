package com.hnkylin.cloud.manage.constant;

/**
 * 组织管理-响应信息
 */
public interface KylinHttpResponseOrderConstants {


    //创建虚拟及失败
    String CREATE_SERVERVM_ERR = "创建云服务器失败";

    String MODIFY_SERVERVM_ERR = "变更云服务器失败";
    //
    String WORK_ORDER_ALREADY_CHECK = "该工单已经审核过，不能重复审核";

    String NOT_PERMISSION_WAIT_LEADER_CHECK = "你不能审核该VDC变更申请，请等到上级管理员进行审核";


}
