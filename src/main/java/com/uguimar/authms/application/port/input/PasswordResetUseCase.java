package com.uguimar.authms.application.port.input;

import reactor.core.publisher.Mono;

public interface PasswordResetUseCase {
    Mono<String> requestPasswordReset(String email);

    Mono<Boolean> validateResetCode(String userId, String code);

    Mono<Boolean> resetPassword(String userId, String code, String newPassword);
}
