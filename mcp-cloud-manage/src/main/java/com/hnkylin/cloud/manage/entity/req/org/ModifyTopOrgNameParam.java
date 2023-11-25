package com.hnkylin.cloud.manage.entity.req.org;


import com.hnkylin.cloud.core.annotation.FieldCheck;
import lombok.Data;

@Data
public class ModifyTopOrgNameParam {

    @FieldCheck(notNull = true, notNullMessage = "组织ID不能为空")
    Integer orgId;

    @FieldCheck(notNull = true, notNullMessage = "组织名称不能为空")
    private String organizationName;


}
