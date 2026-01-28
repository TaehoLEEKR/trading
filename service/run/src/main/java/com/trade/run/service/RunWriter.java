package com.trade.run.service;

import com.trade.common.component.Snowflake;
import com.trade.common.constant.JobStatus;
import com.trade.run.config.RunProperties;
import com.trade.run.entity.RunRun;
import com.trade.run.repository.RunRunRepository;
import com.trade.run.repository.RunRunStepRepository;
import com.trade.run.steps.RunContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
// DB 적재 할 용도 Writer
public class RunWriter {
    private final RunRunRepository runRunRepository;
    private final RunRunStepRepository runRunStepRepository;

    private final Snowflake snowflake = new Snowflake();

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public String startRun(String jobKey, RunContext ctx, RunProperties.Md mdProps) {
        String runId = "run_" + snowflake.nextId();

        RunRun run = RunRun.builder()
                .runId(runId)
                .jobKey(jobKey)
                .status(JobStatus.RUNNING.getStatus())
                .market(ctx.market())
                .intervalCd(ctx.intervalCd())
                .limitN(ctx.limit())
                .maxInstruments(ctx.defaultMaxInstruments())
                .concurrencyN(mdProps.concurrency() == null ? 1 : mdProps.concurrency())
                .targetsCount(0)
                .successCount(0)
                .failedCount(0)
                .startedAt(LocalDateTime.now().toString())
                .build();

        runRunRepository.save(run);
        return runId;
    }
}
