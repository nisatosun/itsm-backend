package com.nisa.itsm.security;

import com.nisa.itsm.user.dto.UserProfileDto;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class UserProfileMapper {

    public UserProfileDto fromJwt(Jwt jwt) {
        String id = jwt.getSubject();
        String username = getClaim(jwt, "preferred_username");
        String email = getClaim(jwt, "email");
        String firstName = getClaim(jwt, "given_name");
        String lastName = getClaim(jwt, "family_name");
        String fullName = buildFullName(firstName, lastName);
        List<String> roles = extractRoles(jwt);

        return new UserProfileDto(
                id,
                username,
                email,
                firstName,
                lastName,
                fullName,
                roles
        );
    }

    private String getClaim(Jwt jwt, String claimName) {
        Object value = jwt.getClaims().get(claimName);
        return value != null ? value.toString() : null;
    }

    private String buildFullName(String firstName, String lastName) {
        String fn = firstName != null ? firstName.trim() : "";
        String ln = lastName != null ? lastName.trim() : "";
        String fullName = (fn + " " + ln).trim();
        return fullName.isBlank() ? null : fullName;
    }

    private List<String> extractRoles(Jwt jwt) {
        List<String> roles = new ArrayList<>();

        Object realmAccessObj = jwt.getClaims().get("realm_access");
        if (realmAccessObj instanceof Map<?, ?> realmAccessMap) {
            Object rolesObj = realmAccessMap.get("roles");
            if (rolesObj instanceof List<?> roleList) {
                for (Object role : roleList) {
                    if (role == null) {
                        continue;
                    }

                    String roleName = role.toString().trim();

                    if (roleName.isBlank()) {
                        continue;
                    }

                    if (isIgnoredRole(roleName)) {
                        continue;
                    }

                    String normalizedRole = normalizeRole(roleName);

                    if (!roles.contains(normalizedRole)) {
                        roles.add(normalizedRole);
                    }
                }
            }
        }

        return roles;
    }

    private boolean isIgnoredRole(String roleName) {
        return roleName.equalsIgnoreCase("offline_access")
                || roleName.equalsIgnoreCase("uma_authorization")
                || roleName.startsWith("default-roles-");
    }

    private String normalizeRole(String roleName) {
        if (roleName == null) {
            return null;
        }

        String cleaned = roleName.trim().toUpperCase();

        // ROLE_ prefix varsa kaldır
        if (cleaned.startsWith("ROLE_")) {
            cleaned = cleaned.substring(5);
        }

        return cleaned;
    }
}
