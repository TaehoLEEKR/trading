package com.trade.run.service;

import com.trade.common.component.Snowflake;
import com.trade.common.constant.JobStatus;
import com.trade.run.config.RunProperties;
import com.trade.run.entity.RunRun;
import com.trade.run.entity.RunRunStep;
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

// Transaction 분리
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

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public String startStep(String runId, String stepName) {
        String stepId = "step_" + snowflake.nextId();

        RunRunStep step = RunRunStep.builder()
                .stepId(stepId)
                .runId(runId)
                .stepName(stepName)
                .status(JobStatus.RUNNING.getStatus())
                .itemTotal(0)
                .itemSuccess(0)
                .itemFailed(0)
                .startedAt(LocalDateTime.now().toString())
                .build();

        runRunStepRepository.save(step);
        return stepId;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void finishStepSuccess(String stepId, int total, int success, int failed) {
        RunRunStep step = runRunStepRepository.findById(stepId).orElseThrow();
        step.setStatus(JobStatus.SUCCESS.getStatus());
        step.setEndedAt(LocalDateTime.now().toString());
        step.setItemTotal(total);
        step.setItemSuccess(success);
        step.setItemFailed(failed);
        runRunStepRepository.save(step);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void finishStepFailed(String stepId, String errCode, String errMsg) {
        RunRunStep step = runRunStepRepository.findById(stepId).orElseThrow();
        step.setStatus(JobStatus.FAILED.getStatus());
        step.setEndedAt(LocalDateTime.now().toString());
        step.setErrCode(errCode);
        step.setErrMsg(errMsg);
        runRunStepRepository.save(step);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void finishRunSuccess(String runId, int targets, int success, int failed, String summary) {
        RunRun run = runRunRepository.findById(runId).orElseThrow();
        run.setStatus(JobStatus.SUCCESS.getStatus());
        run.setEndedAt(LocalDateTime.now().toString());
        run.setTargetsCount(targets);
        run.setSuccessCount(success);
        run.setFailedCount(failed);
        run.setSummary(summary);
        runRunRepository.save(run);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void finishRunFailed(String runId, String errCode, String errMsg) {
        RunRun run = runRunRepository.findById(runId).orElseThrow();
        run.setStatus(JobStatus.FAILED.getStatus());
        run.setEndedAt(LocalDateTime.now().toString());
        run.setErrCode(errCode);
        run.setErrMsg(errMsg);
        runRunRepository.save(run);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void finishRun(String runId, String status, int targets, int success, int failed, String summary, String errCode, String errMsg) {
        RunRun run = runRunRepository.findById(runId).orElseThrow();
        run.setStatus(status);
        run.setEndedAt(LocalDateTime.now().toString());
        run.setTargetsCount(targets);
        run.setSuccessCount(success);
        run.setFailedCount(failed);
        run.setSummary(summary);
        run.setErrCode(errCode);
        run.setErrMsg(errMsg);
        runRunRepository.save(run);
    }
}
