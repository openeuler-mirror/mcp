package com.hnkylin.cloud.core.common.servervm;

import lombok.Data;

/**
 * 云服务器操作日志
 * Created by kylin-ksvd on 21-7-14.
 */
@Data
public class McServerVmLogResp {

    //操作
    private String action;

    //对象名称
    private String objName;

    //操作用户
    private String operUser;

    //原因
    private String reason;


    //详情
    private String detail;

    //开始时间
    private String startTime;

    //结束时间
    private String finishTime;

    //状态
    private String status;


}
