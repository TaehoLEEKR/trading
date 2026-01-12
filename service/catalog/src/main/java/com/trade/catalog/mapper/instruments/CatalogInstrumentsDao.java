package com.trade.catalog.mapper.instruments;

import com.trade.catalog.entity.CatalogInstruments;
import com.trade.catalog.model.dto.instrumentsDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CatalogInstrumentsDao {
    List<CatalogInstruments> search(instrumentsDto.Request request);
    CatalogInstruments selectByPrimaryKey(String instrumentId);
}