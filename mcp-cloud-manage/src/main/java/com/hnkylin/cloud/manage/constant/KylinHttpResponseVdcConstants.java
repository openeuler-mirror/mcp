package com.hnkylin.cloud.manage.constant;

/**
 * 组织管理-响应信息
 */
public interface KylinHttpResponseVdcConstants {


    String ZONE_EXIST_BIND = "该可用区已经被绑定，请检查";

    String VDC_EXCEED_TIER = "已超出5级层级结构限制";

    Integer VDC_MAX_TIRE = 5;

    String EXIST_VDC_NAME = "名称已存在，请核对";

    String EXIST_CHILD_VDC = "存在下级VDC，不能删除";

    String VDC_BIND_ORG = "已有组织绑定该VDC,不能删除";

    String APPLY_MODIFY_VDC_CHECK_PASS = "变更VDC资源成功";

    String APPLY_MODIFY_VDC_CHECK_WAIT = "申请变更VDC资源已提交，请等待上级管理员审核";

    String NOT_PERMISSION_CREATE_FIRST_VDC = "你不能创建一级VDC，请选择上级VDC";

}
