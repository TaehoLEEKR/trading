package com.trade.catalog.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
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
}
