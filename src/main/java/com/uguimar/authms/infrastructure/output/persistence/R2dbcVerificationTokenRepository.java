package com.uguimar.authms.infrastructure.output.persistence;

import com.uguimar.authms.application.port.output.VerificationTokenRepository;
import com.uguimar.authms.domain.model.VerificationToken;
import com.uguimar.authms.infrastructure.output.persistence.entity.VerificationTokenEntity;
import com.uguimar.authms.infrastructure.output.persistence.repository.R2dbcVerificationTokenCrudRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class R2dbcVerificationTokenRepository implements VerificationTokenRepository {

    private final R2dbcVerificationTokenCrudRepository tokenRepository;

    @Override
    public Mono<VerificationToken> save(VerificationToken token) {
        if (token.getId() == null) {
            token.setId(UUID.randomUUID().toString());
        }

        VerificationTokenEntity entity = mapToEntity(token);
        entity.markNew();

        return tokenRepository.save(entity)
                .map(this::mapToDomain);
    }

    @Override
    public Mono<VerificationToken> findByToken(String token) {
        return tokenRepository.findByToken(token)
                .map(this::mapToDomain);
    }

    @Override
    public Mono<VerificationToken> findByUserId(String userId) {
        return tokenRepository.findByUserId(userId)
                .map(this::mapToDomain);
    }

    @Override
    public Mono<Void> deleteByUserId(String userId) {
        return tokenRepository.deleteByUserId(userId);
    }

    @Override
    public Mono<Void> markAsUsedByToken(String token) {
        return tokenRepository.markAsUsedByToken(token);
    }

    private VerificationToken mapToDomain(VerificationTokenEntity entity) {
        VerificationToken token = VerificationToken.builder()
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

    private VerificationTokenEntity mapToEntity(VerificationToken domain) {
        VerificationTokenEntity entity = VerificationTokenEntity.builder()
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