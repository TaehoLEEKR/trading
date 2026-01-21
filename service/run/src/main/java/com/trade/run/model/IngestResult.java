package com.trade.run.model;

public record IngestResult(
        String universeId,
        boolean success,
        String errorMessage
) {}