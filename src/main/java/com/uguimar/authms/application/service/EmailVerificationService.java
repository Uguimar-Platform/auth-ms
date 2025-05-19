package com.uguimar.authms.application.service;

import com.uguimar.authms.application.port.input.EmailVerificationUseCase;
import com.uguimar.authms.application.port.output.EmailService;
import com.uguimar.authms.application.port.output.UserRepository;
import com.uguimar.authms.application.port.output.VerificationTokenRepository;
import com.uguimar.authms.domain.exception.InvalidTokenException;
import com.uguimar.authms.domain.exception.UserAlreadyVerifiedException;
import com.uguimar.authms.domain.exception.UserNotFoundException;
import com.uguimar.authms.domain.model.AuditActionType;
import com.uguimar.authms.domain.model.User;
import com.uguimar.authms.domain.model.VerificationToken;
import com.uguimar.authms.infrastructure.config.AuditingConfig;
import com.uguimar.authms.infrastructure.output.notification.KafkaNotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
@Log4j2
public class EmailVerificationService implements EmailVerificationUseCase {

    private final UserRepository userRepository;
    private final VerificationTokenRepository tokenRepository;
    private final EmailService emailService;

    private static final int CODE_LENGTH = 6;
    private static final String CODE_CHARS = "0123456789";
    private static final SecureRandom random = new SecureRandom();

    @Override
    public Mono<Void> sendVerificationCode(String userId) {
        return userRepository.findById(userId)
                .switchIfEmpty(Mono.error(new UserNotFoundException("Usuario no encontrado")))
                .flatMap(user -> {
                    // Verificar si ya existe un token o crear uno nuevo
                    return tokenRepository.findByUserId(userId)
                            .flatMap(existingToken -> {
                                // Si el token ya se usó o expiró, generar uno nuevo
                                if (existingToken.isUsed() || existingToken.isExpired()) {
                                    return generateAndSaveToken(user);
                                }
                                // Si el token aún es válido, enviarlo de nuevo
                                return sendVerificationEmail(user, existingToken.getToken());
                            })
                            .switchIfEmpty(generateAndSaveToken(user));
                })
                .then();
    }

    @Override
    public Mono<User> verifyEmail(String userId, String code) {
        return tokenRepository.findByUserId(userId)
                .filter(token -> !token.isUsed())
                .switchIfEmpty(Mono.error(new InvalidTokenException("El token ya ha sido utilizado")))
                .filter(token -> !token.isExpired())
                .switchIfEmpty(Mono.error(new InvalidTokenException("El token ha expirado")))
                .filter(token -> token.getToken().equals(code))
                .switchIfEmpty(Mono.error(new InvalidTokenException("Código de verificación o ID del usuario inválidos")))
                .flatMap(token -> {
                    // Marcar el token como usado
                    AuditingConfig.setAuditor(AuditActionType.SYSTEM.getValue());
                    return tokenRepository.markAsUsedByToken(code)
                            .doFinally(signal -> AuditingConfig.clearAuditor());
                })
                .then(Mono.defer(() -> userRepository.findById(userId)))
                .flatMap(user -> {
                    // Marcar el usuario como verificado
                    AuditingConfig.setAuditor(AuditActionType.SELF_REGISTRATION.getValue());
                    return userRepository.markAsVerifiedById(user.getId())
                            .then(Mono.fromCallable(() -> user))
                            .doFinally(signal -> AuditingConfig.clearAuditor());
                })
                .flatMap(user -> {
                    // Enviar correo de bienvenida después de verificar
                    return ((KafkaNotificationService) emailService)
                            .sendWelcomeEmail(user.getEmail(), user.getUsername())
                            .thenReturn(user);
                });
    }

    @Override
    public Mono<User> resendVerificationCode(String userId) {
        return userRepository.findById(userId)
                .switchIfEmpty(Mono.error(new UserNotFoundException("Usuario no encontrado")))
                .flatMap(user -> {
                    if (user.isVerified()) {
                        return Mono.error(new UserAlreadyVerifiedException("El usuario ya está verificado"));
                    }

                    // Eliminar tokens existentes
                    return tokenRepository.deleteByUserId(userId)
                            .then(generateAndSaveToken(user))
                            .thenReturn(user);
                });
    }

    private Mono<VerificationToken> generateAndSaveToken(User user) {
        String code = generateVerificationCode();

        VerificationToken token = VerificationToken.builder()
                .userId(user.getId())
                .token(code)
                .expiryDate(Instant.now().plus(24, ChronoUnit.HOURS))
                .used(false)
                .build();

        // Establecer auditor específico para esta operación
        AuditingConfig.setAuditor(AuditActionType.SYSTEM.getValue());

        return tokenRepository.save(token)
                .doFinally(signal -> AuditingConfig.clearAuditor())
                .flatMap(savedToken -> sendVerificationEmail(user, code)
                        .thenReturn(savedToken));
    }

    private Mono<Boolean> sendVerificationEmail(User user, String code) {
        return emailService.sendVerificationCode(user.getEmail(), user.getUsername(), code)
                .doOnNext(success -> {
                    if (success) {
                        log.info("Verification code sent to user: {}", user.getUsername());
                    } else {
                        log.error("Failed to send verification code to user: {}", user.getUsername());
                    }
                })
                .onErrorResume(e -> {
                    log.error("Error sending verification code to user {}: {}", user.getUsername(), e.getMessage());
                    return Mono.just(false);
                });
    }

    private String generateVerificationCode() {
        StringBuilder sb = new StringBuilder(CODE_LENGTH);
        for (int i = 0; i < CODE_LENGTH; i++) {
            sb.append(CODE_CHARS.charAt(random.nextInt(CODE_CHARS.length())));
        }
        return sb.toString();
    }
}