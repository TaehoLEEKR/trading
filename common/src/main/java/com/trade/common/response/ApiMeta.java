package com.trade.common.response;

import java.time.Instant;

public class ApiMeta {
    private final String requestId;
    private final String runId;
    private final Instant timestamp;

    public ApiMeta(String requestId, String runId, Instant timestamp) {
        this.requestId = requestId;
        this.runId = runId;
        this.timestamp = timestamp;
    }

    public String getRequestId() { return requestId; }
    public String getRunId() { return runId; }
    public Instant getTimestamp() { return timestamp; }
}
