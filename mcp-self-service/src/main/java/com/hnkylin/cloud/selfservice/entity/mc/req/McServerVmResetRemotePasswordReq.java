package com.hnkylin.cloud.selfservice.entity.mc.req;

import com.hnkylin.cloud.core.annotation.FieldCheck;
import lombok.Data;

/**
 * 删除快照
 * Created by kylin-ksvd on 21-7-17.
 */
@Data
public class McServerVmResetRemotePasswordReq {


    @FieldCheck(notNull = true, notNullMessage = "云服务器ID不能为空")
    private String uuid;

    @FieldCheck(notNull = true, notNullMessage = "控制台密码不能为空")
    private String remotePassword;


}
