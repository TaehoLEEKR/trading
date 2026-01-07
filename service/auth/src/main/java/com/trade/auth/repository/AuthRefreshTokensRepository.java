package com.trade.auth.repository;

import com.trade.auth.entity.AuthRefreshTokens;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthRefreshTokensRepository extends JpaRepository<AuthRefreshTokens, String> {
}