package com.hnkylin.cloud.core.common.servervm;


import com.hnkylin.cloud.core.annotation.FieldCheck;
import lombok.Data;

@Data
public class ServerVmBaseParam {


    @FieldCheck(notNull = true, notNullMessage = "云服务器ID")
    private String serverVmUuid;


}
