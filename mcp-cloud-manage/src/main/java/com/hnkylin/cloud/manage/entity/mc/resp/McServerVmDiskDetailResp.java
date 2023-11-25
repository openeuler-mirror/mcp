package com.hnkylin.cloud.manage.entity.mc.resp;

import com.hnkylin.cloud.core.enums.ApplyMcServerVmType;
import com.hnkylin.cloud.core.enums.LastUpdateType;
import com.hnkylin.cloud.core.enums.ModifyType;
import lombok.Data;

@Data
public class McServerVmDiskDetailResp {

    //磁盘大小
    private Integer diskSize;

    //类型
    private ApplyMcServerVmType type = ApplyMcServerVmType.original;

    private Long id;

    private ModifyType modifyType;

    private Integer oldDiskSize;

    private Integer applyId;

    private ModifyType applyModifyType;


}
