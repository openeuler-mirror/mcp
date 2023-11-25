package com.hnkylin.cloud.manage.entity.req.zone;


import com.hnkylin.cloud.core.annotation.FieldCheck;
import com.hnkylin.cloud.core.enums.CloudClusterType;
import lombok.Data;

import java.util.List;
import java.util.Set;

@Data
public class ModifyZoneParam {

    private Integer zoneId;

    @FieldCheck(notNull = true, notNullMessage = "可用区名称不能为空")
    private String name;


    private String remark;

    @FieldCheck(notNull = true, notNullMessage = "可用区类型不能为空")
    private CloudClusterType type;


    private Set<Integer> clusterIdList;


}
