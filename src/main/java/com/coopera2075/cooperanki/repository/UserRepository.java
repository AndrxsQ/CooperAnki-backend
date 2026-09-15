package com.coopera2075.cooperanki.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.coopera2075.cooperanki.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

}
