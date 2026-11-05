package com.coopera2075.modules.auth;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<User, Long> {
    @Query("""
            SELECT DISTINCT account
            FROM User account
            JOIN FETCH account.role role
            LEFT JOIN FETCH role.permissions
            WHERE LOWER(account.email) = LOWER(:email)
            """)
    Optional<User> findByEmailWithRoleAndPermissions(@Param("email") String email);

    boolean existsByEmailIgnoreCase(String email);
}
