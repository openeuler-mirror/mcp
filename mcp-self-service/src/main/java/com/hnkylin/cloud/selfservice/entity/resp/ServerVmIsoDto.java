package com.hnkylin.cloud.selfservice.entity.resp;

import com.hnkylin.cloud.core.enums.ModifyType;
import lombok.Data;

@Data
public class ServerVmIsoDto {


    private String isoFile;

    private ModifyType modifyType;

    private String oldIsoFile;

}
