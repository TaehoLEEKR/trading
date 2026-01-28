package com.trade.run.service;

import com.trade.run.entity.RunIdempotencyKey;
import com.trade.run.repository.RunIdempotencyKeyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class RunIdempotencyService {

    private final RunIdempotencyKeyRepository repo;


    @Transactional
    public boolean acquire(String jobKey) {
        try {
            repo.save(RunIdempotencyKey.builder()
                    .jobKey(jobKey)
                    .build());
            return true;
        } catch (DataIntegrityViolationException e) {
            return false;
        }
    }

    @Transactional
    public void attachRunId(String jobKey, String runId) {
        repo.findById(jobKey).ifPresent(k -> {
            k.setRunId(runId);
            repo.save(k);
        });
    }
}
