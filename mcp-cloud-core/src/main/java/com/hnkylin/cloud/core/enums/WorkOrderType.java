package com.hnkylin.cloud.core.enums;

public enum WorkOrderType {
    REGISTER_USER(0, "注册账号"),
    MODIFY_USER(1, "修改账号"),
    APPLY_SERVERVM(2, "申请云服务器"),
    MODIFY_SERVERVM(3, "变更云服务器"),
    DEFERRED_SERVERVM(4, "延期云服务器"),
    MODIFY_VDC(5, "变更VDC资源"),
    ALL(-1, "全部");


    WorkOrderType(int value, String desc) {
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
