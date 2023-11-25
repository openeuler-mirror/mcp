package com.hnkylin.cloud.manage.entity.req.cluster;

import com.hnkylin.cloud.core.annotation.FieldCheck;
import com.hnkylin.cloud.core.common.BasePageParam;
import lombok.Data;

@Data
public class ClusterPhysicalHostPageParam extends BasePageParam {

    @FieldCheck(notNull = true, notNullMessage = "集群不能为空")
    private Integer clusterId;

}
