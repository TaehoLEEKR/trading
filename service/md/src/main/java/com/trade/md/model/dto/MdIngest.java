package com.trade.md.model.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

public class MdIngest {

    @Data
    public static class UniverseBarsRequest {
        @NotBlank
        private String universeId;

        private String intervalCd = "1d";

        private String from;
        private String to;

        private Integer maxInstruments = 300;
    }

    @Builder
    @Data
    public static class UniverseBarsResponse {
        private String jobId;
        private String universeId;
        private String intervalCd;

        // 대상 종목 수
        private int instruments;
        // KIS에서 받은 row 총합
        private int fetched;
        // md_bars upsert row 총합
        private int upserted;
        // 실패 종목 수
        private int failed;

        @Builder.Default
        private List<Failure> failures = new ArrayList<>();
    }

    @Builder
    @Data
    public static class Failure {
        private String instrumentId;
        private String symbol;
        private String errCode;
        private String errMsg;
    }
}