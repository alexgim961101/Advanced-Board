package com.mailplug.homework.util;

import com.mailplug.homework.handler.ex.CustomValidationException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.HashMap;
import java.util.Map;

public class ValidCheck {
    public static void validCheck(BindingResult bindingResult) {
        if(bindingResult.hasErrors()) {
            Map<String, String> errorMap = new HashMap<>();
            for(FieldError fieldError : bindingResult.getFieldErrors()) {
                errorMap.put(fieldError.getField(), fieldError.getDefaultMessage());
            }
            throw new CustomValidationException(SystemString.INVALID_INPUT.getMessage(), errorMap, SystemString.INVALID_INPUT.isSuccess());
        }
    }
}
