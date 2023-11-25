package com.hnkylin.cloud.core.enums;

public enum NetworkSecurityPolicy {

    NONE("0", "无"),
    SECURITY_GROUP("1", "安全组"),
    VIRTUAL_FIREWALL("2", "虚拟防火墙");

    NetworkSecurityPolicy(String value, String desc) {
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
