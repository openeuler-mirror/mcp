package com.hnkylin.cloud.manage.entity.resp.user;

import com.hnkylin.cloud.core.enums.CloudUserStatus;
import com.hnkylin.cloud.core.enums.RoleType;
import lombok.Data;

@Data
public class ModifyUserInfoRespDto {

    private Integer userId;

    private String userName;

    private String realName;

    private String organizationName;

    private Integer organizationId;

    private String password;

    private String mobile;

    private String remark;

    private CloudUserStatus status;

    private String roleName;

    private Integer roleId;

    private boolean canModifyOrg;

    private boolean canModifyRole;

    private RoleType roleType;


}
