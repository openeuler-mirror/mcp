package com.hnkylin.cloud.core.enums;

public enum OperateLogType {
    IMAGE(0, "镜像"),
    CLUSTER(1, "物理集群"),
    VDC(2, "VDC"),
    ZONE(3, "可用区"),
    ORG(4, "组织"),
    USER(5, "用户"),
    ROLE(6, "角色"),
    SERVERVM(8, "云服务器"),
    ALARM(9, "告警"),
    KCP_HA(12, "主备KCP"),
    ;


    OperateLogType(int value, String desc) {
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
