package com.nisa.itsm.auth.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @GetMapping("/me")
    public Map<String, Object> me(@AuthenticationPrincipal Jwt jwt) {
        return jwt.getClaims();
    }

    @GetMapping("/test-customer")
    @PreAuthorize("hasRole('CUSTOMER')")
    public String testCustomer() {
        return "Hello CUSTOMER";
    }

    @GetMapping("/test-admin")
    @PreAuthorize("hasRole('ADMIN')")
    public String testAdmin() {
        return "Hello ADMIN";
    }
}