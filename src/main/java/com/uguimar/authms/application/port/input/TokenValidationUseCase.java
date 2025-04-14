package com.uguimar.authms.application.port.input;

import com.uguimar.authms.domain.model.User;
import reactor.core.publisher.Mono;

public interface TokenValidationUseCase {
    Mono<User> validateToken(String token);

    Mono<Boolean> isTokenValid(String token);
}
