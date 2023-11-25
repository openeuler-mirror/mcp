package com.hnkylin.cloud.manage.entity.resp.role;

import com.hnkylin.cloud.manage.entity.resp.role.CommonRoleDto;
import lombok.Data;

import java.util.List;

@Data
public class RoleDetailDto extends CommonRoleDto {


    //角色拥有的权限
    private List<Integer> permissions;

}


