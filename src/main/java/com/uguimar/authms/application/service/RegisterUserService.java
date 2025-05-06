package com.uguimar.authms.application.service;

import com.uguimar.authms.application.port.input.EmailVerificationUseCase;
import com.uguimar.authms.application.port.input.RegisterUserUseCase;
import com.uguimar.authms.application.port.output.PasswordEncoder;
import com.uguimar.authms.application.port.output.UserRepository;
import com.uguimar.authms.domain.exception.UserAlreadyExistsException;
import com.uguimar.authms.domain.model.AuditActionType;
import com.uguimar.authms.domain.model.Role;
import com.uguimar.authms.domain.model.User;
import com.uguimar.authms.infrastructure.config.AuditingConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class RegisterUserService implements RegisterUserUseCase {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailVerificationUseCase emailVerificationUseCase;

    @Override
    public Mono<User> registerUser(User user) {
        return userRepository.existsByUsername(user.getUsername())
                .filter(exists -> !exists)
                .switchIfEmpty(Mono.error(new UserAlreadyExistsException("El nombre de usuario ya existe")))
                .then(userRepository.existsByEmail(user.getEmail()))
                .filter(exists -> !exists)
                .switchIfEmpty(Mono.error(new UserAlreadyExistsException("El email ya está registrado")))
                .then(Mono.fromCallable(() -> {
                    user.setPassword(passwordEncoder.encode(user.getPassword()));
                    user.setEnabled(true);
                    user.setVerified(false);

                    // Assign default user role if not set
                    if (user.getRoles() == null || user.getRoles().isEmpty()) {
                        Role userRole = Role.builder().name("USER").build();
                        user.setRoles(Set.of(userRole));
                    }

                    return user;
                }))
                .flatMap(userToSave -> {
                    // Set specific auditor for this operation
                    AuditingConfig.setAuditor(AuditActionType.SELF_REGISTRATION.getValue());

                    // Save and clear auditor
                    return userRepository.save(userToSave)
                            .doFinally(signal -> AuditingConfig.clearAuditor());
                })
                .flatMap(savedUser -> emailVerificationUseCase.sendVerificationCode(savedUser.getId())
                        .thenReturn(savedUser));
    }
}
