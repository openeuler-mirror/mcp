package com.hnkylin.cloud.manage.entity.req.cluster;

import com.hnkylin.cloud.core.annotation.FieldCheck;
import com.hnkylin.cloud.core.common.BasePageParam;
import com.hnkylin.cloud.manage.entity.req.cluster.ClusterNodeParams;
import lombok.Data;

import java.util.List;

@Data
public class ClusterStoragePageParam extends BasePageParam {

    private Integer clusterId;

    private List<ClusterNodeParams> clusterNodeList;


}
