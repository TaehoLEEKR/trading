package com.trade.run.repository;

import com.trade.run.entity.RunRunStep;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RunRunStepRepository extends JpaRepository<RunRunStep, String> {
}