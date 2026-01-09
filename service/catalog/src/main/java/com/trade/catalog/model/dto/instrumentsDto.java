package com.trade.catalog.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class instrumentsDto {

    @Getter
    @Setter
    public static class Reqeust {
        private String market;
        private String exchange;
        private String keyword;
        private String isActive;
        private String limit;
        private String cursor;
    }
}
