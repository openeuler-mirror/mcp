package com.hnkylin.cloud.core.enums;

public enum McArchitectureType {

    X86_64("x86_64"),
    ARM("arm"),
    AARCH64("aarch64"),
    MIPS("mips"),
    sw_64("sw_64"),
    LOWER_86_64("x86_64");

    McArchitectureType(String name) {
        this.name = name;
    }

    private String name;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
