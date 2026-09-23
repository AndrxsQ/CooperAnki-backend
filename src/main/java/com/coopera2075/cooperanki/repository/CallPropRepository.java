package com.coopera2075.cooperanki.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.coopera2075.cooperanki.entity.CallProp;

public interface CallPropRepository extends JpaRepository<CallProp, Long> {
    Optional<CallProp> findByTitle(String title);
}
