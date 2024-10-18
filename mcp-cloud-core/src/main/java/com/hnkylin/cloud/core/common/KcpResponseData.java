package com.hnkylin.cloud.core.common;

import lombok.Data;

@Data
public class KcpResponseData<T> {
    /**
     * 请求结果编码
     */
    private int code;
    /**
     * 请求结果描述
     */
    private String desc;
    /**
     * 请求结果数据
     */
    private T data;


}
