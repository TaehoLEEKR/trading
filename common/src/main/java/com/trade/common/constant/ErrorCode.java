package com.trade.common.constant;


import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR,-999, "API 사용중 ERROR 발생"),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED,-401, "Unauthorized"),
    NOT_FOUND(HttpStatus.NOT_FOUND,-404, "NOT FOUND DATA"),

    //Validation
    CONFLICT_DATA(HttpStatus.CONFLICT,-409, "중복된 데이터가 있습니다."),
    VALIDATION_ERROR(HttpStatus.BAD_REQUEST,-400, "Validation Error"),
    VALIDATION_PW_ERROR(HttpStatus.BAD_REQUEST,-400, "Validation PW Error"),


    FAILED_TO_CALL_CLIENT(HttpStatus.BAD_GATEWAY,-301, "HTTP CALL FAILED"),
    CALL_REQUEST_BODY_NULL(HttpStatus.BAD_REQUEST,-302,"HTTP CALL REQEUST BODY NULL" ),

    //KIS
    TOKEN_DISPOSAL_ERROR(HttpStatus.BAD_REQUEST,-304,"토큰 폐기중 에러 발생"),
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
