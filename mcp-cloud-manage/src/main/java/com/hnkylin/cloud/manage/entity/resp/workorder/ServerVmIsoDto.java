package com.hnkylin.cloud.manage.entity.resp.workorder;

import com.hnkylin.cloud.core.enums.ModifyType;
import lombok.Data;

@Data
public class ServerVmIsoDto {

    //用途
    private String isoFile;

    private ModifyType modifyType;

    private String oldIsoFile;

}
