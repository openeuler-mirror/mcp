package com.hnkylin.cloud.core.common;

import lombok.Data;

@Data
public class BaseResult<T> {

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

    public BaseResult() {

    }

    public BaseResult(int code, String desc, T data) {
        this.code = code;
        this.desc = desc;
        this.data = data;
    }

    public static <T, R extends HttpCode> BaseResult<T> success(T data) {
        return new BaseResult<T>(R.SUCCESS, R.CodeEnum.SUCCESS.getDesc(), data);
    }

    public static <T, R extends HttpCode> BaseResult<T> success(T data, String desc) {
        return new BaseResult<T>(R.SUCCESS, desc, data);
    }


    public static <T, R extends HttpCode> BaseResult<T> paramError(String desc) {
        return new BaseResult<T>(R.PARAM_ERROR, desc, null);
    }


    public static <T, R extends HttpCode> BaseResult<T> error(String desc) {
        return new BaseResult<T>(R.SERVER_ERROR, desc, null);
    }


    public static <T, R extends HttpCode> BaseResult<T> tokenExpire(String desc) {
        return new BaseResult<T>(R.TOKEN_EXPIRE, desc, null);
    }

    public static <T, R extends HttpCode> BaseResult<T> customSuccess(String desc, T data) {
        return new BaseResult<T>(R.SUCCESS, desc, data);
    }


}
