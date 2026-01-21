package com.trade.run.scheduler;

import com.trade.run.orchestrator.MdUniverseBarsOrchestrator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class MdBarsScheduler {

    private final MdUniverseBarsOrchestrator orchestrator;

    @Scheduled(cron = "${run.md.cron}")
    public void runDaily() {
        log.info("[SCHED] start cron job");
        orchestrator.runOnce().block();
        log.info("[SCHED] finish cron job");
    }
}