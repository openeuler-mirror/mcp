package com.hnkylin.cloud.manage.entity.req.user;


import com.hnkylin.cloud.core.annotation.FieldCheck;
import lombok.Data;

@Data
public class BaseUserParam {

    @FieldCheck(notNull = true, notNullMessage = "用户ID不能为空")
    Integer userId;


}
