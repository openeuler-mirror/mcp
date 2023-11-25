package com.hnkylin.cloud.manage.entity.req.workorder;

import com.hnkylin.cloud.core.enums.ModifyType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PassServerVmIsoParam {

    //磁盘ID
    private String isoFile;

    //变更类型
    private ModifyType modifyType;

    private Integer applyId;


}
