package com.hnkylin.cloud.core.enums;

public enum WorkOrderStatus {

    WAIT_CHECK(0, "待审核"),
    CHECK_PASS(1, "已通过"),
    CHECK_NO_PASS(2, "已拒绝"),
    ALL(-1, "全部");


    WorkOrderStatus(int value, String desc) {
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
