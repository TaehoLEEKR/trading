package com.trade.md.model.dto;

import lombok.Builder;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
public record MdBarRow(
        String instrumentId,
        String market,
        String intervalCd,
        LocalDateTime ts,    // 2026-01-13 00:00:00
        BigDecimal o,
        BigDecimal h,
        BigDecimal l,
        BigDecimal c,
        BigDecimal v,
        String currency
) {}
