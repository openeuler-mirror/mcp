package com.hnkylin.cloud.selfservice.entity.req;


import com.hnkylin.cloud.core.annotation.FieldCheck;
import lombok.Data;

@Data
public class UpdateRealNameParam {


    @FieldCheck(notNull = true, notNullMessage = "用户真实姓名不能为空")
    private String realName;


}
