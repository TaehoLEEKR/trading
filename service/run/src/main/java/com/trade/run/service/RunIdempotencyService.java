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
    public boolean acquireOrResume(String jobKey) {
        return repo.findById(jobKey)
                .map(existing -> {
                    if ("SUCCESS".equals(existing.getStatus())) {
                        return false;
                    }
                    existing.setStatus("RUNNING");
                    repo.save(existing);
                    return true;
                })
                .orElseGet(() -> {
                    try {
                        repo.save(RunIdempotencyKey.builder()
                                .jobKey(jobKey)
                                .status("RUNNING")
                                .build());
                        return true;
                    } catch (DataIntegrityViolationException e) {
                        // 동시 경쟁 시 재조회
                        var again = repo.findById(jobKey).orElse(null);
                        return again != null && !"SUCCESS".equals(again.getStatus());
                    }
                });
    }

    @Transactional
    public void attachRunId(String jobKey, String runId) {
        repo.findById(jobKey).ifPresent(k -> {
            k.setRunId(runId);
            repo.save(k);
        });
    }

    @Transactional
    public void markSuccess(String jobKey) {
        repo.findById(jobKey).ifPresent(k -> {
            k.setStatus("SUCCESS");
            repo.save(k);
        });
    }

    @Transactional
    public void markFailed(String jobKey) {
        repo.findById(jobKey).ifPresent(k -> {
            k.setStatus("FAILED");
            repo.save(k);
        });
    }
}
