package com.portfolio.magnum.exception;

public class ConverterError {

    private String message;
    private Integer httpStatus;
    private String uri;

    public ConverterError(String message, Integer httpStatus, String uri) {
        this.message = message;
        this.httpStatus = httpStatus;
        this.uri = uri;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Integer getHttpStatus() {
        return httpStatus;
    }

    public void setHttpStatus(Integer httpStatus) {
        this.httpStatus = httpStatus;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }
}
