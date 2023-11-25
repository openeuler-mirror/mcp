package com.hnkylin.cloud.manage.entity.resp.role;

import lombok.Data;

import java.util.List;

@Data
public class RoleInfoRespDto extends CommonRoleDto {


    //角色拥有的权限
    private List<RolePermissionTreeDto> permissionTree;

}


