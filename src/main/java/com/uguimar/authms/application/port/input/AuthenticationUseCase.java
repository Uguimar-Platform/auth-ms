package com.uguimar.authms.application.port.input;

import com.uguimar.authms.domain.model.Token;
import com.uguimar.authms.domain.model.TokenPair;

import reactor.core.publisher.Mono;

public interface AuthenticationUseCase {
    Mono<TokenPair> login(String username, String password);

    Mono<Token> refreshToken(String refreshToken);

    Mono<Void> logout(String token);
}
