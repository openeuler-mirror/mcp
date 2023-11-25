package com.hnkylin.cloud.core.enums;

public enum RoleType {
    PLATFORM(0, "平台管理"),
    ORG(1, "组织管理"),
    SELF_SERVICE(2, "自服务用户"),
    ;


    RoleType(int value, String desc) {
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
