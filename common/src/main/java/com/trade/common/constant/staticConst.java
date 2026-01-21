package com.trade.common.constant;

import lombok.experimental.UtilityClass;

import java.util.Map;

@UtilityClass
public class staticConst {
    public static final String HEADER_REQUEST_ID = "X-Request-Id";
    public static final String HEADER_RUN_ID = "X-Run-Id";
    // Headers
    public static final String AUTHORIZATION = "Authorization";
    public static final String X_INTERNAL_TOKEN = "X-Internal-Token";
    public static final String BEARER = "Bearer ";

    public static Map<String,String> headers = Map.of("Content-Type","application/json; charset=utf-8");
    // ROLE
    public static final String ADMIN = "ADMIN";
    public static final String USER = "USER";

    //MD
    public static final String BARS = "BARS";
    public static final String CUST_TYPE = "P";
    // 조건 시장 분류 코드
    public static final String KRX_FID_COND_MARKT_DIV_CODE = "J";
    public static final String NXT_FID_COND_MARKT_DIV_CODE = "NX";
    public static final String UN_FID_COND_MARKT_DIV_CODE = "UN";

    public static final String PERIOD_DIV_CODE_D = "D" ;
    public static final String PERIOD_DIV_CODE_W = "w" ;
    public static final String PERIOD_DIV_CODE_M = "M" ;

    public static final String ORG_ADJ_PRC_1 = "1";
    public static final String ORG_ADJ_PRC_0 = "0";

    //주식현재가 시세 tr_id
    public static final String INGEST_TR_ID = "FHKST01010100";
    //주식현재가 일자별
    public static final String INGEST_TR_ID_DAILY = "FHKST01010400";
}
