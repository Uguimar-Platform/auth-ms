package com.uguimar.authms.application.port.output;

import reactor.core.publisher.Mono;

public interface PasswordResetNotificationService {
    Mono<Boolean> sendPasswordResetCode(String email, String username, String code);
}
