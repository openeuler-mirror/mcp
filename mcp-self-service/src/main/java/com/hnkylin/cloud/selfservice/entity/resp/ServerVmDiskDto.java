package com.hnkylin.cloud.selfservice.entity.resp;

import com.hnkylin.cloud.core.enums.ModifyType;
import lombok.Data;

@Data
public class ServerVmDiskDto {

    //硬盘大小
    private Integer diskSize;
    //用途
    private String purpose;

    private Integer diskId;

    private ModifyType modifyType;

    private Integer oldDiskSize;


}
