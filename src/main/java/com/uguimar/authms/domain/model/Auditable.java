package com.uguimar.authms.domain.model;

import lombok.Data;

import java.time.Instant;

@Data
public abstract class Auditable {
    private String createdBy;
    private Instant createdDate;
    private String lastModifiedBy;
    private Instant lastModifiedDate;
}
