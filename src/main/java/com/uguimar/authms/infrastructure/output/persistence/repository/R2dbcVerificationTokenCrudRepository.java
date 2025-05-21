package com.uguimar.authms.infrastructure.output.persistence.repository;

import com.uguimar.authms.infrastructure.output.persistence.entity.VerificationTokenEntity;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface R2dbcVerificationTokenCrudRepository extends ReactiveCrudRepository<VerificationTokenEntity, String> {
    Mono<VerificationTokenEntity> findByToken(String token);

    Mono<VerificationTokenEntity> findByUserId(String userId);

    @Modifying
    @Query("DELETE FROM verification_tokens WHERE user_id = :userId")
    Mono<Void> deleteByUserId(String userId);

    @Modifying
    @Query("UPDATE verification_tokens SET used = true, last_modified_by = 'SYSTEM', last_modified_date = CURRENT_TIMESTAMP WHERE token = :token")
    Mono<Void> markAsUsedByToken(String token);
}
