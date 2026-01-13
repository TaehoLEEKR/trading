package com.trade.md.model.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Map;

@Getter
@Setter
public class Md {

    @Getter
    @Setter
    public static class RequestBars {

        @NotBlank
        private String instrumentId;

        private String intervalCd = "1d";

        @DateTimeFormat(pattern = "yyyy-MM-dd")
        private String from;

        @DateTimeFormat(pattern = "yyyy-MM-dd")
        private String to;
    }

    @Getter
    @Setter
    @Builder
    public static class ResponseBars {
        private String jobId;
        private String jobType;
        private String status;
        private String market;
        private String instrumentId;
        private String intervalCd;
        private RequestRange requestedRange;
        private int fetched;
        private int upserted;
        private String skipped;
        private String failed;
        private String message;
    }


    @Getter
    @Setter
    public static class RequestRange {
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        private String from;

        @DateTimeFormat(pattern = "yyyy-MM-dd")
        private String to;
    }
}
