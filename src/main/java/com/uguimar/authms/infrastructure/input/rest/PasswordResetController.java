package com.uguimar.authms.infrastructure.input.rest;

import com.uguimar.authms.application.port.input.PasswordResetUseCase;
import com.uguimar.authms.domain.exception.InvalidTokenException;
import com.uguimar.authms.domain.exception.UserNotFoundException;
import com.uguimar.authms.infrastructure.input.rest.dto.PasswordResetRequest;
import com.uguimar.authms.infrastructure.input.rest.dto.PasswordResetValidationRequest;
import com.uguimar.authms.infrastructure.input.rest.dto.RequestPasswordResetRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth/password-reset")
@RequiredArgsConstructor
@Log4j2
public class PasswordResetController {

    private final PasswordResetUseCase passwordResetUseCase;

    @PostMapping("/request")
    public Mono<ResponseEntity<Map<String, Object>>> requestPasswordReset(@RequestBody RequestPasswordResetRequest request) {
        return passwordResetUseCase.requestPasswordReset(request.getEmail())
                .map(userId -> {
                    Map<String, Object> response = new HashMap<>();
                    response.put("success", true);
                    response.put("message", "Se ha enviado un código de restablecimiento a su correo electrónico");
                    response.put("userId", userId);
                    return ResponseEntity.ok(response);
                })
                .onErrorResume(UserNotFoundException.class, e -> {
                    log.warn("Usuario no encontrado: {}", e.getMessage());
                    Map<String, Object> response = new HashMap<>();
                    response.put("success", false);
                    response.put("message", "No se encontró un usuario con este correo electrónico");
                    return Mono.just(ResponseEntity.badRequest().body(response));
                })
                .onErrorResume(e -> {
                    log.error("Error al solicitar restablecimiento: {}", e.getMessage());
                    Map<String, Object> response = new HashMap<>();
                    response.put("success", false);
                    response.put("message", "Error al procesar la solicitud");
                    return Mono.just(ResponseEntity.status(500).body(response));
                });
    }

    @PostMapping("/validate")
    public Mono<ResponseEntity<Map<String, Object>>> validateResetCode(@RequestBody PasswordResetValidationRequest request) {
        return passwordResetUseCase.validateResetCode(request.getUserId(), request.getCode())
                .map(valid -> {
                    Map<String, Object> response = new HashMap<>();
                    if (valid) {
                        response.put("success", true);
                        response.put("message", "Código válido");
                    } else {
                        response.put("success", false);
                        response.put("message", "Código inválido");
                    }
                    return ResponseEntity.ok(response);
                })
                .onErrorResume(InvalidTokenException.class, e -> {
                    log.warn("Token inválido: {}", e.getMessage());
                    Map<String, Object> response = new HashMap<>();
                    response.put("success", false);
                    response.put("message", e.getMessage());
                    return Mono.just(ResponseEntity.badRequest().body(response));
                })
                .onErrorResume(e -> {
                    log.error("Error al validar código: {}", e.getMessage());
                    Map<String, Object> response = new HashMap<>();
                    response.put("success", false);
                    response.put("message", "Error al validar código");
                    return Mono.just(ResponseEntity.status(500).body(response));
                });
    }

    @PostMapping("/reset")
    public Mono<ResponseEntity<Map<String, Object>>> resetPassword(@RequestBody PasswordResetRequest request) {
        return passwordResetUseCase.resetPassword(request.getUserId(), request.getCode(), request.getNewPassword())
                .map(success -> {
                    Map<String, Object> response = new HashMap<>();
                    response.put("success", true);
                    response.put("message", "Contraseña restablecida exitosamente");
                    return ResponseEntity.ok(response);
                })
                .onErrorResume(InvalidTokenException.class, e -> {
                    log.warn("Token inválido: {}", e.getMessage());
                    Map<String, Object> response = new HashMap<>();
                    response.put("success", false);
                    response.put("message", e.getMessage());
                    return Mono.just(ResponseEntity.badRequest().body(response));
                })
                .onErrorResume(e -> {
                    log.error("Error al restablecer contraseña: {}", e.getMessage());
                    Map<String, Object> response = new HashMap<>();
                    response.put("success", false);
                    response.put("message", "Error al restablecer contraseña");
                    return Mono.just(ResponseEntity.status(500).body(response));
                });
    }
}