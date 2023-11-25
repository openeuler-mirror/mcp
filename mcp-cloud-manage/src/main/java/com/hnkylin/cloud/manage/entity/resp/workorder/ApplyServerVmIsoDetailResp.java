package com.hnkylin.cloud.manage.entity.resp.workorder;

import com.hnkylin.cloud.core.enums.ModifyType;
import lombok.Data;

@Data
public class ApplyServerVmIsoDetailResp {


    private String isoFile;

    private ModifyType modifyType;


    private Integer applyId;

    private ModifyType applyModifyType;


}
