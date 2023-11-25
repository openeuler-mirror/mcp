package com.hnkylin.cloud.manage.entity.req.cluster;

import com.hnkylin.cloud.core.annotation.FieldCheck;
import com.hnkylin.cloud.core.common.BasePageParam;
import lombok.Data;

import java.util.List;

@Data
public class CheckClusterNameAndPasswordParam {

    private Integer clusterId;

    private List<ClusterNodeParams> clusterNodeList;

    @FieldCheck(notNull = true, notNullMessage = "集群管理员账号不能为空")
    private String clusterAdminName;

    @FieldCheck(notNull = true, notNullMessage = "集群管理员密码不能为空")
    private String clusterAdminPassword;


}
