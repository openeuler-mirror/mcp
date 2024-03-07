package com.hnkylin.cloud.core.enums;

public enum OperateLogStatus {
    SUCCESS("成功"),
    FAIL("失败"),
    RUNNING("执行中"),
    WAIT_START("待开始"),
    CANCELED("已取消"),
    CANCELING("取消中"),
    TIMEOUT("超时"),
    UNKNOWN("未知"),
    ;


//    SUCCESS("success"),
//    FAILED("failed"),
//    TIMEOUT("timeout"),
//    PENDING("pending"),
//    UNKNOWN("unknown"),
//    WAIT_START("waitStart"),
//    CANCELED("canceled"),
//    CANCELING("canceling");


    OperateLogStatus(String desc) {
        this.desc = desc;
    }

    private String desc;


    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
