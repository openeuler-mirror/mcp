package com.hnkylin.cloud.core.enums;

public enum ArchitectureType {

    X86_64(0, "X86_64"),
    ARM64(0, "ARM64"),
    MIPS64(0, "MIPS64"),
    SW64(0, "SW64");

    ArchitectureType(int value, String desc) {
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
