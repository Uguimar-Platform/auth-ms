package com.uguimar.authms.application.port.output;

import com.uguimar.authms.domain.model.VerificationToken;
import reactor.core.publisher.Mono;

public interface VerificationTokenRepository {
    Mono<VerificationToken> save(VerificationToken token);

    Mono<VerificationToken> findByToken(String token);

    Mono<VerificationToken> findByUserId(String userId);

    Mono<Void> deleteByUserId(String userId);
}
