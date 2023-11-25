package com.hnkylin.cloud.manage.entity.req.role;


import com.hnkylin.cloud.core.annotation.FieldCheck;
import lombok.Data;

@Data
public class BaseRoleParam {

    @FieldCheck(notNull = true, notNullMessage = "角色ID不能为空")
    Integer roleId;


}
