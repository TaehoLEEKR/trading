package com.trade.common.exception;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.trade.common.constant.ErrorCode;
import com.trade.common.response.ApiResponse;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
@Component
@RequiredArgsConstructor
@Slf4j
public class CustomExceptionHandler {

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ApiResponse<Object>> handleCustomException(CustomException e) {
        log.error("EXCEPTION : {}", e.getMessage());

        ApiResponse<Object> errorResponse = ApiResponse.error(e.getErrorCode());

        return ResponseEntity
                .status(e.getErrorCode().getHttpStatus())
                .body(errorResponse);
    }


    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleMethodArgumentNotValid(MethodArgumentNotValidException e) {

        try {
            log.error("VALIDATION ERROR : {}", e.getMessage());
            Map<String, String> errors = new LinkedHashMap<>();
            for (FieldError fe : e.getBindingResult().getFieldErrors()) {
                errors.put(fe.getField(), fe.getDefaultMessage());
            }

            return ResponseEntity
                    .status(ErrorCode.VALIDATION_ERROR.getHttpStatus())
                    .body(ApiResponse.error(ErrorCode.VALIDATION_ERROR, errors));
        }catch (Exception ex) {
            log.error("VALIDATION ERROR catch : {}", ex.getMessage());
            return null;
        }
    }




}
