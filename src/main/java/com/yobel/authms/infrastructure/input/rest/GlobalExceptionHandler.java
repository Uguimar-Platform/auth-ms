package com.yobel.authms.infrastructure.input.rest;

import com.yobel.authms.domain.exception.AuthenticationException;
import com.yobel.authms.domain.exception.InvalidTokenException;
import com.yobel.authms.domain.exception.UserAlreadyExistsException;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
@Log4j2
public class GlobalExceptionHandler {
    @ExceptionHandler(AuthenticationException.class)
    public Mono<ResponseEntity<Map<String, String>>> handleAuthenticationException(AuthenticationException ex) {
        log.error("Error de autenticación: {}", ex.getMessage());
        Map<String, String> response = new HashMap<>();
        response.put("error", "Error de autenticación");
        response.put("message", ex.getMessage());

        return Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response));
    }

    @ExceptionHandler(InvalidTokenException.class)
    public Mono<ResponseEntity<Map<String, String>>> handleInvalidTokenException(InvalidTokenException ex) {
        log.error("Token inválido: {}", ex.getMessage());
        Map<String, String> response = new HashMap<>();
        response.put("error", "Token inválido");
        response.put("message", ex.getMessage());

        return Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response));
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public Mono<ResponseEntity<Map<String, String>>> handleUserAlreadyExistsException(UserAlreadyExistsException ex) {
        log.error("Usuario ya existe: {}", ex.getMessage());
        Map<String, String> response = new HashMap<>();
        response.put("error", "Error de registro");
        response.put("message", ex.getMessage());

        return Mono.just(ResponseEntity.status(HttpStatus.CONFLICT).body(response));
    }

    @ExceptionHandler(WebExchangeBindException.class)
    public Mono<ResponseEntity<Map<String, Object>>> handleValidationErrors(WebExchangeBindException ex) {
        log.error("Error de validación: {}", ex.getMessage());
        Map<String, Object> response = new HashMap<>();

        String errorMessage = ex.getBindingResult()
                .getAllErrors()
                .stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.joining(", "));

        response.put("error", "Error de validación");
        response.put("message", errorMessage);

        return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response));
    }

    @ExceptionHandler(Exception.class)
    public Mono<ResponseEntity<Map<String, String>>> handleGenericException(Exception ex) {
        log.error("Error inesperado: {}", ex.getMessage(), ex);
        Map<String, String> response = new HashMap<>();
        response.put("error", "Error interno del servidor");
        response.put("message", "Ocurrió un error inesperado. Por favor, inténtalo más tarde.");

        return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response));
    }
}
