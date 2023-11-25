package com.hnkylin.cloud.selfservice.entity.req;


import com.hnkylin.cloud.core.annotation.FieldCheck;
import lombok.Data;

@Data
public class LoginUserParam {

    @FieldCheck(notNull = true, notNullMessage = "用户名不能为空")
    private String userName;

    @FieldCheck(notNull = true, notNullMessage = "密码不能为空")
    private String password;

}
