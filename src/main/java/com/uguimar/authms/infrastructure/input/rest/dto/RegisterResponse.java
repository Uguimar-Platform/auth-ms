package com.uguimar.authms.infrastructure.input.rest.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterResponse {
    private String id;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private LocalDate birthDate;
    private String message;
    private boolean verified;
    private String verificationMessage;
    // Audit fields
    private String createdBy;
    private Instant createdDate;
}
