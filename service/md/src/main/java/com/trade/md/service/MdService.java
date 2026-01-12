package com.trade.md.service;

import com.trade.catalog.entity.CatalogInstruments;
import com.trade.catalog.mapper.instruments.CatalogInstrumentsDao;
import com.trade.common.component.Snowflake;
import com.trade.md.model.dto.Md;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static com.trade.common.constant.staticConst.BARS;

@Service
@RequiredArgsConstructor
@Slf4j
public class MdService {

    private final CatalogInstrumentsDao catalogInstrumentsDao;

    public Md.ResponseBars KISDataParsingAndSavingToJob(Md.@Valid RequestBars request) {
        Snowflake snowflake = new Snowflake();

        String instrumentId = request.getInstrumentId();
        String jobId = snowflake.nextId() + "JOB_" + BARS;
        CatalogInstruments catalogInstruments = catalogInstrumentsDao.selectByPrimaryKey(instrumentId);

        return null;
    }
}
