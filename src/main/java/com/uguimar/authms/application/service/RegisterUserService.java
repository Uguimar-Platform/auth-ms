package com.uguimar.authms.application.service;

import com.uguimar.authms.application.port.input.RegisterUserUseCase;
import com.uguimar.authms.application.port.output.PasswordEncoder;
import com.uguimar.authms.application.port.output.UserRepository;
import com.uguimar.authms.domain.exception.UserAlreadyExistsException;
import com.uguimar.authms.domain.model.Role;
import com.uguimar.authms.domain.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class RegisterUserService implements RegisterUserUseCase {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

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

                    // Asignar rol de usuario por defecto si no tiene roles
                    if (user.getRoles() == null || user.getRoles().isEmpty()) {
                        Role userRole = Role.builder().name("USER").build();
                        user.setRoles(Set.of(userRole));
                    }

                    return user;
                }))
                .flatMap(userRepository::save);
    }
}
