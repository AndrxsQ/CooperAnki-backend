package com.coopera2075.cooperanki.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.coopera2075.cooperanki.entity.AppUser;

public interface AppUserRepository extends JpaRepository<AppUser, Integer> {
}
