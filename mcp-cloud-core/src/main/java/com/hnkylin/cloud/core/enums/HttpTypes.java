package com.hnkylin.cloud.core.enums;

public enum HttpTypes {

    HTTP("http://"),
    HTTPS("https://");


    HttpTypes(String value) {
        this.value = value;
    }

    private String value;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}



