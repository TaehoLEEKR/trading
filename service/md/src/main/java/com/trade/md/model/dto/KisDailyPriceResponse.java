package com.trade.md.model.dto;

import java.util.List;

public record KisDailyPriceResponse(
        List<Output> output,
        String rt_cd,
        String msg_cd,
        String msg1
) {
    public record Output(
            String stck_bsop_date,
            String stck_oprc,
            String stck_hgpr,
            String stck_lwpr,
            String stck_clpr,
            String acml_vol,
            String prdy_vrss_vol_rate,
            String prdy_vrss,
            String prdy_vrss_sign,
            String prdy_ctrt,
            String hts_frgn_ehrt,
            String frgn_ntby_qty,
            String flng_cls_code,
            String acml_prtt_rate
    ) {}
}