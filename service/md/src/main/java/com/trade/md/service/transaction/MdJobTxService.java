package com.trade.md.service.transaction;

import com.trade.common.constant.ErrorCode;
import com.trade.common.constant.JobStatus;
import com.trade.common.exception.CustomException;
import com.trade.md.entity.MdIngestJob;
import com.trade.md.repository.MdIngestJobRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class MdJobTxService {

    private final MdIngestJobRepository mdIngestJobRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void start(MdIngestJob job) {
        mdIngestJobRepository.save(job);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void success(String jobId, String summary) {
        MdIngestJob job = mdIngestJobRepository.findById(jobId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, "job not found"));
        job.setStatus(JobStatus.SUCCESS.getStatus());
        job.setEndedAt(LocalDateTime.now());
        job.setErrMsg(summary);
        mdIngestJobRepository.save(job);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void failed(String jobId, String errCode, String errMsg) {
        MdIngestJob job = mdIngestJobRepository.findById(jobId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, "job not found"));
        job.setStatus(JobStatus.FAILED.getStatus());
        job.setEndedAt(LocalDateTime.now());
        job.setErrCode(errCode);
        job.setErrMsg(errMsg);
        mdIngestJobRepository.save(job);
    }
}