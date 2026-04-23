package com.nisa.itsm.security;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class KeycloakJwtConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {
        Collection<SimpleGrantedAuthority> authorities = extractAuthorities(jwt);

        String username = jwt.getClaimAsString("preferred_username");

        return new JwtAuthenticationToken(jwt, authorities, username);
    }

    private Collection<SimpleGrantedAuthority> extractAuthorities(Jwt jwt) {
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();

        Object realmAccessObj = jwt.getClaims().get("realm_access");

        if (realmAccessObj instanceof Map<?, ?> realmAccessMap) {
            Object rolesObj = realmAccessMap.get("roles");

            if (rolesObj instanceof List<?> roles) {
                for (Object role : roles) {
                    if (role == null) {
                        continue;
                    }

                    String roleName = role.toString().trim();

                    if (roleName.isBlank() || isIgnoredRole(roleName)) {
                        continue;
                    }

                    String normalizedRole = normalizeRole(roleName);

                    authorities.add(new SimpleGrantedAuthority(normalizedRole));
                }
            }
        }

        return authorities;
    }

    private boolean isIgnoredRole(String roleName) {
        return roleName.equalsIgnoreCase("offline_access")
                || roleName.equalsIgnoreCase("uma_authorization")
                || roleName.startsWith("default-roles-");
    }

    private String normalizeRole(String roleName) {
        String cleaned = roleName.trim().toUpperCase();

        if (cleaned.startsWith("ROLE_")) {
            cleaned = cleaned.substring(5);
        }

        return cleaned;
    }
}
