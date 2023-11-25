package com.hnkylin.cloud.selfservice.entity.mc.resp;

import com.hnkylin.cloud.core.enums.ApplyMcServerVmType;
import com.hnkylin.cloud.core.enums.LastUpdateType;
import com.hnkylin.cloud.core.enums.ModifyType;
import lombok.Data;

@Data
public class McTemplateDiskDetailResp {

    private Integer diskSize;

    private Long id;


}
