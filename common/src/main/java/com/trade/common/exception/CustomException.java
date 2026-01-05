package com.trade.common.exception;

import com.trade.common.constant.ErrorCode;
import lombok.Getter;

@Getter
public class CustomException extends RuntimeException {

    private final ErrorCode errorCode;
    private final String additionalMessage;

    public CustomException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
        this.additionalMessage = null;
    }

    public CustomException(ErrorCode errorCode, String additionalMessage) {
        super(additionalMessage == null
                ? errorCode.getMessage()
                : errorCode.getMessage() + " - " + additionalMessage);
        this.errorCode = errorCode;
        this.additionalMessage = additionalMessage;
    }

    @Override
    public String getMessage() {
        if (additionalMessage == null) {
            return errorCode.getMessage();
        }
        return errorCode.getMessage() + " - " + additionalMessage;
    }

}
