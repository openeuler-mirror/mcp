package com.hnkylin.cloud.core.enums;

public enum AlarmResourceType {
    VDC_CPU(0, "CPU使用率"),
    VDC_MEM(1, "内存使用率"),
    VDC_STORAGE(1, "存储使用率"),
    ;


    AlarmResourceType(int value, String desc) {
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
