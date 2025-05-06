package com.uguimar.authms.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.Instant;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class VerificationToken extends Auditable {
    private String id;
    private String userId;
    private String token;
    private Instant expiryDate;
    private boolean used;

    public boolean isExpired() {
        return expiryDate != null && expiryDate.isBefore(Instant.now());
    }
}
