package com.yobel.authms.application.port.input;

import com.yobel.authms.domain.model.Token;
import reactor.core.publisher.Mono;

public interface AuthenticationUseCase {
    Mono<Token> login(String username, String password);

    Mono<Token> refreshToken(String refreshToken);

    Mono<Void> logout(String token);
}
