package com.shaileshmishra.app.exception;

public class ErrorResponse {

    private String errorMessage;
    private int errorCode;
    private String message;

    public ErrorResponse(String errorMessage, int errorCode, String message) {
        this.errorMessage = errorMessage;
        this.errorCode = errorCode;
        this.message = message;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public String getMessage() {
        return message;
    }
}