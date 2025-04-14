package com.uguimar.authms.application.port.input;

import com.uguimar.authms.domain.model.User;
import reactor.core.publisher.Mono;

public interface RegisterUserUseCase {
    Mono<User> registerUser(User user);
}
