package com.trade.md.mapper;

import com.trade.catalog.entity.CatalogInstruments;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UniverseInstrumentDao {
    List<CatalogInstruments> selectInstrumentsByUniverse(
            @Param("universeId") String universeId,
            @Param("limit") int limit
    );
}