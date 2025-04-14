package com.yobel.authms.infrastructure.input.rest;

import com.yobel.authms.application.port.input.AuthenticationUseCase;
import com.yobel.authms.application.port.input.RegisterUserUseCase;
import com.yobel.authms.domain.exception.AuthenticationException;
import com.yobel.authms.domain.exception.UserAlreadyExistsException;
import com.yobel.authms.infrastructure.input.rest.dto.LoginRequest;
import com.yobel.authms.infrastructure.input.rest.dto.LoginResponse;
import com.yobel.authms.infrastructure.input.rest.dto.RegisterRequest;
import com.yobel.authms.infrastructure.input.rest.dto.RegisterResponse;
import com.yobel.authms.infrastructure.input.rest.mapper.AuthMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Log4j2
public class AuthController {

    private final AuthenticationUseCase authenticationUseCase;
    private final RegisterUserUseCase registerUserUseCase;
    private final AuthMapper authMapper;

    @PostMapping("/login")
    public Mono<ResponseEntity<LoginResponse>> login(@Valid @RequestBody LoginRequest request) {
        return authenticationUseCase.login(request.getUsername(), request.getPassword())
                .map(tokenPair -> {
                    LoginResponse response = LoginResponse.builder()
                            .accessToken(tokenPair.getAccessToken().getValue())
                            .refreshToken(tokenPair.getRefreshToken().getValue())
                            .expiryDate(tokenPair.getAccessToken().getExpiryDate())
                            .tokenType("Bearer")
                            .build();
                    return ResponseEntity.ok(response);
                })
                .onErrorResume(AuthenticationException.class, e -> {
                    log.error("Error de autenticación: {}", e.getMessage());
                    return Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
                })
                .onErrorResume(e -> {
                    log.error("Error inesperado durante login: {}", e.getMessage());
                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
                });
    }

    @PostMapping("/register")
    public Mono<ResponseEntity<RegisterResponse>> register(@Valid @RequestBody RegisterRequest request) {
        return registerUserUseCase.registerUser(authMapper.mapToUser(request))
                .map(user -> ResponseEntity
                        .status(HttpStatus.CREATED)
                        .body(authMapper.mapToRegisterResponse(user)))
                .onErrorResume(UserAlreadyExistsException.class, e -> {
                    log.error("Error de registro: {}", e.getMessage());
                    return Mono.just(ResponseEntity.status(HttpStatus.CONFLICT).build());
                })
                .onErrorResume(e -> {
                    log.error("Error inesperado durante registro: {}", e.getMessage());
                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
                });
    }

    @PostMapping("/logout")
    public Mono<ResponseEntity<Void>> logout(@RequestHeader("Authorization") String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            return authenticationUseCase.logout(token)
                    .thenReturn(ResponseEntity.ok().<Void>build())
                    .onErrorResume(e -> {
                        log.error("Error durante logout: {}", e.getMessage());
                        return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
                    });
        }
        return Mono.just(ResponseEntity.badRequest().build());
    }

    @PostMapping("/refresh")
    public Mono<ResponseEntity<LoginResponse>> refresh(@RequestHeader("Refresh-Token") String refreshToken) {
        return authenticationUseCase.refreshToken(refreshToken)
                .map(token -> {
                    LoginResponse response = LoginResponse.builder()
                            .accessToken(token.getValue())
                            .expiryDate(token.getExpiryDate())
                            .build();
                    return ResponseEntity.ok(response);
                })
                .onErrorResume(AuthenticationException.class, e -> {
                    log.error("Error al refrescar token: {}", e.getMessage());
                    return Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
                })
                .onErrorResume(e -> {
                    log.error("Error inesperado al refrescar token: {}", e.getMessage());
                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
                });
    }
}
