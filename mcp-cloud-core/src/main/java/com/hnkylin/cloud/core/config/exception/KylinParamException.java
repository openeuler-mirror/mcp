package com.hnkylin.cloud.core.config.exception;

public class KylinParamException extends RuntimeException {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private String message;

    public KylinParamException() {
        super();
    }

    public KylinParamException(String message) {
        super();
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
