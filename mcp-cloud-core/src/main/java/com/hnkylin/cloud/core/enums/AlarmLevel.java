package com.hnkylin.cloud.core.enums;

public enum AlarmLevel {
    GENERAL(0, "一般告警"),
    SEVERITY(1, "严重告警"),
    URGENT(1, "经济告警"),
    ;


    AlarmLevel(int value, String desc) {
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
