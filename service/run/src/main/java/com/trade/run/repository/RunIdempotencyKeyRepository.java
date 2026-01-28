package com.trade.run.repository;

import com.trade.run.entity.RunIdempotencyKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RunIdempotencyKeyRepository extends JpaRepository<RunIdempotencyKey, String> {
}