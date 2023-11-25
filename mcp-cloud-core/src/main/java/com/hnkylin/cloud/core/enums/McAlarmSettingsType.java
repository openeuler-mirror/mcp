package com.hnkylin.cloud.core.enums;

public enum McAlarmSettingsType {
    ServerVirtualization(0, "云服务器告警策略"),
    Server(1, "物理服务器告警策略");

    McAlarmSettingsType(int value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    private int value;
    private String desc;

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
