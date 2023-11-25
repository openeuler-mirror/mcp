package com.hnkylin.cloud.manage.entity.mc.req;

import lombok.Data;

import java.util.List;

@Data
public class QueryVdcUsedResourceParam {

    private List<String> uuidList;
}
