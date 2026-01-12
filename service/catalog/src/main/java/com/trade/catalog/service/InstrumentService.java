package com.trade.catalog.service;

import com.trade.catalog.entity.CatalogInstruments;
import com.trade.catalog.mapper.instruments.CatalogInstrumentsDao;
import com.trade.catalog.model.dto.instrumentsDto;
import com.trade.common.constant.ErrorCode;
import com.trade.common.exception.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class InstrumentService {

    private final CatalogInstrumentsDao catalogInstrumentsDao;

    public instrumentsDto.Response getInstruments(instrumentsDto.Request req) {
        if (req == null) throw new CustomException(ErrorCode.NOT_FOUND);

        int size = (req.getSize() == null || req.getSize() <= 0) ? 20 : Math.min(req.getSize(), 100);
        int offset = (req.getOffset() == null || req.getOffset() < 0) ? 0 : req.getOffset();

        req.setSize(size);
        req.setOffset(offset);

        req.setSize(size + 1);

        List<CatalogInstruments> rows = catalogInstrumentsDao.search(req);

        boolean hasNext = rows.size() > size;
        if (hasNext) rows = rows.subList(0, size);

        List<instrumentsDto.Item> items = rows.stream()
                .map(r -> instrumentsDto.Item.builder()
                        .instrumentId(r.getInstrumentId())
                        .market(r.getMarket())
                        .exchange(r.getExchange())
                        .symbol(r.getSymbol())
                        .name(r.getName())
                        .currency(r.getCurrency())
                        .isActive(r.getIsActive())
                        .build())
                .toList();

        return instrumentsDto.Response.builder()
                .items(items)
                .size(size)
                .offset(offset)
                .hasNext(hasNext)
                .build();
    }

    public CatalogInstruments getInstrumentByOne(String instrumentId) {
        if(instrumentId == null) throw new CustomException(ErrorCode.NOT_FOUND);
        return catalogInstrumentsDao.selectByPrimaryKey(instrumentId);
    }
}
