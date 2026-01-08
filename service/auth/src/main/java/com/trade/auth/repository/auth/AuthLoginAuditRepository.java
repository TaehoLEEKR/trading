package com.trade.auth.repository.auth;

import com.trade.auth.entity.auth.AuthLoginAudit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthLoginAuditRepository extends JpaRepository<AuthLoginAudit, Long> {
}