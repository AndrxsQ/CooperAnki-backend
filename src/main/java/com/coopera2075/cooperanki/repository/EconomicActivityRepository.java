package com.coopera2075.cooperanki.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.coopera2075.cooperanki.entity.EconomicActivity;
import com.coopera2075.cooperanki.entity.EconomicUnit;

public interface EconomicActivityRepository extends JpaRepository<EconomicActivity, Integer> {
    @Query("SELECT activity FROM EconomicActivity activity ORDER BY activity.ciiuId")
    List<EconomicActivity> findAllOrderByCiiuId();

    boolean existsByCiiuId(String ciiuId);

    @Query(value = """
            SELECT ea.*
            FROM economic_activities ea
            JOIN unit_activities ua ON ua.activity_id = ea.ciiu_id
            JOIN economic_units eu ON eu.doc_number = ua.unit_id
            WHERE eu.id = :unitId
            ORDER BY ea.ciiu_id
            """, nativeQuery = true)
    List<EconomicActivity> findAllByEconomicUnitId(@Param("unitId") Integer unitId);

}
