package com.uguimar.authms.infrastructure.output.persistence.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("tokens")
public class TokenEntity implements Persistable<String> {

    @Id
    private String id;

    @Column("user_id")
    private String userId;

    @Column("value")
    private String value;

    @Column("expiry_date")
    private Instant expiryDate;

    @Column("token_type")
    private String tokenType;

    @Transient
    private boolean isNew;

    @Override
    public boolean isNew() {
        return isNew || id == null;
    }

    public void markNew() {
        this.isNew = true;
    }

    public static TokenEntity newToken() {
        TokenEntity token = new TokenEntity();
        token.setId(UUID.randomUUID().toString());
        token.markNew();
        return token;
    }
}