package com.trade.run.repository;

import com.trade.run.entity.RunRun;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RunRunRepository extends JpaRepository<RunRun, String> {
}