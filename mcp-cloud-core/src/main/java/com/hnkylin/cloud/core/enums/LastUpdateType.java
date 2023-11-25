package com.hnkylin.cloud.core.enums;

public enum LastUpdateType {

    add("add", "添加"),
    update("update", "修改"),
    delete("delete", "删除");


    LastUpdateType(String value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    private String value;
    private String desc;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
