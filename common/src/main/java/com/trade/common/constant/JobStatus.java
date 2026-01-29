package com.trade.common.constant;

import lombok.Getter;

@Getter
public enum JobStatus {
    RUNNING("RUNNING", "Running"),
    SKIPPED("SKIPPED", "Skip"),
    SUCCESS("SUCCESS", "Success"),
    PARTIAL_SUCCESS("PARTIAL_SUCCESS", "PARTIAL_SUCCESS"),
    FAILED("FAILED", "Failed"),
    ALL_FAILED("ALL_FAILED", "Failed"),
    RUN_EXCEPTION("RUN_EXCEPTION", "Run Exception"),
    ;

    private final String status;
    private final String description;

    JobStatus(String status, String description) {
        this.status = status;
        this.description = description;
    }
}
