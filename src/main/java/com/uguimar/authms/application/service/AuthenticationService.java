package com.uguimar.authms.application.service;

import com.uguimar.authms.application.port.input.AuthenticationUseCase;
import com.uguimar.authms.application.port.output.PasswordEncoder;
import com.uguimar.authms.application.port.output.TokenRepository;
import com.uguimar.authms.application.port.output.UserRepository;
import com.uguimar.authms.domain.exception.AuthenticationException;
import com.uguimar.authms.domain.model.Token;
import com.uguimar.authms.domain.model.TokenPair;
import com.uguimar.authms.domain.model.TokenType;
import com.uguimar.authms.infrastructure.output.security.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
public class AuthenticationService implements AuthenticationUseCase {

    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    @Override
    public Mono<TokenPair> login(String username, String password) {
        return userRepository.findByUsername(username)
                .switchIfEmpty(Mono.error(new AuthenticationException("Usuario no encontrado")))
                .filter(user -> passwordEncoder.matches(password, user.getPassword()))
                .switchIfEmpty(Mono.error(new AuthenticationException("Credenciales inválidas")))
                .flatMap(user -> {
                    String accessTokenValue = jwtProvider.generateToken(user, TokenType.ACCESS);
                    String refreshTokenValue = jwtProvider.generateToken(user, TokenType.REFRESH);

                    Token accessToken = Token.builder()
                            .userId(user.getId())
                            .value(accessTokenValue)
                            .expiryDate(Instant.now().plus(30, ChronoUnit.MINUTES))
                            .type(TokenType.ACCESS)
                            .build();

                    Token refreshToken = Token.builder()
                            .userId(user.getId())
                            .value(refreshTokenValue)
                            .expiryDate(Instant.now().plus(7, ChronoUnit.DAYS))
                            .type(TokenType.REFRESH)
                            .build();

                    return tokenRepository.save(accessToken)
                            .then(tokenRepository.save(refreshToken))
                            .thenReturn(TokenPair.builder()
                                    .accessToken(accessToken)
                                    .refreshToken(refreshToken)
                                    .build());
                });
    }

    @Override
    public Mono<Token> refreshToken(String refreshToken) {
        return tokenRepository.findByValue(refreshToken)
                .switchIfEmpty(Mono.error(new AuthenticationException("Token inválido")))
                .filter(token -> !token.isExpired() && token.getType() == TokenType.REFRESH)
                .switchIfEmpty(Mono.error(new AuthenticationException("Token expirado o de tipo incorrecto")))
                .flatMap(token -> userRepository.findById(token.getUserId()))
                .flatMap(user -> {
                    String newAccessTokenValue = jwtProvider.generateToken(user, TokenType.ACCESS);
                    Token newAccessToken = Token.builder()
                            .userId(user.getId())
                            .value(newAccessTokenValue)
                            .expiryDate(Instant.now().plus(30, ChronoUnit.MINUTES))
                            .type(TokenType.ACCESS)
                            .build();

                    return tokenRepository.save(newAccessToken);
                });
    }

    @Override
    public Mono<Void> logout(String token) {
        return tokenRepository.deleteByValue(token);
    }
}
