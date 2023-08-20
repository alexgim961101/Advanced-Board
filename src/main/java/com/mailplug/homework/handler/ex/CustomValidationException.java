package com.mailplug.homework.handler.ex;

import lombok.Getter;

import java.util.Map;

@Getter
public class CustomValidationException extends RuntimeException{
    private Map<String, String> erroeMap;
    private boolean isSuccess;

    public CustomValidationException(String message, Map<String, String> erroeMap, boolean isSuccess) {
        super(message);
        this.erroeMap = erroeMap;
        this.isSuccess = isSuccess;
    }
}
