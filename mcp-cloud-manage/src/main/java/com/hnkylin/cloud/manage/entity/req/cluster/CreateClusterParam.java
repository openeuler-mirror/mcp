package com.hnkylin.cloud.manage.entity.req.cluster;


import com.hnkylin.cloud.core.annotation.FieldCheck;
import com.hnkylin.cloud.core.enums.CloudClusterType;
import com.hnkylin.cloud.manage.entity.req.cluster.ClusterNodeParams;
import lombok.Data;

import java.util.List;

@Data
public class CreateClusterParam {

    @FieldCheck(notNull = true, notNullMessage = "集群名称不能为空")
    private String name;


    private String remark;

    @FieldCheck(notNull = true, notNullMessage = "集群名称类型不能为空")
    private CloudClusterType type;


    private List<ClusterNodeParams> clusterNodeList;

    @FieldCheck(notNull = true, notNullMessage = "集群管理员账号不能为空")
    private String clusterAdminName;

    @FieldCheck(notNull = true, notNullMessage = "集群管理员密码不能为空")
    private String clusterAdminPassword;


}
