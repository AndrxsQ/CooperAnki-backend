package com.coopera2075.cooperanki.repository;

import java.util.Optional;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.coopera2075.cooperanki.entity.AuthIdentity;

public interface AuthIdentityRepository extends JpaRepository<AuthIdentity, Integer> {
    @Query("""
            SELECT identity
            FROM AuthIdentity identity
            JOIN FETCH identity.user
            WHERE identity.username = :username
            """)
    Optional<AuthIdentity> findByUsernameWithUser(@Param("username") String username);

    @Query("""
            SELECT identity
            FROM AuthIdentity identity
            JOIN FETCH identity.user
            WHERE identity.username = :identifier OR identity.email = :identifier
            """)
    List<AuthIdentity> findAllByLoginIdentifierWithUser(@Param("identifier") String identifier);

    boolean existsByEmailIgnoreCase(String email);
}
