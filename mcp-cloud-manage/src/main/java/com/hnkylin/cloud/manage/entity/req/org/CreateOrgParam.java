package com.hnkylin.cloud.manage.entity.req.org;


import com.hnkylin.cloud.core.annotation.FieldCheck;
import lombok.Data;

@Data
public class CreateOrgParam {

    @FieldCheck(notNull = true, notNullMessage = "组织名称不能为空")
    private String organizationName;

    private String remark;

    private Integer parentId;

    @FieldCheck(notNull = true, notNullMessage = "VDC不能为空")
    private Integer vdcId;

    private Boolean createLeader;

    private String leaderUserName;

    private String leaderRealName;

    private String leaderPassword;

}
