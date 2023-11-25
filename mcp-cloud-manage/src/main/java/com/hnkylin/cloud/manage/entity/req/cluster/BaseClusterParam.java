package com.hnkylin.cloud.manage.entity.req.cluster;


import com.hnkylin.cloud.core.annotation.FieldCheck;
import lombok.Data;

@Data
public class BaseClusterParam {

    @FieldCheck(notNull = true, notNullMessage = "集群ID不能为空")
    Integer clusterId;


}
