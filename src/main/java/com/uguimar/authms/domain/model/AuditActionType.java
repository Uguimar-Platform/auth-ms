package com.uguimar.authms.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor
@Getter
@ToString
public enum AuditActionType {
    SYSTEM("SYSTEM"),
    SELF_REGISTRATION("SELF-REGISTRATION"),
    ADMIN("ADMIN"),
    USER("USER");

    private final String value;
}
