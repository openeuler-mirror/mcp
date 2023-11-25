package com.hnkylin.cloud.manage.entity.req.org;


import com.hnkylin.cloud.core.annotation.FieldCheck;
import lombok.Data;

@Data
public class ModifyOrgParam {

    @FieldCheck(notNull = true, notNullMessage = "组织ID不能为空")
    Integer orgId;

    @FieldCheck(notNull = true, notNullMessage = "组织名称不能为空")
    private String organizationName;

    private String remark;

    @FieldCheck(notNull = true, notNullMessage = "父组织ID不能为空")
    private Integer parentId;

    @FieldCheck(notNull = true, notNullMessage = "VDC不能为空")
    private Integer vdcId;


    private Boolean createLeader;

    private Integer leaderUserId = 0;

    private String leaderUserName;

    private String leaderRealName;

    private String leaderPassword;

}
