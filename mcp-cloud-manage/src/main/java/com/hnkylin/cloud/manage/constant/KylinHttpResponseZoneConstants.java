package com.hnkylin.cloud.manage.constant;

/**
 * 组织管理-响应信息
 */
public interface KylinHttpResponseZoneConstants {


    String EXIST_ZONE_NAME = "可用区名称已存在，请核对";

    String EXIST_VDC_NOT_DELETE = "有VDC绑定了该可用区，不能删除";


    String ZONE_BIND_VDC_NOT_UNBIND_CLUSTER = "有VDC绑定了该可用区，不能解绑物理集群";

    String CLUSTER_IS_NOT_EMPTY = "请选择物理集群";


}
