package com.trade.run.entity;

import com.trade.common.model.BaseTime;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "run_runs", schema = "trading")
public class RunRun extends BaseTime {
    @Id
    @Size(max = 64)
    @Column(name = "run_id", nullable = false, length = 64)
    private String runId;

    @Size(max = 160)
    @NotNull
    @Column(name = "job_key", nullable = false, length = 160)
    private String jobKey;

    @Size(max = 20)
    @NotNull
    @Column(name = "status", nullable = false, length = 20)
    private String status;

    @Size(max = 32)
    @NotNull
    @Column(name = "market", nullable = false, length = 32)
    private String market;

    @Size(max = 16)
    @NotNull
    @Column(name = "interval_cd", nullable = false, length = 16)
    private String intervalCd;

    @NotNull
    @Column(name = "limit_n", nullable = false)
    private Integer limitN;

    @NotNull
    @Column(name = "max_instruments", nullable = false)
    private Integer maxInstruments;

    @NotNull
    @Column(name = "concurrency_n", nullable = false)
    private Integer concurrencyN;

    @NotNull
    @ColumnDefault("0")
    @Column(name = "targets_count", nullable = false)
    private Integer targetsCount;

    @NotNull
    @ColumnDefault("0")
    @Column(name = "success_count", nullable = false)
    private Integer successCount;

    @NotNull
    @ColumnDefault("0")
    @Column(name = "failed_count", nullable = false)
    private Integer failedCount;

    @Lob
    @Column(name = "summary")
    private String summary;

    @Size(max = 64)
    @Column(name = "err_code", length = 64)
    private String errCode;

    @Lob
    @Column(name = "err_msg")
    private String errMsg;

    @NotNull
    @Column(name = "started_at", nullable = false)
    private Instant startedAt;

    @Column(name = "ended_at")
    private Instant endedAt;

}