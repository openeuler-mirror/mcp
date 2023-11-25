package com.hnkylin.cloud.core.config.exception;

public class KylinMcRequestException extends RuntimeException {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private String message;

    public KylinMcRequestException() {
        super();
    }

    public KylinMcRequestException(String message) {
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
