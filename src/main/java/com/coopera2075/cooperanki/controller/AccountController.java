package com.coopera2075.cooperanki.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.coopera2075.cooperanki.service.AccountService;
import com.coopera2075.modules.auth.dto.AccountPreferencesRequest;
import com.coopera2075.modules.auth.dto.AccountResponse;
import com.coopera2075.modules.auth.dto.ChangePasswordRequest;
import com.coopera2075.modules.auth.dto.UpdateAccountRequest;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/account/me")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('PERM_SYSTEM_PERSONAL_ACCOUNT_ACCESS')")
public class AccountController {
    private final AccountService accountService;

    @GetMapping
    public ResponseEntity<AccountResponse> getAccount(Authentication authentication) {
        return ResponseEntity.ok(accountService.getAccount(authentication.getName()));
    }

    @PutMapping
    public ResponseEntity<AccountResponse> updateAccount(
            Authentication authentication,
            @Valid @RequestBody UpdateAccountRequest request) {
        return ResponseEntity.ok(accountService.updateAccount(authentication.getName(), request));
    }

    @PatchMapping("/preferences")
    public ResponseEntity<AccountResponse> updatePreferences(
            Authentication authentication,
            @Valid @RequestBody AccountPreferencesRequest request) {
        return ResponseEntity.ok(accountService.updatePreferences(authentication.getName(), request));
    }

    @PatchMapping("/password")
    public ResponseEntity<Void> changePassword(
            Authentication authentication,
            @Valid @RequestBody ChangePasswordRequest request) {
        accountService.changePassword(authentication.getName(), request);
        return ResponseEntity.noContent().build();
    }
}
