package com.coopera2075.cooperanki.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.coopera2075.cooperanki.entity.EconomicUnit;

public interface EconomicUnitRepository extends JpaRepository<EconomicUnit, Integer> {
    @Query("""
            SELECT DISTINCT unit
            FROM EconomicUnit unit
            LEFT JOIN FETCH unit.activities
            LEFT JOIN FETCH unit.department
            LEFT JOIN FETCH unit.municipality
            LEFT JOIN FETCH unit.node
            LEFT JOIN FETCH unit.organization
            ORDER BY unit.id
            """)
    List<EconomicUnit> findAllWithActivities();

    @Query("""
            SELECT DISTINCT unit
            FROM EconomicUnit unit
            LEFT JOIN FETCH unit.activities
            LEFT JOIN FETCH unit.department
            LEFT JOIN FETCH unit.municipality
            LEFT JOIN FETCH unit.node
            LEFT JOIN FETCH unit.organization
            WHERE unit.id = :id
            """)
    Optional<EconomicUnit> findByIdWithActivities(@Param("id") Integer id);

    @Query("""
            SELECT DISTINCT unit
            FROM EconomicUnit unit
            JOIN unit.activities matchedActivity
            LEFT JOIN FETCH unit.activities
            LEFT JOIN FETCH unit.department
            LEFT JOIN FETCH unit.municipality
            LEFT JOIN FETCH unit.node
            LEFT JOIN FETCH unit.organization
            WHERE matchedActivity.ciiuId = :ciiuId
            ORDER BY unit.id
            """)
    List<EconomicUnit> findAllWithActivitiesByCiiuId(@Param("ciiuId") String ciiuId);
}
