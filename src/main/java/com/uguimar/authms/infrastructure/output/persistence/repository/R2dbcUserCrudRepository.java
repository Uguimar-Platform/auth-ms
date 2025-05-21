package com.uguimar.authms.infrastructure.output.persistence.repository;

import com.uguimar.authms.infrastructure.output.persistence.entity.UserEntity;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface R2dbcUserCrudRepository extends ReactiveCrudRepository<UserEntity, String> {

    Mono<UserEntity> findByUsername(String username);

    Mono<UserEntity> findByEmail(String email);

    Mono<Boolean> existsByUsername(String username);

    Mono<Boolean> existsByEmail(String email);

    @Query("SELECT r.id, r.name FROM roles r JOIN user_roles ur ON r.id = ur.role_id WHERE ur.user_id = :userId")
    Flux<Object[]> findRolesByUserId(String userId);

    @Modifying
    @Query("UPDATE users SET verified = true WHERE id = :id")
    Mono<Void> markAsVerifiedById(String id);
}
