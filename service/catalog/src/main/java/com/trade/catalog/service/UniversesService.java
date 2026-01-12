package com.trade.catalog.service;

import com.trade.catalog.entity.CatalogUnivers;
import com.trade.catalog.mapper.catalogUniverses.CatalogUniverseInstrumentsDao;
import com.trade.catalog.model.dto.UniverseDto;
import com.trade.catalog.repository.CatalogUniversRepository;
import com.trade.common.component.JwtProvider;
import com.trade.common.component.Snowflake;
import com.trade.common.constant.ErrorCode;
import com.trade.common.exception.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UniversesService {

    private final JwtProvider jwtProvider;
    private final CatalogUniversRepository catalogUniversRepository;
    private final CatalogUniverseInstrumentsDao catalogUniverseInstrumentsDao;

    public CatalogUnivers createUniverse(String token, UniverseDto.Create request) {

        Snowflake snowflake = new Snowflake();

        String userId = jwtProvider.getUserId(token);
        log.info("userId : {}", userId);
        String universeId = snowflake.nextId() + "";
        log.info("universeId : {}", universeId);

        CatalogUnivers catalogUnivers = CatalogUnivers.builder()
                .universeId(universeId)
                .userId(userId)
                .market(request.getMarket())
                .name(request.getName())
                .description(request.getDescription())
                .build();

        return catalogUniversRepository.save(catalogUnivers);
    }

    @Transactional
    public int addInstrumentToUniverse(String universeId, String instrumentId) {
        Optional<CatalogUnivers> universe = catalogUniversRepository.findByUniverseId(universeId);
        if(universe.isPresent()){
            return catalogUniverseInstrumentsDao.insertData(universeId, instrumentId);
        }else{
            throw new CustomException(ErrorCode.NOT_FOUND);
        }
    }
}
