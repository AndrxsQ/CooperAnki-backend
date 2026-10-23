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

// import com.coopera2075.cooperanki.dto.InvestmentRequest;
// import com.coopera2075.cooperanki.dto.InvestmentResponse;
// import com.coopera2075.cooperanki.service.InvestmentService;

// import jakarta.validation.Valid;
// import lombok.RequiredArgsConstructor;

// // Inversiones de los funders en las convocatorias
// @RestController
// @RequestMapping ("/api/v1")
// @RequiredArgsConstructor
// public class InvestmentController {
//     private final InvestmentService investmentService;

//     // Un funder ve sus inversiones; un manager, las de las convocatorias de sus proyectos (?callPropId=)
//     @GetMapping("/investments")
//     public ResponseEntity<List<InvestmentResponse>> findAll(@RequestParam(name = "callPropId", required = false) Long callPropId) {
//         return ResponseEntity.ok(investmentService.findAll(callPropId));
//     }

//     @GetMapping("/investments/{id}")
//     public ResponseEntity<InvestmentResponse> findById(@PathVariable("id") Long id) {
//         return ResponseEntity.ok(investmentService.findById(id));
//     }

//     // Invertir en una convocatoria como el funder autenticado
//     @PostMapping("/calls/{callPropId}/investments")
//     @PreAuthorize("hasAuthority('FUNDER')")
//     public ResponseEntity<InvestmentResponse> create(
//         @PathVariable("callPropId") Long callPropId,
//         @Valid @RequestBody InvestmentRequest request
//     ) {
//         return ResponseEntity.status(HttpStatus.CREATED).body(investmentService.create(callPropId, request));
//     }

//     // Cambiar el monto de una inversión propia
//     @PutMapping("/investments/{id}")
//     @PreAuthorize("hasAuthority('FUNDER')")
//     public ResponseEntity<InvestmentResponse> update(@PathVariable("id") Long id, @Valid @RequestBody InvestmentRequest request) {
//         return ResponseEntity.ok(investmentService.update(id, request));
//     }

//     // Retirar una inversión propia
//     @DeleteMapping("/investments/{id}")
//     @PreAuthorize("hasAuthority('FUNDER')")
//     public ResponseEntity<Void> delete(@PathVariable("id") Long id) {
//         investmentService.delete(id);
//         return ResponseEntity.noContent().build();
//     }
// }
