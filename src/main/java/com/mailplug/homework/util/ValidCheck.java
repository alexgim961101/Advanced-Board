package com.mailplug.homework.util;

import com.mailplug.homework.handler.ex.CustomValidationException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.HashMap;
import java.util.Map;

public class ValidCheck {
    public static void validCheck(BindingResult bindingResult) throws CustomValidationException{
        if(bindingResult.hasErrors()) {
            Map<String, String> errorMap = new HashMap<>();
            for(FieldError fieldError : bindingResult.getFieldErrors()) {
                errorMap.put(fieldError.getField(), fieldError.getDefaultMessage());
            }
            throw new CustomValidationException(SystemString.INVALID_INPUT.getMessage(), errorMap, SystemString.INVALID_INPUT.isSuccess());
        }
    }

    public static void validCheck(Long...id) throws CustomValidationException{
        for(Long num : id) {
            if(num < 1L) throw new CustomValidationException(SystemString.INVALID_INPUT.getMessage(), null, SystemString.INVALID_INPUT.isSuccess());
        }
    }

    public static void validCheck(Long longId, Integer...intId) throws CustomValidationException{
        if(longId < 1L) throw new CustomValidationException(SystemString.INVALID_INPUT.getMessage(), null, SystemString.INVALID_INPUT.isSuccess());
        for(Integer num : intId) {
            if(num < 0L) throw new CustomValidationException(SystemString.INVALID_INPUT.getMessage(), null, SystemString.INVALID_INPUT.isSuccess());
        }
    }

}
