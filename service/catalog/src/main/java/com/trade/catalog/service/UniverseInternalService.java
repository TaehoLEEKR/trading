package com.trade.catalog.service;

import com.trade.catalog.mapper.catalogUniverses.CatalogUniverseInstrumentsDao;
import com.trade.catalog.model.dto.UniverseTargetDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UniverseInternalService {

    private final CatalogUniverseInstrumentsDao dao;

    public UniverseTargetDto.UniverseTargetsResponse getAutoIngestTargets(String intervalCd, String market, int limit) {
        var rows = dao.selectAutoIngestTargets(intervalCd, market, limit);
        return UniverseTargetDto.UniverseTargetsResponse.builder()
                .targets(rows)
                .count(rows.size())
                .build();
    }
}
