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

import java.util.Set;
import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Table("roles")
public class RoleEntity extends AuditableEntity implements Persistable<String> {

    @Id
    private String id;

    @Column("name")
    private String name;

    @Column("description")
    private String description;

    @Transient
    private Set<PermissionEntity> permissions;

    @Transient
    private boolean isNew;

    @Override
    public boolean isNew() {
        return isNew || id == null;
    }

    public void markNew() {
        this.isNew = true;
    }

    public static RoleEntity newRole() {
        RoleEntity role = new RoleEntity();
        role.setId(UUID.randomUUID().toString());
        role.markNew();
        return role;
    }
}
