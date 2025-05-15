package com.uguimar.authms.infrastructure.input.rest.mapper;

import com.uguimar.authms.domain.model.Token;
import com.uguimar.authms.domain.model.User;
import com.uguimar.authms.infrastructure.input.rest.dto.LoginResponse;
import com.uguimar.authms.infrastructure.input.rest.dto.RegisterRequest;
import com.uguimar.authms.infrastructure.input.rest.dto.RegisterResponse;
import org.springframework.stereotype.Component;

@Component
public class AuthMapper {

    public User mapToUser(RegisterRequest registerRequest) {
        return User.builder()
                .username(registerRequest.getUsername())
                .email(registerRequest.getEmail())
                .password(registerRequest.getPassword())
                .firstName(registerRequest.getFirstName())
                .lastName(registerRequest.getLastName())
                .birthDate(registerRequest.getBirthDate())
                .build();
    }

    public RegisterResponse mapToRegisterResponse(User user) {
        return RegisterResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .birthDate(user.getBirthDate())
                .message("Usuario registrado exitosamente")
                .verified(user.isVerified())
                .verificationMessage("Se ha enviado un código de verificación a su correo electrónico")
                .createdBy(user.getCreatedBy())
                .createdDate(user.getCreatedDate())
                .build();
    }

    public LoginResponse mapToLoginResponse(Token accessToken, Token refreshToken) {
        return LoginResponse.builder()
                .accessToken(accessToken.getValue())
                .refreshToken(refreshToken != null ? refreshToken.getValue() : null)
                .expiryDate(accessToken.getExpiryDate())
                .build();
    }
}
