package com.hnkylin.cloud.core.enums;

/**
 * Created by kylin-ksvd on 21-7-9.
 */
public enum ModifyType {


    NONE(0, "没有变动"),
    ADD(1, "新增"),
    MODIFY(2, "变更"),
    DELETE(3, "删除");


    ModifyType(int value, String desc) {
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
