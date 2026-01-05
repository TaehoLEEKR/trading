package com.trade.common.constant;

import lombok.Getter;

@Getter
public enum ResponseCode {

    //성공
    SUCCESS(200, "SUCCESS");

    private final int code;
    private final String message;

    ResponseCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
