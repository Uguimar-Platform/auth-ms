package com.uguimar.authms.application.port.output;

import reactor.core.publisher.Mono;

public interface EmailService {
    Mono<Boolean> sendVerificationCode(String email, String username, String code);
}
