package com.trade.md.model.dto;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class IngestDto {

    @Builder
    @Data
    public static class Bar {
        private String ts;   // "2026-01-13T00:00:00"
        private String o;
        private String h;
        private String l;
        private String c;
        private String v;
    }

    @Builder
    @Data
    public static class BarsResponse {
        private String instrumentId;
        private String intervalCd;
        private String from;
        private String to;
        private int size;
        private String order;
        private int count;
        private java.util.List<Bar> bars;
    }
}
