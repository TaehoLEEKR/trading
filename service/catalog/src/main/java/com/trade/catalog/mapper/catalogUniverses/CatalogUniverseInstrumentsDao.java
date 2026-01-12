package com.trade.catalog.mapper.catalogUniverses;

import com.trade.catalog.entity.CatalogUniverseInstrumentId;
import com.trade.catalog.model.dto.UniverseDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CatalogUniverseInstrumentsDao {
    int insertData(String universeId, String instrumentId);

    List<UniverseDto.InstrumentItem> selectUniverseInstruments(@Param("universeId") String universeId,
                                                               @Param("limit") int limit,
                                                               @Param("offset") int offset);
}
