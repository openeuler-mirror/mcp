package com.hnkylin.cloud.selfservice.entity.req;

import com.hnkylin.cloud.core.enums.ApplyMcServerVmType;
import lombok.Data;

@Data
public class ServerVmNetworkParam {


    private String purpose;

    //类型
    private ApplyMcServerVmType type;

    private Long interfaceId;
}
