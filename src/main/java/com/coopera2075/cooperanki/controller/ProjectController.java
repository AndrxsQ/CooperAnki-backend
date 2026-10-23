// package com.coopera2075.cooperanki.controller;

// import java.util.List;

// import org.springframework.http.HttpStatus;
// import org.springframework.http.ResponseEntity;
// import org.springframework.security.access.prepost.PreAuthorize;
// import org.springframework.web.bind.annotation.DeleteMapping;
// import org.springframework.web.bind.annotation.GetMapping;
// import org.springframework.web.bind.annotation.PathVariable;
// import org.springframework.web.bind.annotation.PostMapping;
// import org.springframework.web.bind.annotation.PutMapping;
// import org.springframework.web.bind.annotation.RequestBody;
// import org.springframework.web.bind.annotation.RequestMapping;
// import org.springframework.web.bind.annotation.RequestParam;
// import org.springframework.web.bind.annotation.RestController;

// import com.coopera2075.cooperanki.dto.ProjectRequest;
// import com.coopera2075.cooperanki.dto.ProjectResponse;
// import com.coopera2075.cooperanki.service.ProjectService;

// import jakarta.validation.Valid;
// import lombok.RequiredArgsConstructor;

// @RestController
// @RequestMapping ("/api/v1/projects")
// @RequiredArgsConstructor
// public class ProjectController {
//     private final ProjectService projectService;

//     // Endpoint público para listar proyectos, con búsqueda opcional por texto (?q=)
//     @GetMapping
//     public ResponseEntity<List<ProjectResponse>> findAll(@RequestParam(name = "q", required = false) String q) {
//         return ResponseEntity.ok(projectService.findAll(q));
//     }

//     // Endpoint público para consultar un proyecto
//     @GetMapping("/{id}")
//     public ResponseEntity<ProjectResponse> findById(@PathVariable("id") Long id) {
//         return ResponseEntity.ok(projectService.findById(id));
//     }

//     // Crear un proyecto; el manager autenticado queda asignado como su gestor
//     @PostMapping
//     @PreAuthorize("hasAuthority('MANAGER')")
//     public ResponseEntity<ProjectResponse> create(@Valid @RequestBody ProjectRequest request) {
//         return ResponseEntity.status(HttpStatus.CREATED).body(projectService.create(request));
//     }

//     // Actualizar un proyecto (solo sus managers)
//     @PutMapping("/{id}")
//     @PreAuthorize("hasAuthority('MANAGER')")
//     public ResponseEntity<ProjectResponse> update(@PathVariable("id") Long id, @Valid @RequestBody ProjectRequest request) {
//         return ResponseEntity.ok(projectService.update(id, request));
//     }

//     // Eliminar un proyecto (solo sus managers)
//     @DeleteMapping("/{id}")
//     @PreAuthorize("hasAuthority('MANAGER')")
//     public ResponseEntity<Void> delete(@PathVariable("id") Long id) {
//         projectService.delete(id);
//         return ResponseEntity.noContent().build();
//     }
// }
