package com.mailplug.homework.handler.ex;

public class CustomApiException extends RuntimeException{
    private boolean isSuccess;
    public CustomApiException(boolean isSuccess, String message) {
        super(message);
        this.isSuccess = isSuccess;
    }
}
