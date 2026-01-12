package com.trade.catalog.model.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class instrumentsDto {

    @Getter @Setter
    public static class Request {
        private String market = "KR_STOCK";
        private String exchange;
        private Integer isActive = 1;
        private String keyword;
        private Integer size = 20;
        private Integer offset = 0;
    }

    @Getter @Builder
    public static class Item {
        private String instrumentId;
        private String market;
        private String exchange;
        private String symbol;
        private String name;
        private String currency;
        private Integer isActive;
    }

    @Getter @Builder
    public static class Response {
        private List<Item> items;
        private Integer size;
        private Integer offset;
        private Boolean hasNext;
    }
}
