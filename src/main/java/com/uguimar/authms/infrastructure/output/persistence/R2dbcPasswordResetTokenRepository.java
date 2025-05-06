package com.uguimar.authms.infrastructure.output.persistence;

import com.uguimar.authms.application.port.output.PasswordResetTokenRepository;
import com.uguimar.authms.domain.model.PasswordResetToken;
import com.uguimar.authms.infrastructure.output.persistence.entity.PasswordResetTokenEntity;
import com.uguimar.authms.infrastructure.output.persistence.repository.R2dbcPasswordResetTokenCrudRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class R2dbcPasswordResetTokenRepository implements PasswordResetTokenRepository {

    private final R2dbcPasswordResetTokenCrudRepository tokenRepository;

    @Override
    public Mono<PasswordResetToken> save(PasswordResetToken token) {
        if (token.getId() == null) {
            token.setId(UUID.randomUUID().toString());
        }

        PasswordResetTokenEntity entity = mapToEntity(token);
        entity.markNew();

        return tokenRepository.save(entity)
                .map(this::mapToDomain);
    }

    @Override
    public Mono<PasswordResetToken> findByToken(String token) {
        return tokenRepository.findByToken(token)
                .map(this::mapToDomain);
    }

    @Override
    public Mono<PasswordResetToken> findByUserId(String userId) {
        return tokenRepository.findByUserId(userId)
                .map(this::mapToDomain);
    }

    @Override
    public Mono<Void> deleteByUserId(String userId) {
        return tokenRepository.deleteByUserId(userId);
    }

    private PasswordResetToken mapToDomain(PasswordResetTokenEntity entity) {
        PasswordResetToken token = PasswordResetToken.builder()
                .id(entity.getId())
                .userId(entity.getUserId())
                .token(entity.getToken())
                .expiryDate(entity.getExpiryDate())
                .used(entity.isUsed())
                .build();

        // Set audit fields
        token.setCreatedBy(entity.getCreatedBy());
        token.setCreatedDate(entity.getCreatedDate());
        token.setLastModifiedBy(entity.getLastModifiedBy());
        token.setLastModifiedDate(entity.getLastModifiedDate());

        return token;
    }

    private PasswordResetTokenEntity mapToEntity(PasswordResetToken domain) {
        PasswordResetTokenEntity entity = PasswordResetTokenEntity.builder()
                .id(domain.getId())
                .userId(domain.getUserId())
                .token(domain.getToken())
                .expiryDate(domain.getExpiryDate())
                .used(domain.isUsed())
                .build();

        // Set audit fields from domain if available (for updates)
        entity.setCreatedBy(domain.getCreatedBy());
        entity.setCreatedDate(domain.getCreatedDate());
        entity.setLastModifiedBy(domain.getLastModifiedBy());
        entity.setLastModifiedDate(domain.getLastModifiedDate());

        return entity;
    }
}
