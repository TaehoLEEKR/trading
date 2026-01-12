package com.trade.catalog.model.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class UniverseDto {

    @Getter @Setter
    public static class Create{
        private String market = "KR_STOCK";
        private String name;
        private String description;
    }

    @Getter @Setter
    public static class Request{
        private String instrumentId;
    }


    @Getter @Setter @NoArgsConstructor
    public static class InstrumentItem {
        private String instrumentId;
        private String market;
        private String exchange;
        private String symbol;
        private String name;
        private String currency;
        private Integer isActive;
        private LocalDateTime addedAt;
    }

    @Getter @Builder
    public static class InstrumentListResponse {
        private String universeId;
        private Integer size;
        private Integer offset;
        private Boolean hasNext;
        private List<InstrumentItem> items;
    }
}
