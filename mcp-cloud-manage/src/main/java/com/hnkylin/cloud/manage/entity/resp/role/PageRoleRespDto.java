package com.hnkylin.cloud.manage.entity.resp.role;

import com.hnkylin.cloud.core.enums.RoleType;
import lombok.Data;

@Data
public class PageRoleRespDto {

    private Integer roleId;

    private String roleName;

    private RoleType roleType;

    private String roleTypeDesc;

    private String remark;

    private Boolean canDelete;

    private Boolean canModify;

    private String createTime;

    private Boolean defaultRole;


}
