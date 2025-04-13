package com.yobel.authms.infrastructure.output.persistence.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("roles")
public class RoleEntity implements Persistable<String> {

    @Id
    private String id;

    @Column("name")
    private String name;

    @Transient
    private boolean isNew;

    @Override
    public boolean isNew() {
        return isNew || id == null;
    }

    public void markNew() {
        this.isNew = true;
    }

    public static RoleEntity newRole(String name) {
        RoleEntity role = new RoleEntity();
        role.setId(UUID.randomUUID().toString());
        role.setName(name);
        role.markNew();
        return role;
    }
}
