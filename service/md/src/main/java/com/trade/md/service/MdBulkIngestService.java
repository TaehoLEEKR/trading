package com.trade.md.service;

import com.trade.catalog.entity.CatalogInstruments;
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
import com.trade.md.mapper.MdBarsDao;
import com.trade.md.mapper.UniverseInstrumentDao;
import com.trade.md.model.dto.KisDailyPriceResponse;
import com.trade.md.model.dto.MdBarRow;
import com.trade.md.model.dto.MdIngest;
import com.trade.md.service.transaction.MdTransactionService;
import lombok.RequiredArgsConstructor;
import com.trade.md.service.MdJobWriter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.trade.common.constant.staticConst.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class MdBulkIngestService {

    private final UniverseInstrumentDao universeInstrumentDao;
    private final MdBarsDao mdBarsDao;
    private final MdTransactionService mdTransactionService;

    private final MdJobWriter mdJobWriter;

    private final KisTokenStore kisTokenStore;
    private final CallClient callClient;
    private final localCommunication localCommunication;
    private final KisAuthConfig kisAuthConfig;

    @Value("${run.internal.token}")
    private String runInternalToken;

    private Map<String, String> newHeaders() {
        return new HashMap<>(staticConst.headers);
    }

    public MdIngest.UniverseBarsResponse ingestDailyBarsByUniverse(MdIngest.UniverseBarsRequest req) {

        if (!"1d".equals(req.getIntervalCd())) {
            throw new CustomException(ErrorCode.NOT_FOUND, "intervalCd only supports 1d for now");
        }

        int limit = (req.getMaxInstruments() == null) ? 300 : Math.min(req.getMaxInstruments(), 1000);

        Snowflake snowflake = new Snowflake();
        String jobId = "mdjob_" + snowflake.nextId();
        List<MdIngest.Failure> failures = new ArrayList<>();

        // 유니버스 종목 목록 조회
        List<CatalogInstruments> instruments = universeInstrumentDao.selectInstrumentsByUniverse(req.getUniverseId(), limit);
        if (instruments == null || instruments.isEmpty()) {
            throw new CustomException(ErrorCode.NOT_FOUND, "universe instruments is empty");
        }

        MdIngestJob job = MdIngestJob.builder()
                .jobId(jobId)
                .market(instruments.getFirst().getMarket())
                .jobType(BARS)
                .status(JobStatus.RUNNING.getStatus())
                .startedAt(LocalDateTime.now())
                .build();
        mdJobWriter.saveRunning(job);

        int fetched = 0;
        int upserted = 0;
        int failed = 0;

        MdIngest.UniverseBarsResponse.UniverseBarsResponseBuilder resp = MdIngest.UniverseBarsResponse.builder()
                .jobId(jobId)
                .universeId(req.getUniverseId())
                .intervalCd("1d")
                .instruments(instruments.size());

        try {

            String kisAccessKey = getKisRedisAccessKey();
            Map<String, String> kisHeaders = makeSendHeaders(kisAccessKey);

            // 종목별 순차 수집
            for (CatalogInstruments inst : instruments) {
                try {
                    KisDailyPriceResponse kis = callKisDailyPrice(inst, kisHeaders);

                    int rows = (kis.output() == null) ? 0 : kis.output().size();
                    fetched += rows;

                    List<MdBarRow> bars = com.trade.md.mapper.KisToMdBarsMapper.toDailyBars(
                            inst.getInstrumentId(),
                            inst.getMarket(),
                            inst.getCurrency(),
                            kis
                    );

                    if (!bars.isEmpty()) {
                        upserted += mdTransactionService.upsertBarsInNewTx(bars);
                    }

                } catch (Exception e) {
                    failed++;
                    failures.add(MdIngest.Failure.builder()
                                    .instrumentId(inst.getInstrumentId())
                                    .symbol(inst.getSymbol())
                                    .errCode("INSTRUMENT_FAIL")
                                    .errMsg(e.getMessage())
                                    .build());

//                    resp.failures(List.of());
//                    resp.build().getFailures().add(
//                            MdIngest.Failure.builder()
//                                    .instrumentId(inst.getInstrumentId())
//                                    .symbol(inst.getSymbol())
//                                    .errCode("INSTRUMENT_FAIL")
//                                    .errMsg(e.getMessage())
//                                    .build()
//                    );
                    // 실패해도 전체 job은 계속 진행
                    log.warn("instrument ingest failed. instrumentId={}, symbol={}, msg={}",
                            inst.getInstrumentId(), inst.getSymbol(), e.getMessage());
                }
            }

            //job 종료 상태 결정
            String summary = "instruments=" + instruments.size()
                    + ", fetched=" + fetched
                    + ", upserted=" + upserted
                    + ", failed=" + failed;

            if (failed == instruments.size()) {
                mdJobWriter.markFailed(jobId, "ALL_FAILED", summary);
            } else {
                mdJobWriter.markSuccess(jobId, summary);
            }

            return MdIngest.UniverseBarsResponse.builder()
                    .jobId(jobId)
                    .universeId(req.getUniverseId())
                    .intervalCd("1d")
                    .instruments(instruments.size())
                    .fetched(fetched)
                    .upserted(upserted)
                    .failed(failed)
                    .failures(failures)
                    .build();

        } catch (Exception e) {
            mdJobWriter.markFailed(jobId, "JOB_EXCEPTION", e.getMessage());
            throw (e instanceof CustomException ce) ? ce : new CustomException(ErrorCode.SERVER_ERROR, e.getMessage());
        }
    }

    private KisDailyPriceResponse callKisDailyPrice(CatalogInstruments inst, Map<String, String> kisHeaders) {

        String url = kisAuthConfig.url().inquireDailyPrice()
                + "?FID_COND_MRKT_DIV_CODE=" + KRX_FID_COND_MARKT_DIV_CODE
                + "&FID_INPUT_ISCD=" + inst.getSymbol()
                + "&FID_PERIOD_DIV_CODE=" + PERIOD_DIV_CODE_D
                + "&FID_ORG_ADJ_PRC=" + ORG_ADJ_PRC_1;

        CallClient.CallResult result = callClient.GET_WITH_HEADERS(url, kisHeaders);

        if (result.headers() != null && result.headers().get("tr_id") != null) {
            String trId = result.headers().get("tr_id").getFirst();
            if (!INGEST_TR_ID_DAILY.equals(trId)) {
                throw new CustomException(ErrorCode.MD_JOB_TR_ID_WARN, "tr_id mismatch: " + trId);
            }
        }

        KisDailyPriceResponse kis = JsonUtil.getInstance().decodeFromJson(result.body(), KisDailyPriceResponse.class);
        if (kis == null || kis.rt_cd() == null || !"0".equals(kis.rt_cd())) {
            String code = (kis == null) ? "NULL" : kis.rt_cd();
            String msg = (kis == null) ? "null response" : kis.msg1();
            throw new CustomException(ErrorCode.SERVER_ERROR, "KIS failed: " + code + " / " + msg);
        }
        return kis;
    }


    private String getKisRedisAccessKey() {
        TokenResponse cachedToken = kisTokenStore.getAccessToken();
        if (cachedToken != null && cachedToken.getAccessToken() != null) {
            return cachedToken.getAccessToken();
        }

        Map<String, String> headers = newHeaders();
        headers.put(staticConst.X_INTERNAL_TOKEN,runInternalToken );

        String result = callClient.POST(localCommunication.retryKisToken(), headers, "");
        TokenResponse tokenResponse = JsonUtil.getInstance().decodeFromJson(result, TokenResponse.class);
        kisTokenStore.storeAccessToken(tokenResponse);
        return tokenResponse.getAccessToken();
    }

    private Map<String, String> makeSendHeaders(String kisAccessToken) {
        Map<String, String> headers = newHeaders();
        headers.put("authorization", staticConst.BEARER + kisAccessToken);
        headers.put("custtype", staticConst.CUST_TYPE);
        headers.put("appkey", kisAuthConfig.key().app());
        headers.put("appsecret", kisAuthConfig.key().secret());
        headers.put("tr_id", staticConst.INGEST_TR_ID_DAILY);
        return headers;
    }
}
