package com.yobel.authms.infrastructure.output.persistence;

import com.yobel.authms.application.port.output.TokenRepository;
import com.yobel.authms.domain.model.Token;
import com.yobel.authms.domain.model.TokenType;
import com.yobel.authms.infrastructure.output.persistence.entity.TokenEntity;
import com.yobel.authms.infrastructure.output.persistence.repository.R2dbcTokenCrudRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class R2dbcTokenRepository implements TokenRepository {

    private final R2dbcTokenCrudRepository tokenRepository;

    @Override
    public Mono<Token> save(Token token) {
        if (token.getId() == null) {
            token.setId(UUID.randomUUID().toString());
        }

        TokenEntity tokenEntity = mapToEntity(token);
        tokenEntity.markNew();

        return tokenRepository.save(tokenEntity)
                .map(this::mapToDomain);
    }

    @Override
    public Mono<Token> findByValue(String value) {
        return tokenRepository.findByValue(value)
                .map(this::mapToDomain);
    }

    @Override
    public Mono<Void> deleteByValue(String value) {
        return tokenRepository.deleteByValue(value);
    }

    @Override
    public Mono<Void> deleteAllByUserId(String userId) {
        return tokenRepository.deleteAllByUserId(userId);
    }

    private Token mapToDomain(TokenEntity entity) {
        return Token.builder()
                .id(entity.getId())
                .userId(entity.getUserId())
                .value(entity.getValue())
                .expiryDate(entity.getExpiryDate())
                .type(TokenType.valueOf(entity.getTokenType()))
                .build();
    }

    private TokenEntity mapToEntity(Token domain) {
        return TokenEntity.builder()
                .id(domain.getId())
                .userId(domain.getUserId())
                .value(domain.getValue())
                .expiryDate(domain.getExpiryDate())
                .tokenType(domain.getType().name())
                .build();
    }
}