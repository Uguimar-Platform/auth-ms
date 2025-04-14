package com.uguimar.authms.application.port.output;

import com.uguimar.authms.domain.model.Token;
import reactor.core.publisher.Mono;

public interface TokenRepository {
    Mono<Token> save(Token token);

    Mono<Token> findByValue(String value);

    Mono<Void> deleteByValue(String value);

    Mono<Void> deleteAllByUserId(String userId);
}
