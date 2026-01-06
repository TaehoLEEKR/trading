package com.trade.auth.record;

import com.trade.auth.model.LoginDto;

public record LoginResult(LoginDto.Response body, String refreshToken) {}
