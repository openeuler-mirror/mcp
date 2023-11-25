package com.hnkylin.cloud.manage.entity.resp.org;

import lombok.Data;


@Data
public class OrgDetailRespDto {

    //组织id
    private Integer organizationId;
    //组织名称
    private String organizationName;

    //父组织ID
    private Integer parentId;

    //父组织名称
    private String parentName;


    //描述信息
    private String remark;

    //vdc-ID
    private Integer vdcId;
    //VDC名称
    private String vdcName;

    //是否拥有组织管理员
    private boolean hasOrgLeader;

    //是否能编辑父组织
    private boolean canModifyParent;

    //是否能编辑VDC
    private boolean canModifyVdc;

    //组织管理员ID
    private Integer orgLeaderUserId = 0;

    //组织管理员用户名
    private String orgLeaderUserName;

    //组织管理员真实姓名
    private String orgLeaderRealName;

    //组织管理员密码
    private String orgLeaderUserPassword;


}
