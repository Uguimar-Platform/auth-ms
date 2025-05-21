package com.uguimar.authms.infrastructure.output.notification;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.uguimar.authms.application.port.output.EmailService;
import com.uguimar.authms.application.port.output.PasswordResetNotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Log4j2
public class KafkaNotificationService implements EmailService, PasswordResetNotificationService {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private final String verificationTopic;
    private final String welcomeTopic;
    private final String passwordResetTopic;

    @Override
    public Mono<Boolean> sendVerificationCode(String email, String username, String code) {
        return Mono.fromCallable(() -> {
            try {
                Map<String, String> event = Map.of(
                        "email", email,
                        "username", username,
                        "code", code
                );

                String payload = objectMapper.writeValueAsString(event);
                kafkaTemplate.send(verificationTopic, email, payload);

                log.info("Verification code event sent for user: {}", username);
                return true;
            } catch (Exception e) {
                log.error("Failed to send verification code event: {}", e.getMessage());
                return false;
            }
        });
    }

    @Override
    public Mono<Boolean> sendPasswordResetCode(String email, String username, String code) {
        return Mono.fromCallable(() -> {
            try {
                Map<String, String> event = Map.of(
                        "email", email,
                        "username", username,
                        "code", code
                );

                String payload = objectMapper.writeValueAsString(event);
                kafkaTemplate.send(passwordResetTopic, email, payload);

                log.info("Password reset code event sent for user: {}", username);
                return true;
            } catch (Exception e) {
                log.error("Failed to send password reset code event: {}", e.getMessage());
                return false;
            }
        });
    }

    // New method to send welcome email
    public Mono<Boolean> sendWelcomeEmail(String email, String username) {
        return Mono.fromCallable(() -> {
            try {
                Map<String, String> event = Map.of(
                        "email", email,
                        "username", username
                );

                String payload = objectMapper.writeValueAsString(event);
                kafkaTemplate.send(welcomeTopic, email, payload);

                log.info("Welcome email event sent for user: {}", username);
                return true;
            } catch (Exception e) {
                log.error("Failed to send welcome email event: {}", e.getMessage());
                return false;
            }
        });
    }
}
