package com.hnkylin.cloud.core.enums;

public enum ServerVmDeadlineType {
    POWER_OFF(0, "关机"),
    DESTROY(1, "销毁");


    ServerVmDeadlineType(int value, String desc) {
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
