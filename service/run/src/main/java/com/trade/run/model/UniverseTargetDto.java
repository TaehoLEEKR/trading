package com.trade.run.model;

import java.util.List;

public class UniverseTargetDto {

    public record UniverseTargetsQuery(String intervalCd, String market, Integer limit) {}

    public record UniverseTargetsResponse(List<Target> targets, int count) {
        public record Target(
                String universeId,
                String market,
                String intervalCd,
                Integer maxInstruments
        ) {}
    }
}