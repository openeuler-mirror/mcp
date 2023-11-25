package com.hnkylin.cloud.core.enums;

public enum CloudUserStatus {
    NO_ACTIVATE(0, "未激活"),
    ACTIVATE(1, "激活");


    CloudUserStatus(int value, String desc) {
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
