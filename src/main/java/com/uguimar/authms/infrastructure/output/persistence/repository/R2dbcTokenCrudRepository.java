package com.uguimar.authms.infrastructure.output.persistence.repository;

import com.uguimar.authms.infrastructure.output.persistence.entity.TokenEntity;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface R2dbcTokenCrudRepository extends ReactiveCrudRepository<TokenEntity, String> {

    Mono<TokenEntity> findByValue(String value);

    @Modifying
    @Query("DELETE FROM tokens WHERE value = :value")
    Mono<Void> deleteByValue(String value);

    @Modifying
    @Query("DELETE FROM tokens WHERE user_id = :userId")
    Mono<Void> deleteAllByUserId(String userId);
}