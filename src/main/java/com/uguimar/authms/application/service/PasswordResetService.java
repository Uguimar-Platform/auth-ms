package com.uguimar.authms.application.service;

import com.uguimar.authms.application.port.input.PasswordResetUseCase;
import com.uguimar.authms.application.port.output.PasswordEncoder;
import com.uguimar.authms.application.port.output.PasswordResetNotificationService;
import com.uguimar.authms.application.port.output.PasswordResetTokenRepository;
import com.uguimar.authms.application.port.output.UserRepository;
import com.uguimar.authms.domain.exception.InvalidTokenException;
import com.uguimar.authms.domain.exception.UserNotFoundException;
import com.uguimar.authms.domain.model.AuditActionType;
import com.uguimar.authms.domain.model.PasswordResetToken;
import com.uguimar.authms.domain.model.User;
import com.uguimar.authms.infrastructure.config.AuditingConfig;
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
public class PasswordResetService implements PasswordResetUseCase {

    private final UserRepository userRepository;
    private final PasswordResetTokenRepository tokenRepository;
    private final PasswordResetNotificationService notificationService;
    private final PasswordEncoder passwordEncoder;

    private static final int CODE_LENGTH = 6;
    private static final String CODE_CHARS = "0123456789";
    private static final SecureRandom random = new SecureRandom();

    @Override
    public Mono<String> requestPasswordReset(String email) {
        return userRepository.findByEmail(email)
                .switchIfEmpty(Mono.error(new UserNotFoundException("No existe un usuario con este correo electrónico")))
                .flatMap(user -> {
                    // First, delete existing tokens for the user
                    return tokenRepository.deleteByUserId(user.getId())
                            .then(generateAndSaveToken(user))
                            .map(token -> user.getId());
                });
    }

    @Override
    public Mono<Boolean> validateResetCode(String userId, String code) {
        return tokenRepository.findByUserId(userId)
                .switchIfEmpty(Mono.error(new InvalidTokenException("No se encontró un token de restablecimiento para este usuario")))
                .filter(token -> !token.isUsed())
                .switchIfEmpty(Mono.error(new InvalidTokenException("El token ya ha sido utilizado")))
                .filter(token -> !token.isExpired())
                .switchIfEmpty(Mono.error(new InvalidTokenException("El token ha expirado")))
                .map(token -> token.getToken().equals(code));
    }

    @Override
    public Mono<Boolean> resetPassword(String userId, String code, String newPassword) {
        return validateResetCode(userId, code)
                .filter(valid -> valid)
                .switchIfEmpty(Mono.error(new InvalidTokenException("Código de restablecimiento inválido")))
                .flatMap(valid -> tokenRepository.findByUserId(userId))
                .flatMap(token -> {
                    // Set the token as used
                    token.setUsed(true);
                    return tokenRepository.save(token);
                })
                .flatMap(token -> userRepository.findById(userId))
                .flatMap(user -> {
                    // Update the user's password
                    user.setPassword(passwordEncoder.encode(newPassword));

                    // Set the auditor for this operation
                    AuditingConfig.setAuditor(AuditActionType.SYSTEM.getValue());

                    return userRepository.save(user)
                            .doFinally(signal -> AuditingConfig.clearAuditor());
                })
                .map(user -> true);
    }

    private Mono<PasswordResetToken> generateAndSaveToken(User user) {
        String code = generateResetCode();

        PasswordResetToken token = PasswordResetToken.builder()
                .userId(user.getId())
                .token(code)
                .expiryDate(Instant.now().plus(15, ChronoUnit.MINUTES)) // Less time for security
                .used(false)
                .build();

        // Set the auditor for this operation
        AuditingConfig.setAuditor(AuditActionType.SYSTEM.getValue());

        return tokenRepository.save(token)
                .doFinally(signal -> AuditingConfig.clearAuditor())
                .flatMap(savedToken -> sendPasswordResetEmail(user, code)
                        .thenReturn(savedToken));
    }

    private Mono<Boolean> sendPasswordResetEmail(User user, String code) {
        return notificationService.sendPasswordResetCode(user.getEmail(), user.getUsername(), code)
                .doOnNext(success -> {
                    if (success) {
                        log.info("Password reset code sent to user: {}", user.getUsername());
                    } else {
                        log.error("Failed to send password reset code to user: {}", user.getUsername());
                    }
                })
                .onErrorResume(e -> {
                    log.error("Error sending password reset code to user {}: {}", user.getUsername(), e.getMessage());
                    return Mono.just(false);
                });
    }

    private String generateResetCode() {
        StringBuilder sb = new StringBuilder(CODE_LENGTH);
        for (int i = 0; i < CODE_LENGTH; i++) {
            sb.append(CODE_CHARS.charAt(random.nextInt(CODE_CHARS.length())));
        }
        return sb.toString();
    }
}
