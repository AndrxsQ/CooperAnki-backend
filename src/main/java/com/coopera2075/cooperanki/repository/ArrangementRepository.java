package com.coopera2075.cooperanki.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.coopera2075.cooperanki.entity.Arrangement;

public interface ArrangementRepository extends JpaRepository<Arrangement, Long> {
    boolean existsByManagerIdAndProjectId(Long managerId, Long projectId);

    List<Arrangement> findByProjectId(Long projectId);

    List<Arrangement> findByManagerId(Long managerId);

    List<Arrangement> findByManagerIdAndProjectId(Long managerId, Long projectId);

    long countByProjectId(Long projectId);

    // Proyectos que se quedarían sin ningún manager si este desapareciera
    @Query("""
            select count(a) from Arrangement a
            where a.manager.id = :managerId
              and not exists (
                  select 1 from Arrangement other
                  where other.project = a.project and other.manager.id <> :managerId
              )
            """)
    long countProjectsManagedOnlyBy(@Param("managerId") Long managerId);
}
