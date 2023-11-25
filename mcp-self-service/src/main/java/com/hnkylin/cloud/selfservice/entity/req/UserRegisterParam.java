package com.hnkylin.cloud.selfservice.entity.req;


import com.hnkylin.cloud.core.annotation.FieldCheck;
import lombok.Data;

@Data
public class UserRegisterParam {

    @FieldCheck(notNull = true, notNullMessage = "用户名不能为空")
    private String userName;

    private String realName;

    @FieldCheck(notNull = true, notNullMessage = "用户密码不能为空")
    private String password;

    private String mobile;

    @FieldCheck(notNull = true, notNullMessage = "申请原因不能为空")
    private String applyReason;

    @FieldCheck(notNull = true, notNullMessage = "组织不能为空", minNum = 0, minNumMessage = "组织ID不能小于0")
    private Integer organizationId = 0;


}
