package com.trade.common.constant;


import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR,-999, "API 사용중 ERROR 발생"),
    FAILED_TO_CALL_CLIENT(HttpStatus.BAD_GATEWAY,-301, "HTTP CALL FAILED"),
    CALL_REQUEST_BODY_NULL(HttpStatus.BAD_REQUEST,-302,"HTTP CALL REQEUST BODY NULL" ),

    ;

    private final HttpStatus httpStatus;
    private final int ErrorCode;
    private final String message;

    ErrorCode(HttpStatus httpStatus, int errorCode, String message) {

        ErrorCode = errorCode;
        this.httpStatus = httpStatus;
        this.message = message;
    }
}
