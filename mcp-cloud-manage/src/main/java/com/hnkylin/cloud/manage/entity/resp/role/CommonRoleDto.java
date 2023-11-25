package com.hnkylin.cloud.manage.entity.resp.role;

import com.hnkylin.cloud.core.enums.RoleType;
import lombok.Data;

@Data
public class CommonRoleDto {

    //角色ID
    private Integer roleId;
    //组织名称
    private String roleName;

    //角色类型
    private RoleType roleType;

    //描述
    private String remark;
}
