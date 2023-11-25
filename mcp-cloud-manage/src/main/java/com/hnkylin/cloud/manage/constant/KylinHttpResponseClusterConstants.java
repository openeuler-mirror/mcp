package com.hnkylin.cloud.manage.constant;

/**
 * 组织管理-响应信息
 */
public interface KylinHttpResponseClusterConstants {


    String EXIST_CLUSTER_NAME = "集群名称已存在，请核对";

    String CLUSTER_NODE_IS_NOT_EMPTY = "集群节点不能为空";

    String CLUSTER_NODE_IS_EXIST = "集群节点已存在，请核对";

    String CLUSTER_NAME_AND_PASSWORD_ERR = "集群账号密码错误，请核对后输入";

    String CLUSTER_NODE_NOT_VISIT = "节点不能访问，请检查节点是否正确";

    String CLUSTER_HAS_MACHINE_NOT_DELETE = "有用户拥有该集群下的云服务器，不能删除集群";

    String ZONE_BIND_CLUSTER_NOT_DELETE = "有可用区绑定了集群，不能删除集群";

    String MASTER_NODE_CANNOT_DELETE = "当前主节点不能删除";


}
