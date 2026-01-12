package com.trade.md.service;

import com.trade.catalog.entity.CatalogInstruments;
import com.trade.catalog.mapper.instruments.CatalogInstrumentsDao;
import com.trade.common.component.KisTokenStore;
import com.trade.common.component.Snowflake;
import com.trade.common.constant.ErrorCode;
import com.trade.common.constant.JobStatus;
import com.trade.common.exception.CustomException;
import com.trade.common.model.token.TokenResponse;
import com.trade.md.entity.MdIngestJob;
import com.trade.md.model.dto.Md;
import com.trade.md.repository.MdIngestJobRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static com.trade.common.constant.staticConst.BARS;

@Service
@RequiredArgsConstructor
@Slf4j
public class MdService {

    private final CatalogInstrumentsDao catalogInstrumentsDao;
    private final MdIngestJobRepository mdIngestJobRepository;
    private final KisTokenStore kisTokenStore;

    @Transactional
    public Md.ResponseBars KISDataParsingAndSavingToJob(Md.@Valid RequestBars request) {
        Snowflake snowflake = new Snowflake();

        String instrumentId = request.getInstrumentId();
        String jobId = snowflake.nextId() + "JOB_" + BARS;
        CatalogInstruments catalogInstruments = catalogInstrumentsDao.selectByPrimaryKey(instrumentId);

        MdIngestJob mdIngestJob = MdIngestJob.builder()
                .jobId(jobId)
                .market(catalogInstruments.getMarket())
                .jobType(BARS)
                .status(JobStatus.RUNNING.getStatus())
                .startedAt(LocalDateTime.now())
                .build();

        saveJobStep(mdIngestJob);

        return null;
    }

    @Transactional
    public void saveJobStep(MdIngestJob mdIngestJob) {
        try {
            mdIngestJobRepository.save(mdIngestJob);
        }catch (CustomException e){
            throw new CustomException(ErrorCode.SERVER_ERROR);
        }
    }

    @Transactional
    public String getKisRedisAccessKey(){
        TokenResponse cachedToken = kisTokenStore.getAccessToken();
        if (cachedToken != null && cachedToken.getAccessToken() != null) {
            return cachedToken.getAccessToken();
        }
        return null;
    }
}
