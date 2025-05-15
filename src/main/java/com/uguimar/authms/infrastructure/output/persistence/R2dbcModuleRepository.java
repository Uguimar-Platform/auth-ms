// src/main/java/com/uguimar/authms/infrastructure/output/persistence/R2dbcModuleRepository.java
package com.uguimar.authms.infrastructure.output.persistence;

import com.uguimar.authms.application.port.output.ModuleRepository;
import com.uguimar.authms.domain.model.Module;
import com.uguimar.authms.domain.model.Permission;
import com.uguimar.authms.infrastructure.output.persistence.entity.ModuleEntity;
import com.uguimar.authms.infrastructure.output.persistence.entity.PermissionEntity;
import com.uguimar.authms.infrastructure.output.persistence.repository.R2dbcModuleCrudRepository;
import com.uguimar.authms.infrastructure.output.persistence.repository.R2dbcPermissionCrudRepository;
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
public class R2dbcModuleRepository implements ModuleRepository {

    private final R2dbcModuleCrudRepository moduleCrudRepository;
    private final R2dbcPermissionCrudRepository permissionCrudRepository;
    private final DatabaseClient databaseClient;

    @Override
    public Mono<Module> findById(String id) {
        return moduleCrudRepository.findById(id)
                .flatMap(this::enrichModuleWithPermissions)
                .map(this::mapToDomain);
    }

    @Override
    public Mono<Module> findByName(String name) {
        return moduleCrudRepository.findByName(name)
                .flatMap(this::enrichModuleWithPermissions)
                .map(this::mapToDomain);
    }

    @Override
    public Flux<Module> findAll() {
        return moduleCrudRepository.findAll()
                .flatMap(this::enrichModuleWithPermissions)
                .map(this::mapToDomain);
    }

    @Override
    @Transactional
    public Mono<Module> save(Module module) {
        if (module.getId() == null) {
            module.setId(UUID.randomUUID().toString());
        }

        ModuleEntity moduleEntity = mapToEntity(module);
        moduleEntity.markNew();

        return moduleCrudRepository.save(moduleEntity)
                .flatMap(savedModule -> {
                    if (module.getPermissions() != null && !module.getPermissions().isEmpty()) {
                        return saveModulePermissions(savedModule.getId(), module.getPermissions())
                                .thenReturn(savedModule);
                    }
                    return Mono.just(savedModule);
                })
                .flatMap(this::enrichModuleWithPermissions)
                .map(this::mapToDomain);
    }

    @Override
    public Mono<Void> deleteById(String id) {
        return moduleCrudRepository.deleteById(id);
    }

    private Mono<ModuleEntity> enrichModuleWithPermissions(ModuleEntity module) {
        return permissionCrudRepository.findByModuleId(module.getId())
                .collectList()
                .map(permissions -> {
                    module.setPermissions(new HashSet<>(permissions));
                    return module;
                });
    }

    private Mono<Void> saveModulePermissions(String moduleId, Set<Permission> permissions) {
        return Flux.fromIterable(permissions)
                .flatMap(permission -> databaseClient.sql("INSERT INTO module_permissions (module_id, permission_id) VALUES (:moduleId, :permissionId) ON CONFLICT DO NOTHING")
                        .bind("moduleId", moduleId)
                        .bind("permissionId", permission.getId())
                        .fetch()
                        .rowsUpdated())
                .then();
    }

    private Module mapToDomain(ModuleEntity entity) {
        Set<Permission> permissions = entity.getPermissions() != null
                ? entity.getPermissions().stream()
                .map(this::mapPermissionToDomain)
                .collect(Collectors.toSet())
                : new HashSet<>();

        Module module = Module.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .permissions(permissions)
                .build();

        // Set audit fields
        module.setCreatedBy(entity.getCreatedBy());
        module.setCreatedDate(entity.getCreatedDate());
        module.setLastModifiedBy(entity.getLastModifiedBy());
        module.setLastModifiedDate(entity.getLastModifiedDate());

        return module;
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

    private ModuleEntity mapToEntity(Module domain) {
        ModuleEntity entity = ModuleEntity.builder()
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