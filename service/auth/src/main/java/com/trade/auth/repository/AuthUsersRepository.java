package com.trade.auth.repository;

import com.trade.auth.entity.AuthUsers;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AuthUsersRepository extends JpaRepository<AuthUsers, String> {
    boolean existsByEmail(String email);

    Optional<AuthUsers> findByEmail(@Email @NotBlank String email);
}