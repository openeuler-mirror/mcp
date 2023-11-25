package com.hnkylin.cloud.manage.entity.req.user;

import com.hnkylin.cloud.core.common.BasePageParam;
import lombok.Data;

@Data
public class PageUserParam extends BasePageParam {

    private Integer orgId;

    private String searchKey;
}
