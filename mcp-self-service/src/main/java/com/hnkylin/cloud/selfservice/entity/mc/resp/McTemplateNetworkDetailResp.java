package com.hnkylin.cloud.selfservice.entity.mc.resp;

import com.hnkylin.cloud.core.enums.ApplyMcServerVmType;
import com.hnkylin.cloud.core.enums.LastUpdateType;
import com.hnkylin.cloud.core.enums.ModifyType;
import lombok.Data;

@Data
public class McTemplateNetworkDetailResp {

    private String virtualSwitch;

    private String portGroup;

    private String portGroupUuid;

    private String macAddress;

    private String interfaceType;

    private String modelType;

    private Long id;

    private Integer applyId;

    //变更类型
    private ModifyType modifyType;


}
