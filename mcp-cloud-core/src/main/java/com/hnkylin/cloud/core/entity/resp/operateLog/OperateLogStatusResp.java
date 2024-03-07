package com.hnkylin.cloud.core.entity.resp.operateLog;

import com.hnkylin.cloud.core.enums.OperateLogStatus;
import lombok.Data;

@Data
public class OperateLogStatusResp {


    //类型
    private OperateLogStatus status;

    private String i8nMessage;
}
