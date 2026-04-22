package com.nisa.itsm.user.dto;

import java.util.List;

public record UserProfileDto(
        String id,
        String username,
        String email,
        String firstName,
        String lastName,
        String fullName,
        List<String> roles
) {
}
