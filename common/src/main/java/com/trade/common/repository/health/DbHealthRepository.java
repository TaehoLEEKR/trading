package com.trade.common.repository.health;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

@Repository
public class DbHealthRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public void ping() {
        entityManager.createNativeQuery("SELECT 1").getSingleResult();
    }
}