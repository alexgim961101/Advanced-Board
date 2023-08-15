package com.mailplug.homework.handler.ex;

import java.util.Map;

public class CustomValidationException extends RuntimeException{
    private Map<String, String> erroeMap;
    private boolean isSuccess;

    public CustomValidationException(String message, Map<String, String> erroeMap, boolean isSuccess) {
        super(message);
        this.erroeMap = erroeMap;
        this.isSuccess = isSuccess;
    }
}
