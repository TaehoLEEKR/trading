package com.trade.auth.repository;

import com.trade.auth.entity.auth.AuthLoginAudit;
import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface AuthLoginAuditRepository extends JpaRepository<AuthLoginAudit, Long> {
}