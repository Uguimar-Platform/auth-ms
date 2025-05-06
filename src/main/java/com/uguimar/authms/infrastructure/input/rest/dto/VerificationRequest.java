package com.uguimar.authms.infrastructure.input.rest.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VerificationRequest {
    @NotBlank(message = "El ID de usuario es obligatorio")
    private String userId;

    @NotBlank(message = "El código de verificación es obligatorio")
    private String code;
}
