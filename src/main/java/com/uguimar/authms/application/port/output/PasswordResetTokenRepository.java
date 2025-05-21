package com.uguimar.authms.application.port.output;

import com.uguimar.authms.domain.model.PasswordResetToken;
import reactor.core.publisher.Mono;

public interface PasswordResetTokenRepository {
    Mono<PasswordResetToken> save(PasswordResetToken token);

    Mono<PasswordResetToken> findByToken(String token);

    Mono<PasswordResetToken> findByUserId(String userId);

    Mono<Void> deleteByUserId(String userId);

    Mono<Void> markAsUsedByToken(String token);
}
