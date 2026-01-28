package com.trade.run.steps;

public record RunContext(String market, String intervalCd, Integer limit, Integer defaultMaxInstruments,
String jobKey, String runId) {

    public RunContext withRun(String jobKey, String runId) {
        return new RunContext(market, intervalCd, limit, defaultMaxInstruments, jobKey, runId);
    }

}

