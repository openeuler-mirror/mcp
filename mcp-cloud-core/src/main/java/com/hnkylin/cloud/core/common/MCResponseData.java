package com.hnkylin.cloud.core.common;

import lombok.Data;

@Data
public class MCResponseData<T> {
    private String status;
    /**
     * 请求结果数据
     */
    private T data;

    private String msg;

    private String message;


}
