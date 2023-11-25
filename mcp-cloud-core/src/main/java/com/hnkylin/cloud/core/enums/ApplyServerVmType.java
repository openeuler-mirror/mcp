package com.hnkylin.cloud.core.enums;

/**
 * Created by kylin-ksvd on 21-7-9.
 */
public enum ApplyServerVmType {


    TEMPLATE(0, "基于模板创建"),
    ISO(1, "ISO创建"),
    ;


    ApplyServerVmType(int value, String desc) {
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
