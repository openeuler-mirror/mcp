package com.hnkylin.cloud.manage.entity.resp.workorder;

import com.hnkylin.cloud.core.enums.ModifyType;
import lombok.Data;

@Data
public class ServerVmDiskDto {

    //硬盘大小
    private Integer diskSize;
    //用途
    private String purpose;

    private ModifyType modifyType;

    private Integer oldDiskSize;
}
