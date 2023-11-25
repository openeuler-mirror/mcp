package com.hnkylin.cloud.manage.entity.req.role;


import com.hnkylin.cloud.core.annotation.FieldCheck;
import lombok.Data;

import java.util.List;

@Data
public class CreateRoleParam {

    @FieldCheck(notNull = true, notNullMessage = "角色名称不能为空")
    private String roleName;


    private List<Integer> permissions;


    private String remark;


}
