package com.trade.common.constant;

import lombok.experimental.UtilityClass;

import java.util.HashMap;
import java.util.Map;

@UtilityClass
public class staticConst {
    public static final String HEADER_REQUEST_ID = "X-Request-Id";
    public static final String HEADER_RUN_ID = "X-Run-Id";
    // Headers
    public static final String AUTHORIZATION = "Authorization";
    public static final String BEARER = "Bearer ";
    public static final Map<String,String> headers = Map.of("Content-Type","application/json");
    // ROLE
    public static final String ADMIN = "ADMIN";
    public static final String USER = "USER";

    //MD
    public static final String BARS = "BARS";
}
