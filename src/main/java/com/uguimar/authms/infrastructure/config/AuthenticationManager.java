package com.uguimar.authms.infrastructure.config;

import com.uguimar.authms.application.port.input.TokenValidationUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.HashSet;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class AuthenticationManager implements ReactiveAuthenticationManager {
    private final TokenValidationUseCase tokenValidationUseCase;

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        String authToken = authentication.getCredentials().toString();

        return tokenValidationUseCase.validateToken(authToken)
                .map(user -> {
                    // Get authorities from roles
                    Set<SimpleGrantedAuthority> authorities = new HashSet<>();

                    // Add role-based authorities
                    user.getRoles().forEach(role -> {
                        authorities.add(new SimpleGrantedAuthority("ROLE_" + role.getName()));

                        // Add permission-based authorities
                        if (role.getPermissions() != null) {
                            role.getPermissions().forEach(permission ->
                                    authorities.add(new SimpleGrantedAuthority("PERMISSION_" + permission.getName()))
                            );
                        }
                    });

                    return new UsernamePasswordAuthenticationToken(
                            user.getUsername(),
                            null,
                            authorities
                    );
                });
    }
}