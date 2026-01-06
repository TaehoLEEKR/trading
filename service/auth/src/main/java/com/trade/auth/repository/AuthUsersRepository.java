package com.trade.auth.repository;

import com.trade.auth.entity.AuthUsers;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthUsersRepository extends JpaRepository<AuthUsers, String> {
    boolean existsByEmail(String email);
}