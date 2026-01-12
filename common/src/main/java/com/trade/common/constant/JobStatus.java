package com.trade.common.constant;

import lombok.Getter;

@Getter
public enum JobStatus {
    RUNNING("RUNNING", "Running"),
    SUCCESS("SUCCESS", "Success"),
    FAILED("FAILED", "Failed")
    ;

    private final String status;
    private final String description;

    JobStatus(String status, String description) {
        this.status = status;
        this.description = description;
    }
}
