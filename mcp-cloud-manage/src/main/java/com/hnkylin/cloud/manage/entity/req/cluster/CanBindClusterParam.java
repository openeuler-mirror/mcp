package com.hnkylin.cloud.manage.entity.req.cluster;

import com.hnkylin.cloud.core.enums.CloudClusterType;
import lombok.Data;

@Data
public class CanBindClusterParam {

    private Integer zoneId;

    private CloudClusterType type;


}
