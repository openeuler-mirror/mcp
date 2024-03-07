package com.hnkylin.cloud.core.entity.resp.operateLog;

import com.hnkylin.cloud.core.enums.OperateLogType;
import lombok.Data;

@Data
public class OperateLogTypeResp {


    //类型
    private OperateLogType type;

    private String i8nMessage;
}
