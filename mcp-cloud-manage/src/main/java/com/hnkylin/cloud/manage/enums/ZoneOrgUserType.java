package com.hnkylin.cloud.manage.enums;

public enum ZoneOrgUserType {

    ZONE(0, "可用区"),
    ORG(1, "组织"),
    USER(2, "用户");


    ZoneOrgUserType(int value, String desc) {
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
