package com.trade.md.kis;

public class KisApiException extends RuntimeException{
    private final String msgCd;

    public KisApiException(String msgCd, String message) {
        super(message);
        this.msgCd = msgCd;
    }

    public String getMsgCd() {
        return msgCd;
    }
}
