package com.hnkylin.cloud.manage.enums;

/**
 * mc 存储用途
 */
public enum DataStoreUsage {

    MANAGE(0, "Manage"),
    DATA(1, "Data");


    DataStoreUsage(int id, String name) {
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
