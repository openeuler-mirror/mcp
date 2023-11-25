package com.hnkylin.cloud.core.enums;

/**
 * Created by kylin-ksvd on 21-7-9.
 */
public enum McServerVmStatus {


    AVAILABLE("在线"),
    OFFLINE("离线"),
    CONNECTED("已连接"),
    INSTALLING("安装"),
    SUSPEND("暂停"),
    OVERDUE("已过期"),
    UNKNOWN("其他"),
    ALL("全部");


    McServerVmStatus(String desc) {
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
