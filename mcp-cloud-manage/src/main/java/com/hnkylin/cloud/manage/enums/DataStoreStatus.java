package com.hnkylin.cloud.manage.enums;

/**
 * mc 存储状态
 */
public enum DataStoreStatus {

    NORMAL(0, "NORMAL"),
    WARNING(1, "WARNING"),
    ERROR(2, "ERROR");


    DataStoreStatus(int id, String name) {
        this.id = id;
        this.name = name;
    }

    private int id;
    private String name;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
