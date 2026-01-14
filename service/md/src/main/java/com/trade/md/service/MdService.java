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
import com.trade.md.mapper.KisToMdBarsMapper;
import com.trade.md.mapper.MdBarsDao;
import com.trade.md.model.dto.IngestDto;
import com.trade.md.model.dto.KisDailyPriceResponse;
import com.trade.md.model.dto.Md;
import com.trade.md.model.dto.MdBarRow;
import com.trade.md.repository.MdIngestJobRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
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
    private final MdBarsDao mdBarsDao;

    private Map<String, String> newHeaders() {
        return new HashMap<>(staticConst.headers);
    }

    @Transactional
    public Md.ResponseBars ingestDailyBars(Md.@Valid RequestBars request, String token) {
        Snowflake snowflake = new Snowflake();

        String instrumentId = request.getInstrumentId();
        String jobId = "mdjob_" + snowflake.nextId();

        CatalogInstruments inst = catalogInstrumentsDao.selectByPrimaryKey(instrumentId);
        if (inst == null) throw new CustomException(ErrorCode.NOT_FOUND, "instrument not found");


        MdIngestJob job = MdIngestJob.builder()
                .jobId(jobId)
                .market(inst.getMarket())
                .jobType(BARS)
                .status(JobStatus.RUNNING.getStatus())
                .startedAt(LocalDateTime.now())
                .build();
//        mdIngestJobRepository.save(job);
        start(job);

        try {
            String kisAccessKey = getKisRedisAccessKey(token);
            Map<String, String> kisHeaders = makeSendHeaders(kisAccessKey);

            String url = kisAuthConfig.url().inquireDailyPrice()
                    + "?FID_COND_MRKT_DIV_CODE=" + KRX_FID_COND_MARKT_DIV_CODE
                    + "&FID_INPUT_ISCD=" + inst.getSymbol()
                    + "&FID_PERIOD_DIV_CODE=" + PERIOD_DIV_CODE_D
                    + "&FID_ORG_ADJ_PRC=" + ORG_ADJ_PRC_1;

            log.info("KIS URL: {}", url);

            // KIS 호출
            CallClient.CallResult result = callClient.GET_WITH_HEADERS(url, kisHeaders);

            if (result.headers() != null && result.headers().get("tr_id") != null) {
                String trId = result.headers().get("tr_id").getFirst();
                if (!INGEST_TR_ID_DAILY.equals(trId)) {
                    log.warn("tr_id mismatch. expected={}, actual={}", INGEST_TR_ID_DAILY, trId);
                }
            }

            KisDailyPriceResponse kis = JsonUtil.getInstance()
                    .decodeFromJson(result.body(), KisDailyPriceResponse.class);

            if (kis == null || kis.rt_cd() == null || !"0".equals(kis.rt_cd())) {
                // job FAILED 업데이트
                failed(jobId, kis != null ? kis.rt_cd() : "null", kis != null ? kis.msg1() : "null response");
                throw new CustomException(ErrorCode.SERVER_ERROR, "KIS failed: " + (kis != null ? kis.msg1() : "null"));
            }

            int fetched = (kis.output() == null) ? 0 : kis.output().size();

            // md_bars rows 변환
            List<MdBarRow> bars = KisToMdBarsMapper.toDailyBars(
                    instrumentId,
                    inst.getMarket(),
                    inst.getCurrency(),
                    kis
            );

            int upserted = 0;
            if (!bars.isEmpty()) {
                mdBarsDao.upsertBars(bars);
                upserted = bars.size();
            }

            success(jobId);

            return Md.ResponseBars.builder()
                    .jobId(jobId)
                    .instrumentId(instrumentId)
                    .market(inst.getMarket())
                    .intervalCd("1d")
                    .fetched(fetched)
                    .upserted(upserted)
                    .status(JobStatus.SUCCESS.getStatus())
                    .message(kis.msg1())
                    .build();

        } catch (Exception e) {
            failed(jobId, "EXCEPTION", e.getMessage());
            if (e instanceof CustomException ce) throw ce;
            throw new CustomException(ErrorCode.SERVER_ERROR, e.getMessage());
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void start(MdIngestJob job) {
        mdIngestJobRepository.save(job);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void success(String jobId) {
        MdIngestJob job = mdIngestJobRepository.findById(jobId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, "job not found"));
        job.setStatus(JobStatus.SUCCESS.getStatus());
        job.setEndedAt(LocalDateTime.now());
        mdIngestJobRepository.save(job);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void failed(String jobId, String errCode, String errMsg) {
                MdIngestJob job = mdIngestJobRepository.findById(jobId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, "job not found"));
        job.setStatus(JobStatus.FAILED.getStatus());
        job.setEndedAt(LocalDateTime.now());
        job.setErrCode(errCode);
        job.setErrMsg(errMsg);
        mdIngestJobRepository.save(job);
    }

//    private void updateJobSuccess(String jobId) {
//        MdIngestJob job = mdIngestJobRepository.findById(jobId)
//                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, "job not found"));
//        job.setStatus(JobStatus.SUCCESS.getStatus());
//        job.setEndedAt(LocalDateTime.now());
//        mdIngestJobRepository.save(job);
//    }
//
//    private void updateJobFailed(String jobId, String errCode, String errMsg) {
//        MdIngestJob job = mdIngestJobRepository.findById(jobId)
//                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, "job not found"));
//        job.setStatus(JobStatus.FAILED.getStatus());
//        job.setEndedAt(LocalDateTime.now());
//        job.setErrCode(errCode);
//        job.setErrMsg(errMsg);
//        mdIngestJobRepository.save(job);
//    }

    @Transactional
    public String getKisRedisAccessKey(String token) {
        TokenResponse cachedToken = kisTokenStore.getAccessToken();
        if (cachedToken != null && cachedToken.getAccessToken() != null) {
            return cachedToken.getAccessToken();
        }

        Map<String, String> headers = newHeaders();
        headers.put(staticConst.AUTHORIZATION, staticConst.BEARER + token);

        String result = callClient.POST(localCommunication.retryKisToken(), headers, "");
        TokenResponse tokenResponse = JsonUtil.getInstance().decodeFromJson(result, TokenResponse.class);
        kisTokenStore.storeAccessToken(tokenResponse);

        return tokenResponse.getAccessToken();
    }

    public Map<String, String> makeSendHeaders(String token) {
        Map<String, String> headers = newHeaders();
        headers.put("authorization", staticConst.BEARER + token);
        headers.put("custtype", staticConst.CUST_TYPE);
        headers.put("appkey", kisAuthConfig.key().app());
        headers.put("appsecret", kisAuthConfig.key().secret());
        headers.put("tr_id", staticConst.INGEST_TR_ID_DAILY);
        return headers;
    }

    public IngestDto.BarsResponse getBars(String instrumentId, String intervalCd, String from, String to, int size, String order) {

        if (instrumentId == null || instrumentId.isBlank()) {
            throw new CustomException(ErrorCode.NOT_FOUND, "instrumentId is required");
        }

        if (!"1d".equals(intervalCd)) {
            throw new CustomException(ErrorCode.NOT_FOUND, "1d for now");
        }

        if (size <= 0) {
            size = 200;
        }
        if (size > 1000){ // 걍 고정 혹시 모름
            size = 1000;
        }

        String ord = (order != null && order.equalsIgnoreCase("DESC")) ? "DESC" : "ASC";

        // 날짜를 ts로 변환 (일봉은 00:00:00 통일)
        String fromTs = from + " 00:00:00";
        String toTs   = to   + " 23:59:59";

        List<MdBarRow> rows = mdBarsDao.selectBars(instrumentId, intervalCd, fromTs, toTs, size, ord);

        List<IngestDto.Bar> bars = rows.stream().map(r ->
                IngestDto.Bar.builder()
                        .ts(r.ts().toString())
                        .o(r.o().toPlainString())
                        .h(r.h().toPlainString())
                        .l(r.l().toPlainString())
                        .c(r.c().toPlainString())
                        .v(r.v().toPlainString())
                        .build()
        ).toList();

        return IngestDto.BarsResponse.builder()
                .instrumentId(instrumentId)
                .intervalCd(intervalCd)
                .from(from)
                .to(to)
                .size(size)
                .order(ord)
                .count(bars.size())
                .bars(bars)
                .build();
    }
}
