package com.trade.md.kis;

public class KisInvalidTokenException extends KisApiException{
    public KisInvalidTokenException(String msgCd, String message) {
        super(msgCd, message);
    }
}
