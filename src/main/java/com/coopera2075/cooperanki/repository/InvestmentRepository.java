// package com.coopera2075.cooperanki.repository;

// import java.util.List;

// import org.springframework.data.jpa.repository.JpaRepository;
// import org.springframework.data.jpa.repository.Query;
// import org.springframework.data.repository.query.Param;

// import com.coopera2075.cooperanki.entity.Investment;

// public interface InvestmentRepository extends JpaRepository<Investment, Long> {
//     List<Investment> findByFunderId(Long funderId);

//     // Inversiones hechas en convocatorias de los proyectos que gestiona el manager
//     @Query("""
//             select i from Investment i
//             where exists (
//                 select 1 from Arrangement a
//                 where a.project = i.callProp.project and a.manager.id = :managerId
//             )
//             """)
//     List<Investment> findByProjectManagerId(@Param("managerId") Long managerId);
// }
