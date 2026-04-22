package com.nisa.itsm.auth.controller;

import com.nisa.itsm.security.UserProfileMapper;
import com.nisa.itsm.user.dto.UserProfileDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserProfileMapper userProfileMapper;

    @GetMapping("/me")
    public UserProfileDto me(@AuthenticationPrincipal Jwt jwt) {
        return userProfileMapper.fromJwt(jwt);
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
