package com.hnkylin.cloud.selfservice.entity.req;

import com.hnkylin.cloud.core.annotation.FieldCheck;
import com.hnkylin.cloud.core.common.BasePageParam;
import lombok.Data;

@Data
public class ServerVmOperateLogPageParam extends BasePageParam {


    @FieldCheck(notNull = true, notNullMessage = "云服务uuid不能为空")
    private String serverVmUuid;

}
