package com.hnkylin.cloud.core.enums;

/**
 * Created by kylin-ksvd on 21-7-9.
 */
public enum ApplyMcServerVmType {


    original(0, "原始"),
    custom(1, "自定义"),
    ;


    ApplyMcServerVmType(int value, String desc) {
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
