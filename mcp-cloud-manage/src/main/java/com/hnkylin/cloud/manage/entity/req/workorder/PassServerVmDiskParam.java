package com.hnkylin.cloud.manage.entity.req.workorder;

import com.hnkylin.cloud.core.enums.ModifyType;
import lombok.Data;

@Data
public class PassServerVmDiskParam {

    //磁盘ID
    private Integer id;

    //磁盘大小
    private Integer diskCapacity;

    //变更类型
    private ModifyType modifyType;

    private Integer applyId;


}
