package com.yobel.authms.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Token {
    private String id;
    private String userId;
    private String value;
    private Instant expiryDate;
    private TokenType type;

    public boolean isExpired() {
        return expiryDate != null && expiryDate.isBefore(Instant.now());
    }
}
