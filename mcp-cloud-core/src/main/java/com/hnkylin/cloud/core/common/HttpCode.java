package com.hnkylin.cloud.core.common;

public class HttpCode {

    public final static int SUCCESS = 200;
    public final static int FAIL = 3000;
    public final static int PARAM_ERROR = 4000;
    public final static int AUTHORIZATION_ERROR = 4100;
    public final static int TOKEN_EXPIRE = 401;
    public final static int ACCESS_KEY_ERROR = 4200;
    public final static int SERVER_ERROR = 5000;

    public static enum CodeEnum {
        SUCCESS(200, "请求成功"), FAIL(3000, "请求失败"), PARAM_ERROR(4000, "参数错误"), AUTHORIZATION_ERROR(4100, "权限不足"),
        TOKEN_EXPIRE(401, "Token过期"), ACCESS_KEY_ERROR(4200, "密钥错误"), SERVER_ERROR(5000, "服务器错误"),
        ;

        private int code;
        private String desc;

        CodeEnum(int code, String desc) {
            this.code = code;
            this.desc = desc;
        }

        public int getCode() {
            return this.code;
        }

        public String getDesc() {
            return this.desc;
        }
    }

}
