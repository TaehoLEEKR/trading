package com.trade.auth.entity.auth;

import com.trade.common.model.BaseTime;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthLoginAudit extends BaseTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "audit_id", nullable = false)
    private Long auditId;

    @Size(max = 36)
    @Column(name = "user_id", length = 36)
    private String userId;

    @Size(max = 255)
    @Column(name = "email")
    private String email;

    @Size(max = 64)
    @Column(name = "ip", length = 64)
    private String ip;

    @Size(max = 512)
    @Column(name = "user_agent", length = 512)
    private String userAgent;

    @Size(max = 20)
    @NotNull
    @Column(name = "result", nullable = false, length = 20)
    private String result;

}
