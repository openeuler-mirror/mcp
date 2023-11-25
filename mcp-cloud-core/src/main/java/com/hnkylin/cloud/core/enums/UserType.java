package com.hnkylin.cloud.core.enums;

public enum UserType {
    selfServiceUser(0, "自服务用户"),
    cloudUser(1, "云管用户"),
    ;


    UserType(int value, String desc) {
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
