package com.trade.catalog.service;

import com.trade.catalog.entity.CatalogUnivers;
import com.trade.catalog.model.dto.UniverseDto;
import com.trade.catalog.repository.CatalogUniversRepository;
import com.trade.common.component.JwtProvider;
import com.trade.common.component.Snowflake;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UniversesService {

    private final JwtProvider jwtProvider;
    private final CatalogUniversRepository catalogUniversRepository;
    private Snowflake snowflake;

    public CatalogUnivers createUniverse(String token, UniverseDto.Create request) {

        String userId = jwtProvider.getUserId(token);
        log.info("userId : {}", userId);
        String universeId = snowflake.nextId() + "";

        CatalogUnivers catalogUnivers = CatalogUnivers.builder()
                .universeId(universeId)
                .userId(userId)
                .market(request.getMarket())
                .name(request.getName())
                .description(request.getDescription())
                .build();

        return catalogUniversRepository.save(catalogUnivers);
    }
}
