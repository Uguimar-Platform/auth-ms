package com.uguimar.authms.infrastructure.output.persistence;

import com.uguimar.authms.application.port.output.RoleRepository;
import com.uguimar.authms.domain.model.Permission;
import com.uguimar.authms.domain.model.Role;
import com.uguimar.authms.infrastructure.output.persistence.entity.PermissionEntity;
import com.uguimar.authms.infrastructure.output.persistence.entity.RoleEntity;
import com.uguimar.authms.infrastructure.output.persistence.repository.R2dbcPermissionCrudRepository;
import com.uguimar.authms.infrastructure.output.persistence.repository.R2dbcRoleCrudRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class R2dbcRoleRepository implements RoleRepository {

    private final R2dbcRoleCrudRepository roleCrudRepository;
    private final R2dbcPermissionCrudRepository permissionCrudRepository;
    private final DatabaseClient databaseClient;

    @Override
    public Mono<Role> findById(String id) {
        return roleCrudRepository.findById(id)
                .flatMap(this::enrichRoleWithPermissions)
                .map(this::mapToDomain);
    }

    @Override
    public Mono<Role> findByName(String name) {
        return roleCrudRepository.findByName(name)
                .flatMap(this::enrichRoleWithPermissions)
                .map(this::mapToDomain);
    }

    @Override
    public Flux<Role> findAll() {
        return roleCrudRepository.findAll()
                .flatMap(this::enrichRoleWithPermissions)
                .map(this::mapToDomain);
    }

    @Override
    @Transactional
    public Mono<Role> save(Role role) {
        if (role.getId() == null) {
            role.setId(UUID.randomUUID().toString());
        }

        RoleEntity roleEntity = mapToEntity(role);
        roleEntity.markNew();

        return roleCrudRepository.save(roleEntity)
                .flatMap(savedRole -> {
                    if (role.getPermissions() != null && !role.getPermissions().isEmpty()) {
                        return saveRolePermissions(savedRole.getId(), role.getPermissions())
                                .thenReturn(savedRole);
                    }
                    return Mono.just(savedRole);
                })
                .flatMap(this::enrichRoleWithPermissions)
                .map(this::mapToDomain);
    }

    @Override
    public Mono<Void> deleteById(String id) {
        return roleCrudRepository.deleteById(id);
    }

    @Override
    public Mono<Boolean> addPermissionToRole(String roleId, String permissionId) {
        return roleCrudRepository.addPermission(roleId, permissionId)
                .map(rowsUpdated -> rowsUpdated > 0);
    }

    @Override
    public Mono<Boolean> removePermissionFromRole(String roleId, String permissionId) {
        return roleCrudRepository.removePermission(roleId, permissionId)
                .map(rowsUpdated -> rowsUpdated > 0);
    }

    private Mono<RoleEntity> enrichRoleWithPermissions(RoleEntity role) {
        return permissionCrudRepository.findByRoleId(role.getId())
                .collectList()
                .map(permissions -> {
                    role.setPermissions(new HashSet<>(permissions));
                    return role;
                });
    }

    private Mono<Void> saveRolePermissions(String roleId, Set<Permission> permissions) {
        return Flux.fromIterable(permissions)
                .flatMap(permission -> {
                    return databaseClient.sql("INSERT INTO role_permissions (role_id, permission_id) VALUES (:roleId, :permissionId) ON CONFLICT DO NOTHING")
                            .bind("roleId", roleId)
                            .bind("permissionId", permission.getId())
                            .fetch()
                            .rowsUpdated();
                })
                .then();
    }

    private Role mapToDomain(RoleEntity entity) {
        Set<Permission> permissions = entity.getPermissions() != null
                ? entity.getPermissions().stream()
                .map(this::mapPermissionToDomain)
                .collect(Collectors.toSet())
                : new HashSet<>();

        Role role = Role.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .permissions(permissions)
                .build();

        // Set audit fields
        role.setCreatedBy(entity.getCreatedBy());
        role.setCreatedDate(entity.getCreatedDate());
        role.setLastModifiedBy(entity.getLastModifiedBy());
        role.setLastModifiedDate(entity.getLastModifiedDate());

        return role;
    }

    private Permission mapPermissionToDomain(PermissionEntity entity) {
        Permission permission = Permission.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .build();

        // Set audit fields
        permission.setCreatedBy(entity.getCreatedBy());
        permission.setCreatedDate(entity.getCreatedDate());
        permission.setLastModifiedBy(entity.getLastModifiedBy());
        permission.setLastModifiedDate(entity.getLastModifiedDate());

        return permission;
    }

    private RoleEntity mapToEntity(Role domain) {
        RoleEntity entity = RoleEntity.builder()
                .id(domain.getId())
                .name(domain.getName())
                .description(domain.getDescription())
                .build();

        // Set audit fields from domain if available (for updates)
        entity.setCreatedBy(domain.getCreatedBy());
        entity.setCreatedDate(domain.getCreatedDate());
        entity.setLastModifiedBy(domain.getLastModifiedBy());
        entity.setLastModifiedDate(domain.getLastModifiedDate());

        return entity;
    }
}