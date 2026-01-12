package com.trade.md.repository;

import com.trade.md.entity.MdIngestJob;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MdIngestJobRepository extends JpaRepository<MdIngestJob, String> {
}