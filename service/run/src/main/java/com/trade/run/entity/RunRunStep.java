package com.trade.run.entity;

import com.trade.common.model.BaseTime;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "run_run_steps", schema = "trading")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RunRunStep extends BaseTime {
    @Id
    @Size(max = 64)
    @Column(name = "step_id", nullable = false, length = 64)
    private String stepId;

    @Size(max = 64)
    @NotNull
    @Column(name = "run_id", nullable = false, length = 64)
    private String runId;

    @Size(max = 80)
    @NotNull
    @Column(name = "step_name", nullable = false, length = 80)
    private String stepName;

    @Size(max = 20)
    @NotNull
    @Column(name = "status", nullable = false, length = 20)
    private String status;

    @NotNull
    @ColumnDefault("0")
    @Column(name = "item_total", nullable = false)
    private Integer itemTotal;

    @NotNull
    @ColumnDefault("0")
    @Column(name = "item_success", nullable = false)
    private Integer itemSuccess;

    @NotNull
    @ColumnDefault("0")
    @Column(name = "item_failed", nullable = false)
    private Integer itemFailed;

    @Size(max = 64)
    @Column(name = "err_code", length = 64)
    private String errCode;

    @Lob
    @Column(name = "err_msg")
    private String errMsg;

    @NotNull
    @Column(name = "started_at", nullable = false)
    private String startedAt;

    @Column(name = "ended_at")
    private String endedAt;

}