package com.trade.catalog.repository;

import com.trade.catalog.entity.CatalogUnivers;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CatalogUniversRepository extends JpaRepository<CatalogUnivers, String> {
    Optional<CatalogUnivers> findByUniverseId(String universeId);
}