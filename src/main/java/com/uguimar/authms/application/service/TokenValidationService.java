package com.uguimar.authms.application.service;

import com.uguimar.authms.application.port.input.TokenValidationUseCase;
import com.uguimar.authms.application.port.output.UserRepository;
import com.uguimar.authms.domain.exception.InvalidTokenException;
import com.uguimar.authms.domain.model.User;
import com.uguimar.authms.infrastructure.output.security.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class TokenValidationService implements TokenValidationUseCase {

    private final JwtProvider jwtProvider;
    private final UserRepository userRepository;

    @Override
    public Mono<User> validateToken(String token) {
        return Mono.fromCallable(() -> jwtProvider.validateToken(token))
                .filter(valid -> valid)
                .switchIfEmpty(Mono.error(new InvalidTokenException("Token inválido o expirado")))
                .then(Mono.fromCallable(() -> jwtProvider.getUserIdFromToken(token)))
                .flatMap(userRepository::findById)
                .switchIfEmpty(Mono.error(new InvalidTokenException("Usuario no encontrado")));
    }

    @Override
    public Mono<Boolean> isTokenValid(String token) {
        return Mono.fromCallable(() -> jwtProvider.validateToken(token));
    }
}
