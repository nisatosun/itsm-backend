package com.nisa.itsm.auth.controller;

import com.nisa.itsm.user.dto.UserProfileDto;
import com.nisa.itsm.security.UserProfileMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Auth Controller", description = "Authentication and authorization endpoints")
public class AuthController {

    private final UserProfileMapper userProfileMapper;

    @GetMapping("/me")
    @Operation(summary = "Get current user profile", description = "Returns authenticated user's profile extracted from JWT token")
    public UserProfileDto me(@AuthenticationPrincipal Jwt jwt) {
        return userProfileMapper.fromJwt(jwt);
    }

    @GetMapping("/test-customer")
    @PreAuthorize("hasRole('CUSTOMER')")
    @Operation(summary = "Customer test endpoint", description = "Accessible only by users with CUSTOMER role")
    public String testCustomer() {
        return "Hello CUSTOMER";
    }

    @GetMapping("/test-admin")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Admin test endpoint", description = "Accessible only by users with ADMIN role")
    public String testAdmin() {
        return "Hello ADMIN";
    }
}