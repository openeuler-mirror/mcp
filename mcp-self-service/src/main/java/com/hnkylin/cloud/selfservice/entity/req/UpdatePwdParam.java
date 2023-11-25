package com.hnkylin.cloud.selfservice.entity.req;


import com.hnkylin.cloud.core.annotation.FieldCheck;
import lombok.Data;

@Data
public class UpdatePwdParam {


    @FieldCheck(notNull = true, notNullMessage = "用户密码不能为空")
    private String password;


    @FieldCheck(notNull = true, notNullMessage = "原密码不能为空")
    private String oldPassword;


}
