package com.hnkylin.cloud.manage.entity.req.workorder;


import lombok.Data;


@Data
public class QueryPortGroupParam {

    private String virtualSwitchName;

    private Integer clusterId;

}
