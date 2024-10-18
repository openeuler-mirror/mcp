package com.hnkylin.cloud.core.entity.req.kcpha;

import com.hnkylin.cloud.core.annotation.FieldCheck;
import lombok.Data;

@Data
public class AddSlaveKcpParam {

    @FieldCheck(notNull = true, notNullMessage = "ip地址不能为空")
    private String slaveIp;

    @FieldCheck(notNull = true, notNullMessage = "管理员账号不能为空")
    private String sysadmin;

    @FieldCheck(notNull = true, notNullMessage = "密码不能为空")
    private String password;
}
