package com.hnkylin.cloud.core.entity.resp.operateLog;

import com.hnkylin.cloud.core.enums.OperateLogAction;
import lombok.Data;

@Data
public class OperateLogActionResp {


    //类型
    private OperateLogAction action;

    private String i8nMessage;
}
