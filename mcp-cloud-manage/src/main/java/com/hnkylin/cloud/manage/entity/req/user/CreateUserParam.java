package com.hnkylin.cloud.manage.entity.req.user;


import com.hnkylin.cloud.core.annotation.FieldCheck;
import com.hnkylin.cloud.core.enums.CloudUserStatus;
import com.hnkylin.cloud.core.enums.UserType;
import lombok.Data;


@Data
public class CreateUserParam {

    @FieldCheck(notNull = true, notNullMessage = "用户名不能为空")
    private String userName;

    @FieldCheck(notNull = true, notNullMessage = "真实姓名不能为空")
    private String realName;

    @FieldCheck(notNull = true, notNullMessage = "密码不能为空")
    private String password;

    private String mobile;

    private String remark;

    private CloudUserStatus status;

    @FieldCheck(notNull = true, notNullMessage = "组织不能为空")
    private Integer organizationId;

    @FieldCheck(notNull = true, notNullMessage = "角色不能为空")
    private Integer roleId;

}
