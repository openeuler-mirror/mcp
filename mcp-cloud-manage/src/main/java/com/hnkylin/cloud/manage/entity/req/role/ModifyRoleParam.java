package com.hnkylin.cloud.manage.entity.req.role;


import com.hnkylin.cloud.core.annotation.FieldCheck;
import lombok.Data;

import java.util.List;

@Data
public class ModifyRoleParam {


    @FieldCheck(notNull = true, notNullMessage = "角色ID不能为空")
    private Integer roleId;

    @FieldCheck(notNull = true, notNullMessage = "角色名称不能为空")
    private String roleName;


    private List<Integer> permissions;

    private String remark;


}
