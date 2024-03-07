package com.hnkylin.cloud.core.entity.req.operateLog;

import com.hnkylin.cloud.core.annotation.FieldCheck;
import lombok.Data;

@Data
public class ParentOperateLogParam {

    @FieldCheck(notNull = true, notNullMessage = "对象ID不能为空")
    private Integer parentLogId;


}
