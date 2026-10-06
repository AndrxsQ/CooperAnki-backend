package com.coopera2075.cooperanki.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.coopera2075.cooperanki.dto.AuthResponse;
import com.coopera2075.cooperanki.dto.UpdateUserRequest;
import com.coopera2075.cooperanki.dto.UserResponse;
import com.coopera2075.cooperanki.entity.UserRole;
import com.coopera2075.cooperanki.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

// Cuentas de usuario (managers y funders). El alta se hace en /api/v1/auth/register
@RestController
@RequestMapping ("/api/v1/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    // Listar usuarios, opcionalmente filtrados por rol (?role=MANAGER | FUNDER)
    @GetMapping
    public ResponseEntity<List<UserResponse>> findAll(@RequestParam(name = "role", required = false) UserRole role) {
        return ResponseEntity.ok(userService.findAll(role));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> findById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(userService.findById(id));
    }

    // Actualizar la propia cuenta; responde con un JWT nuevo por si cambió el username
    @PutMapping("/{id}")
    public ResponseEntity<AuthResponse> update(@PathVariable("id") Long id, @Valid @RequestBody UpdateUserRequest request) {
        return ResponseEntity.ok(userService.update(id, request));
    }

    // Eliminar la propia cuenta
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") Long id) {
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
