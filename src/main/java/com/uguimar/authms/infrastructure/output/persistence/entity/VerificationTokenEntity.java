package com.uguimar.authms.infrastructure.output.persistence.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;
import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Table("verification_tokens")
public class VerificationTokenEntity extends AuditableEntity implements Persistable<String> {

    @Id
    private String id;

    @Column("user_id")
    private String userId;

    @Column("token")
    private String token;

    @Column("expiry_date")
    private Instant expiryDate;

    @Column("used")
    private boolean used;

    @Transient
    private boolean isNew;

    @Override
    public boolean isNew() {
        return isNew || id == null;
    }

    public void markNew() {
        this.isNew = true;
    }

    public static VerificationTokenEntity newToken() {
        VerificationTokenEntity token = new VerificationTokenEntity();
        token.setId(UUID.randomUUID().toString());
        token.markNew();
        return token;
    }
}
