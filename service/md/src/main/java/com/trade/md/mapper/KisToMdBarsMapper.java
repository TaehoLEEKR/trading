package com.trade.md.mapper;

import com.trade.md.model.dto.KisDailyPriceResponse;
import com.trade.md.model.dto.MdBarRow;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;

public class KisToMdBarsMapper {

    private static final DateTimeFormatter BASIC = DateTimeFormatter.BASIC_ISO_DATE; // yyyyMMdd

    public static List<MdBarRow> toDailyBars(
            String instrumentId,
            String market,
            String currency,
            KisDailyPriceResponse response
    ) {
        if (response == null || response.output() == null || response.output().isEmpty()) {
            return Collections.emptyList();
        }

        return response.output().stream().map(o -> {
            LocalDate d = LocalDate.parse(o.stck_bsop_date(), BASIC);

            return MdBarRow.builder()
                    .instrumentId(instrumentId)
                    .market(market)
                    .intervalCd("1d")
                    .ts(d.atStartOfDay())
                    .o(new BigDecimal(o.stck_oprc()))
                    .h(new BigDecimal(o.stck_hgpr()))
                    .l(new BigDecimal(o.stck_lwpr()))
                    .c(new BigDecimal(o.stck_clpr()))
                    .v(new BigDecimal(o.acml_vol()))
                    .currency(currency)
                    .build();
        }).toList();
    }
}
