package com.yobel.authms.application.port.input;

import com.yobel.authms.domain.model.User;
import reactor.core.publisher.Mono;

public interface RegisterUserUseCase {
    Mono<User> registerUser(User user);
}
