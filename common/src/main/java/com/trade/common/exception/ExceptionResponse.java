package com.trade.common.exception;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.trade.common.constant.ErrorCode;
import lombok.Getter;

@Getter
public class ExceptionResponse {

    @JsonProperty("errorCode")
    private final String errorCode;

    private final String message;

    public ExceptionResponse(ErrorCode errorCode, String message) {
        this.errorCode = String.valueOf(errorCode.getErrorCode()); // 코드 값을 문자열로 변환
        this.message = message;
    }
}
