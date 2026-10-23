// package com.coopera2075.cooperanki.controller;

// import java.util.List;

// import org.springframework.http.HttpStatus;
// import org.springframework.http.ResponseEntity;
// import org.springframework.security.access.prepost.PreAuthorize;
// import org.springframework.web.bind.annotation.DeleteMapping;
// import org.springframework.web.bind.annotation.GetMapping;
// import org.springframework.web.bind.annotation.PathVariable;
// import org.springframework.web.bind.annotation.PostMapping;
// import org.springframework.web.bind.annotation.RequestBody;
// import org.springframework.web.bind.annotation.RequestMapping;
// import org.springframework.web.bind.annotation.RequestParam;
// import org.springframework.web.bind.annotation.RestController;

// import com.coopera2075.cooperanki.dto.ArrangementRequest;
// import com.coopera2075.cooperanki.dto.ArrangementResponse;
// import com.coopera2075.cooperanki.service.ArrangementService;

// import jakarta.validation.Valid;
// import lombok.RequiredArgsConstructor;

// // Asignaciones de managers a proyectos
// @RestController
// @RequestMapping ("/api/v1/arrangements")
// @RequiredArgsConstructor
// public class ArrangementController {
//     private final ArrangementService arrangementService;

//     // Listar asignaciones, opcionalmente filtradas (?projectId= y/o ?managerId=)
//     @GetMapping
//     public ResponseEntity<List<ArrangementResponse>> findAll(
//         @RequestParam(name = "projectId", required = false) Long projectId,
//         @RequestParam(name = "managerId", required = false) Long managerId
//     ) {
//         return ResponseEntity.ok(arrangementService.findAll(projectId, managerId));
//     }

//     @GetMapping("/{id}")
//     public ResponseEntity<ArrangementResponse> findById(@PathVariable("id") Long id) {
//         return ResponseEntity.ok(arrangementService.findById(id));
//     }

//     // Asignar otro manager a un proyecto (solo los managers actuales del proyecto)
//     @PostMapping
//     @PreAuthorize("hasAuthority('MANAGER')")
//     public ResponseEntity<ArrangementResponse> create(@Valid @RequestBody ArrangementRequest request) {
//         return ResponseEntity.status(HttpStatus.CREATED).body(arrangementService.create(request));
//     }

//     // Quitar a un manager de un proyecto (solo los managers actuales del proyecto)
//     @DeleteMapping("/{id}")
//     @PreAuthorize("hasAuthority('MANAGER')")
//     public ResponseEntity<Void> delete(@PathVariable("id") Long id) {
//         arrangementService.delete(id);
//         return ResponseEntity.noContent().build();
//     }
// }
