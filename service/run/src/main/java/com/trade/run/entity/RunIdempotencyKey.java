package com.trade.run.entity;

import com.trade.common.constant.JobStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "run_idempotency_keys")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RunIdempotencyKey {

    @Id
    @Column(name = "job_key", length = 160, nullable = false)
    private String jobKey;

    @Column(name = "run_id", length = 64)
    private String runId;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "status", length = 20, nullable = false)
    private String status;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        var now = LocalDateTime.now();
        if (createdAt == null){
            createdAt = now;
        }
        if (updatedAt == null){
            updatedAt = now;
        }
        if (status == null){
            status = JobStatus.RUNNING.getStatus();
        }
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }
}