package com.hnkylin.cloud.core.enums;

public enum ArchitectureResourceType {
    CPU(0, "CPU"),
    MEM(1, "MEM"),
    STORAGE(1, "MEM"),
    ;


    ArchitectureResourceType(int value, String desc) {
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
