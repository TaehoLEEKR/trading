package com.trade.md.service.transaction;

import com.trade.common.constant.JobStatus;
import com.trade.md.entity.MdIngestJob;
import com.trade.md.repository.MdIngestJobRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class MdJobWriter {

    private final MdIngestJobRepository repo;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveRunning(MdIngestJob job) {
        repo.save(job);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void markSuccess(String jobId, String msg) {
        MdIngestJob job = repo.findById(jobId).orElseThrow();
        job.setStatus(JobStatus.SUCCESS.getStatus());
        job.setEndedAt(LocalDateTime.now());
        job.setErrMsg(msg);
        repo.save(job);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void markFailed(String jobId, String errCode, String errMsg) {
        MdIngestJob job = repo.findById(jobId).orElseThrow();
        job.setStatus(JobStatus.FAILED.getStatus());
        job.setEndedAt(LocalDateTime.now());
        job.setErrCode(errCode);
        job.setErrMsg(errMsg);
        repo.save(job);
    }
}
