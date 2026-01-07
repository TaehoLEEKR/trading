package com.trade.auth.entity;


import com.trade.common.model.BaseTime;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.Instant;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthRefreshTokens extends BaseTime {

    @Id
    @Column(name = "token_id", nullable = false)
    private String tokenId;

    @Size(max = 36)
    @NotNull
    @Column(name = "user_id", nullable = false, length = 36)
    private String userId;

    @Size(max = 64)
    @NotNull
    @Column(name = "token_hash", nullable = false, length = 64)
    private String tokenHash;

    @NotNull
    @Column(name = "expires_at", nullable = false)
    private String expiresAt;

    @Column(name = "revoked_at")
    private String revokedAt;

}
