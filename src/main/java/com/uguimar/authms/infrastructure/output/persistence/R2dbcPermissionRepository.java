package com.uguimar.authms.infrastructure.output.persistence;

import com.uguimar.authms.application.port.output.PermissionRepository;
import com.uguimar.authms.domain.model.Permission;
import com.uguimar.authms.infrastructure.output.persistence.entity.PermissionEntity;
import com.uguimar.authms.infrastructure.output.persistence.repository.R2dbcPermissionCrudRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class R2dbcPermissionRepository implements PermissionRepository {

    private final R2dbcPermissionCrudRepository permissionRepository;

    @Override
    public Mono<Permission> findById(String id) {
        return permissionRepository.findById(id)
                .map(this::mapToDomain);
    }

    @Override
    public Mono<Permission> findByName(String name) {
        return permissionRepository.findByName(name)
                .map(this::mapToDomain);
    }

    @Override
    public Flux<Permission> findAll() {
        return permissionRepository.findAll()
                .map(this::mapToDomain);
    }

    @Override
    public Mono<Permission> save(Permission permission) {
        if (permission.getId() == null) {
            permission.setId(UUID.randomUUID().toString());
        }

        PermissionEntity permissionEntity = mapToEntity(permission);
        permissionEntity.markNew();

        return permissionRepository.save(permissionEntity)
                .map(this::mapToDomain);
    }

    @Override
    public Mono<Void> deleteById(String id) {
        return permissionRepository.deleteById(id);
    }

    @Override
    public Flux<Permission> findByRoleId(String roleId) {
        return permissionRepository.findByRoleId(roleId)
                .map(this::mapToDomain);
    }

    @Override
    public Flux<Permission> findByModuleId(String moduleId) {
        return permissionRepository.findByModuleId(moduleId)
                .map(this::mapToDomain);
    }

    private Permission mapToDomain(PermissionEntity entity) {
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

    private PermissionEntity mapToEntity(Permission domain) {
        PermissionEntity entity = PermissionEntity.builder()
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