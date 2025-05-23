package com.uguimar.authms.domain.model;

import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.HashSet;
import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Role extends Auditable {
    private String id;
    private String name;
    private String description;
    @Builder.Default
    private Set<Permission> permissions = new HashSet<>();
}
