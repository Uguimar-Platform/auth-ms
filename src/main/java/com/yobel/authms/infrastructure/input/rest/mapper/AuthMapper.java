package com.yobel.authms.infrastructure.input.rest.mapper;

import com.yobel.authms.domain.model.Token;
import com.yobel.authms.domain.model.User;
import com.yobel.authms.infrastructure.input.rest.dto.LoginResponse;
import com.yobel.authms.infrastructure.input.rest.dto.RegisterRequest;
import com.yobel.authms.infrastructure.input.rest.dto.RegisterResponse;
import org.springframework.stereotype.Component;

@Component
public class AuthMapper {

    public User mapToUser(RegisterRequest registerRequest) {
        return User.builder()
                .username(registerRequest.getUsername())
                .email(registerRequest.getEmail())
                .password(registerRequest.getPassword())
                .build();
    }

    public RegisterResponse mapToRegisterResponse(User user) {
        return RegisterResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .message("Usuario registrado exitosamente")
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
