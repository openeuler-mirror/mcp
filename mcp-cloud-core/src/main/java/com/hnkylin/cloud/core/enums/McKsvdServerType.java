package com.hnkylin.cloud.core.enums;

public enum McKsvdServerType {

    CM(1, "Cluster Master (no VDI)"),
    VDI(2, "VDI"),
    LEAF_DESKTOP(3, "VDE"),
    LEAF_DRIVE(4, "LEAF Drive"),
    CB_CM(5, "Cloud Branch (CM, no VDI)"),
    CB_VDI(6, "Cloud Branch (VDI)"),
    CM_VDI(7, "CM and VDI"),
    CB_CM_VDI(8, "Cloud Branch (CM and VDI)"),
    GATEWAY(9, "Gateway");

    McKsvdServerType(int value, String name) {
        this.value = value;
        this.name = name;
    }

    private int value;
    private String name;


    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
