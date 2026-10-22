// package com.coopera2075.cooperanki.repository;

// import java.util.List;
// import java.util.Optional;

// import org.springframework.data.jpa.repository.JpaRepository;
// import org.springframework.data.jpa.repository.Query;
// import org.springframework.data.repository.query.Param;

// import com.coopera2075.cooperanki.entity.Project;

// public interface ProjectRepository extends JpaRepository<Project, Long> {
//     Optional<Project> findByName(String name);

//     // Búsqueda por texto en nombre o descripción; el patrón se construye con SearchPatterns.contains
//     @Query("""
//             select p from Project p
//             where lower(p.name) like :pattern escape '!'
//                or lower(coalesce(p.description, '')) like :pattern escape '!'
//             order by p.createdAt desc, p.id desc
//             """)
//     List<Project> search(@Param("pattern") String pattern);
// }
