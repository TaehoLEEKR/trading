package com.trade.md.service;

import com.trade.catalog.entity.CatalogInstruments;
import com.trade.catalog.mapper.instruments.CatalogInstrumentsDao;
import com.trade.common.component.KisTokenStore;
import com.trade.common.component.Snowflake;
import com.trade.common.config.KisAuthConfig;
import com.trade.common.config.localCommunication;
import com.trade.common.constant.ErrorCode;
import com.trade.common.constant.JobStatus;
import com.trade.common.constant.staticConst;
import com.trade.common.exception.CustomException;
import com.trade.common.model.token.TokenResponse;
import com.trade.common.util.CallClient;
import com.trade.common.util.JsonUtil;
import com.trade.md.entity.MdIngestJob;
import com.trade.md.model.dto.KisDailyPriceResponse;
import com.trade.md.model.dto.Md;
import com.trade.md.repository.MdIngestJobRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static com.trade.common.constant.staticConst.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class MdService {

    private final CatalogInstrumentsDao catalogInstrumentsDao;
    private final MdIngestJobRepository mdIngestJobRepository;
    private final KisTokenStore kisTokenStore;
    private final CallClient callClient;
    private final localCommunication localCommunication;
    private final KisAuthConfig kisAuthConfig;

    private Map<String, String> newHeaders() {
        return new HashMap<>(staticConst.headers);
    }

    @Transactional
    public Md.ResponseBars KISDataParsingAndSavingToJob(Md.@Valid RequestBars request, String token) {
        Snowflake snowflake = new Snowflake();

        String instrumentId = request.getInstrumentId();
        String jobId = "JOB_" + BARS + snowflake.nextId() ;
        String resultCode,resultMsg;

        CatalogInstruments catalogInstruments = catalogInstrumentsDao.selectByPrimaryKey(instrumentId);

        MdIngestJob mdIngestJob = getMdIngestJob(jobId, catalogInstruments, JobStatus.RUNNING, null, null);

        saveJobStep(mdIngestJob);

        String kisAccessKey = getKisRedisAccessKey(token);
        String isCd = catalogInstruments.getSymbol();
        log.info("kisAccessKey : {}",kisAccessKey);

        Map<String,String> kisHeaders =  makeSendHeaders(kisAccessKey);
        log.debug("headers : {}",kisHeaders);

        String url = kisAuthConfig.url().inquireDailyPrice() + "?FID_COND_MRKT_DIV_CODE=" + KRX_FID_COND_MARKT_DIV_CODE + "&FID_INPUT_ISCD="+isCd +
                "&FID_PERIOD_DIV_CODE=" + staticConst.PERIOD_DIV_CODE_D + "&FID_ORG_ADJ_PRC=" + staticConst.ORG_ADJ_PRC_1;

        log.info("url : {}",url);

        CallClient.CallResult result = callClient.GET_WITH_HEADERS(url, kisHeaders);

        if(!result.headers().get("tr_id").getFirst().equals(staticConst.INGEST_TR_ID_DAILY)){
            throw new CustomException(ErrorCode.MD_JOB_TR_ID_WARN);
        }

        KisDailyPriceResponse responseRecord = JsonUtil.getInstance().decodeFromJson(result.body(), KisDailyPriceResponse.class);
        resultCode = responseRecord.rt_cd();
        resultMsg = responseRecord.msg1();

        log.info("rt_cd={}, msg={}", responseRecord.rt_cd(), responseRecord.msg1());
        log.info("rows={}", responseRecord.output() != null ? responseRecord.output().size() : 0);

        if(!(resultCode != null && resultCode.equals("0"))){

            mdIngestJob = getMdIngestJob(jobId, catalogInstruments, JobStatus.FAILED, resultCode , resultMsg);
            saveJobStep(mdIngestJob);

            throw new CustomException(ErrorCode.SERVER_ERROR);
        }


        KisDailyPriceResponse.Output first = responseRecord.output().getFirst();
        log.info("first date={}, close={}", first.stck_bsop_date(), first.stck_clpr());

        return null;
    }

    private static MdIngestJob getMdIngestJob(String jobId, CatalogInstruments catalogInstruments, JobStatus status, String resultCode, String resultMsg) {

        return MdIngestJob.builder()
                .jobId(jobId)
                .market(catalogInstruments.getMarket())
                .jobType(BARS)
                .status(status.getStatus())
                .startedAt(LocalDateTime.now())
                .errCode(resultCode)
                .errMsg(resultMsg)
                .build();
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
    public String getKisRedisAccessKey(String token) {
        TokenResponse cachedToken = kisTokenStore.getAccessToken();

        if (cachedToken != null && cachedToken.getAccessToken() != null) {
            return cachedToken.getAccessToken();
        } else {
            Map<String, String> headers = newHeaders();
            headers.put(staticConst.AUTHORIZATION, staticConst.BEARER + token);

            String result = callClient.POST(localCommunication.retryKisToken(), headers, "");

            TokenResponse tokenResponse = JsonUtil.getInstance().decodeFromJson(result, TokenResponse.class);
            kisTokenStore.storeAccessToken(tokenResponse);

            return tokenResponse.getAccessToken();
        }
    }

    public Map<String, String> makeSendHeaders(String token) {
        Map<String, String> headers = newHeaders();
        log.debug("makeSendHeaders : {}", headers);

        headers.put(staticConst.AUTHORIZATION, staticConst.BEARER + token);
        headers.put("custtype", staticConst.CUST_TYPE);
        headers.put("appkey", kisAuthConfig.key().app());
        headers.put("appsecret", kisAuthConfig.key().secret());
        headers.put("tr_id", staticConst.INGEST_TR_ID_DAILY);
        return headers;
    }
}
