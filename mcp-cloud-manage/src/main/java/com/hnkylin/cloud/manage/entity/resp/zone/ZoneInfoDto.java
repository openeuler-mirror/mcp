package com.hnkylin.cloud.manage.entity.resp.zone;

import com.hnkylin.cloud.core.enums.CloudClusterType;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ZoneInfoDto {

    //可用区ID
    private Integer zoneId;

    //可用区名称
    private String name;

    //可用区描述
    private String remark;


    //集群类型
    private CloudClusterType type;


}
