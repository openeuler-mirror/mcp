package com.hnkylin.cloud.core.entity.req.operateLog;

import com.hnkylin.cloud.core.annotation.FieldCheck;
import com.hnkylin.cloud.core.common.BasePageParam;
import com.hnkylin.cloud.core.enums.OperateLogType;
import lombok.Data;

@Data
public class BasePageOperateLogParam extends BasePageParam {

    @FieldCheck(notNull = true, notNullMessage = "对象ID不能为空")
    private Integer objId;

    private OperateLogType type;

}
