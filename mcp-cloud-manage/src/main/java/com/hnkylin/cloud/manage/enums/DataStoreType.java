package com.hnkylin.cloud.manage.enums;

/**
 * mc 存储类型
 */
public enum DataStoreType {

    LOCAL(0, "LOCAL"),
    NFS(1, "NFS"),
    DISTRIBUTED(2, "Distributed"),
    CIFS(3, "CIFS"),
    IPSAN(4, "IPSAN"),
    FCSAN(5, "FCSAN");


    DataStoreType(int id, String name) {
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
