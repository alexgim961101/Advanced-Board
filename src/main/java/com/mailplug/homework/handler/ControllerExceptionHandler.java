package com.mailplug.homework.handler;

import com.mailplug.homework.handler.ex.CustomApiException;
import com.mailplug.homework.handler.ex.CustomValidationException;
import com.mailplug.homework.web.dto.CMRespDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;

@RestController
@ControllerAdvice
public class ControllerExceptionHandler {
    @ExceptionHandler(CustomValidationException.class)
    public ResponseEntity<?> validationEx(CustomValidationException ex) {
        return new ResponseEntity<>(new CMRespDto<>(HttpStatus.BAD_REQUEST.value(), ex.getMessage(), ex.getErroeMap()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(CustomApiException.class)
    public ResponseEntity<?> apiEx(CustomApiException ex) {
        return new ResponseEntity<>(new CMRespDto<>(HttpStatus.BAD_REQUEST.value(), ex.getMessage(), null), HttpStatus.BAD_REQUEST);
    }
}
