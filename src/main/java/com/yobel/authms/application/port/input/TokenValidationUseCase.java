package com.yobel.authms.application.port.input;

import com.yobel.authms.domain.model.User;
import reactor.core.publisher.Mono;

public interface TokenValidationUseCase {
    Mono<User> validateToken(String token);

    Mono<Boolean> isTokenValid(String token);
}
