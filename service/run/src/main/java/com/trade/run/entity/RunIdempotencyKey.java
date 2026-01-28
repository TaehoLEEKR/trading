package com.trade.run.entity;

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

    @PrePersist
    public void prePersist() {
        if (createdAt == null) createdAt = LocalDateTime.now();
    }
}