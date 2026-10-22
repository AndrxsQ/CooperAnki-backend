package com.coopera2075.cooperanki.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.coopera2075.cooperanki.entity.Municipality;

public interface MunicipalityRepository extends JpaRepository<Municipality, Integer> {
    @Query("""
            SELECT municipality
            FROM Municipality municipality
            JOIN FETCH municipality.department
            ORDER BY municipality.name
            """)
    List<Municipality> findAllWithDepartmentOrderByName();
}
