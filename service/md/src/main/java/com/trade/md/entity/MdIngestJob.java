package com.trade.md.entity;

import com.trade.common.model.BaseTime;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import java.time.Instant;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "md_ingest_jobs", schema = "trading")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MdIngestJob extends BaseTime {
    @Id
    @Size(max = 50)
    @Column(name = "job_id", nullable = false, length = 50)
    private String jobId;

    @Size(max = 40)
    @Column(name = "run_id", length = 40)
    private String runId;

    @Size(max = 20)
    @NotNull
    @Column(name = "market", nullable = false, length = 20)
    private String market;

    @Size(max = 20)
    @NotNull
    @Column(name = "job_type", nullable = false, length = 20)
    private String jobType;

    @Size(max = 20)
    @NotNull
    @Column(name = "status", nullable = false, length = 20)
    private String status;

    @Column(name = "started_at")
    private LocalDateTime startedAt;

    @Column(name = "ended_at")
    private LocalDateTime endedAt;

    @Size(max = 64)
    @Column(name = "err_code", length = 64)
    private String errCode;

    @Size(max = 1024)
    @Column(name = "err_msg", length = 1024)
    private String errMsg;

}