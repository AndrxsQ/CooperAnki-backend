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

// import com.coopera2075.cooperanki.dto.CallPropRequest;
// import com.coopera2075.cooperanki.dto.CallPropResponse;
// import com.coopera2075.cooperanki.entity.CallStatus;
// import com.coopera2075.cooperanki.service.CallPropService;

// import jakarta.validation.Valid;
// import lombok.RequiredArgsConstructor;

// // Convocatorias de financiación de los proyectos
// @RestController
// @RequestMapping ("/api/v1")
// @RequiredArgsConstructor
// public class CallPropController {
//     private final CallPropService callPropService;

//     // Endpoint público para listar y buscar convocatorias (?projectId=, ?status=, ?q=)
//     @GetMapping("/calls")
//     public ResponseEntity<List<CallPropResponse>> findAll(
//         @RequestParam(name = "projectId", required = false) Long projectId,
//         @RequestParam(name = "status", required = false) CallStatus status,
//         @RequestParam(name = "q", required = false) String q
//     ) {
//         return ResponseEntity.ok(callPropService.findAll(projectId, status, q));
//     }

//     // Endpoint público para consultar una convocatoria
//     @GetMapping("/calls/{id}")
//     public ResponseEntity<CallPropResponse> findById(@PathVariable("id") Long id) {
//         return ResponseEntity.ok(callPropService.findById(id));
//     }

//     // Crear una convocatoria dentro de un proyecto (solo los managers del proyecto)
//     @PostMapping("/projects/{projectId}/calls")
//     @PreAuthorize("hasAuthority('MANAGER')")
//     public ResponseEntity<CallPropResponse> create(
//         @PathVariable("projectId") Long projectId,
//         @Valid @RequestBody CallPropRequest request
//     ) {
//         return ResponseEntity.status(HttpStatus.CREATED).body(callPropService.create(projectId, request));
//     }

//     // Actualizar una convocatoria (solo los managers de su proyecto)
//     @PutMapping("/calls/{id}")
//     @PreAuthorize("hasAuthority('MANAGER')")
//     public ResponseEntity<CallPropResponse> update(@PathVariable("id") Long id, @Valid @RequestBody CallPropRequest request) {
//         return ResponseEntity.ok(callPropService.update(id, request));
//     }

//     // Eliminar una convocatoria (solo los managers de su proyecto)
//     @DeleteMapping("/calls/{id}")
//     @PreAuthorize("hasAuthority('MANAGER')")
//     public ResponseEntity<Void> delete(@PathVariable("id") Long id) {
//         callPropService.delete(id);
//         return ResponseEntity.noContent().build();
//     }
// }
