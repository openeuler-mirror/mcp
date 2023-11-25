package com.hnkylin.cloud.manage.entity.req.org;


import com.hnkylin.cloud.core.annotation.FieldCheck;
import lombok.Data;

@Data
public class BaseOrgParam {

    @FieldCheck(notNull = true, notNullMessage = "组织ID不能为空")
    Integer orgId;

    private String searchKey;


}
