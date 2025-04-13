package com.yobel.authms.application.port.output;

import com.yobel.authms.domain.model.User;
import reactor.core.publisher.Mono;

public interface UserRepository {
    Mono<User> findById(String id);

    Mono<User> findByUsername(String username);

    Mono<User> findByEmail(String email);

    Mono<User> save(User user);

    Mono<Boolean> existsByUsername(String username);

    Mono<Boolean> existsByEmail(String email);
}
