package com.hnkylin.cloud.manage.entity.req.servervm;


import com.hnkylin.cloud.core.annotation.FieldCheck;
import lombok.Data;

@Data
public class ServerVmTransferParam {

    @FieldCheck(notNull = true, notNullMessage = "云服务器不能为空")
    private String machineUUid;

    @FieldCheck(notNull = true, notNullMessage = "用户不能为空")
    private Integer transferToUserId;


    private Integer clusterId;


}
