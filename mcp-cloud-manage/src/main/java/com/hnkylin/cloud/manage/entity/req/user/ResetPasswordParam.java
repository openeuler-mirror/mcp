package com.hnkylin.cloud.manage.entity.req.user;


import com.hnkylin.cloud.core.annotation.FieldCheck;
import lombok.Data;


@Data
public class ResetPasswordParam {

    @FieldCheck(notNull = true, notNullMessage = "旧密码不能为空")
    private String oldPassword;

    @FieldCheck(notNull = true, notNullMessage = "审核状态不能为空")
    private String newPassword;


}
