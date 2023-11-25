package com.hnkylin.cloud.manage.enums;

public enum ClusterStatus {
    ONLINE(1, "ONLINE"),
    OFFLINE(0, "OFFLINE"),
    EXCEPTION(-1, "EXCEPTION");


    ClusterStatus(int value, String desc) {
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
