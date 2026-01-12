package com.trade.catalog.mapper.catalogUniverses;

import com.trade.catalog.entity.CatalogUniverseInstrumentId;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CatalogUniverseInstrumentsDao {
    int insertData(String universeId, String instrumentId);
}
