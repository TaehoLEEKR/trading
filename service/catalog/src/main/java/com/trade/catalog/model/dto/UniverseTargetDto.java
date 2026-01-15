package com.trade.catalog.model.dto;


import lombok.*;

import java.util.List;

@Getter
@Setter
public class UniverseTargetDto {
    @Getter @Setter
    public static class UniverseTargetsQuery {
        private String market;       // optional
        private String intervalCd;   // default 1d
        private Integer limit;       // default 1000
    }


    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UniverseTarget {
        private String universeId;
        private String market;
        private String intervalCd;
        private Integer maxInstruments;
    }


    @Builder
    @Getter
    public static class UniverseTargetsResponse {
        private List<UniverseTarget> targets;
        private int count;
    }
}
