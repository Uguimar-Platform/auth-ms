package com.uguimar.authms.application.port.input;

import com.uguimar.authms.domain.model.User;
import reactor.core.publisher.Mono;

public interface EmailVerificationUseCase {
    Mono<Void> sendVerificationCode(String userId);

    Mono<User> verifyEmail(String userId, String code);

    Mono<User> resendVerificationCode(String userId);
}
