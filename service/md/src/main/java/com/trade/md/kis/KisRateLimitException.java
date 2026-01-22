package com.trade.md.kis;

public class KisRateLimitException extends KisApiException{
    public KisRateLimitException(String msgCd, String message) {
        super(msgCd, message);
    }
}
