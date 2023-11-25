package com.hnkylin.cloud.core.common.servervm;

import com.hnkylin.cloud.core.annotation.FieldCheck;
import lombok.Data;

import java.util.List;

@Data
public class ServerVmBatchOperateParam {


    @FieldCheck(notNull = true, notNullMessage = "云服务uuid集合不能为空")
    private List<String> serverVmUuids;


}
