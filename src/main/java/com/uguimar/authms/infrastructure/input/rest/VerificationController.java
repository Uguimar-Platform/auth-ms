package com.uguimar.authms.infrastructure.input.rest;

import com.uguimar.authms.application.port.input.EmailVerificationUseCase;
import com.uguimar.authms.domain.exception.InvalidTokenException;
import com.uguimar.authms.domain.exception.UserNotFoundException;
import com.uguimar.authms.infrastructure.input.rest.dto.VerificationRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth/verification")
@RequiredArgsConstructor
@Log4j2
public class VerificationController {

    private final EmailVerificationUseCase emailVerificationUseCase;

    @PostMapping("/verify")
    public Mono<ResponseEntity<Map<String, Object>>> verifyEmail(@RequestBody VerificationRequest request) {
        return emailVerificationUseCase.verifyEmail(request.getUserId(), request.getCode())
                .map(user -> {
                    Map<String, Object> response = new HashMap<>();
                    response.put("success", true);
                    response.put("message", "Email verificado correctamente");
                    response.put("verified", true);
                    return ResponseEntity.ok(response);
                })
                .onErrorResume(InvalidTokenException.class, e -> {
                    log.warn("Token inválido: {}", e.getMessage());
                    Map<String, Object> response = new HashMap<>();
                    response.put("success", false);
                    response.put("message", e.getMessage());
                    return Mono.just(ResponseEntity.badRequest().body(response));
                })
                .onErrorResume(UserNotFoundException.class, e -> {
                    log.warn("Usuario no encontrado: {}", e.getMessage());
                    Map<String, Object> response = new HashMap<>();
                    response.put("success", false);
                    response.put("message", e.getMessage());
                    return Mono.just(ResponseEntity.badRequest().body(response));
                })
                .onErrorResume(e -> {
                    log.error("Error al verificar email: {}", e.getMessage());
                    Map<String, Object> response = new HashMap<>();
                    response.put("success", false);
                    response.put("message", "Error al verificar el email");
                    return Mono.just(ResponseEntity.status(500).body(response));
                });
    }

    @PostMapping("/resend/{userId}")
    public Mono<ResponseEntity<Map<String, Object>>> resendVerificationCode(@PathVariable String userId) {
        return emailVerificationUseCase.resendVerificationCode(userId)
                .map(user -> {
                    Map<String, Object> response = new HashMap<>();
                    response.put("success", true);
                    response.put("message", "Código de verificación reenviado");
                    return ResponseEntity.ok(response);
                })
                .onErrorResume(UserNotFoundException.class, e -> {
                    log.warn("Usuario no encontrado: {}", e.getMessage());
                    Map<String, Object> response = new HashMap<>();
                    response.put("success", false);
                    response.put("message", e.getMessage());
                    return Mono.just(ResponseEntity.badRequest().body(response));
                })
                .onErrorResume(e -> {
                    log.error("Error al reenviar código: {}", e.getMessage());
                    Map<String, Object> response = new HashMap<>();
                    response.put("success", false);
                    response.put("message", "Error al reenviar código de verificación");
                    return Mono.just(ResponseEntity.status(500).body(response));
                });
    }
}
