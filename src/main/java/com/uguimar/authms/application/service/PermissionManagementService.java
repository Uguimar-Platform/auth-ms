package com.uguimar.authms.application.service;

import com.uguimar.authms.application.port.input.PermissionManagementUseCase;
import com.uguimar.authms.application.port.output.PermissionRepository;
import com.uguimar.authms.domain.exception.ResourceNotFoundException;
import com.uguimar.authms.domain.model.AuditActionType;
import com.uguimar.authms.domain.model.Permission;
import com.uguimar.authms.infrastructure.config.AuditingConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class PermissionManagementService implements PermissionManagementUseCase {

    private final PermissionRepository permissionRepository;

    @Override
    public Mono<Permission> createPermission(Permission permission) {
        AuditingConfig.setAuditor(AuditActionType.SYSTEM.getValue());
        return permissionRepository.save(permission)
                .doFinally(signal -> AuditingConfig.clearAuditor());
    }

    @Override
    public Mono<Permission> updatePermission(String id, Permission permission) {
        return permissionRepository.findById(id)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Permission not found with id: " + id)))
                .flatMap(existingPermission -> {
                    permission.setId(id);
                    AuditingConfig.setAuditor(AuditActionType.SYSTEM.getValue());
                    return permissionRepository.save(permission)
                            .doFinally(signal -> AuditingConfig.clearAuditor());
                });
    }

    @Override
    public Mono<Void> deletePermission(String id) {
        return permissionRepository.findById(id)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Permission not found with id: " + id)))
                .flatMap(permission -> permissionRepository.deleteById(id));
    }

    @Override
    public Mono<Permission> findPermissionById(String id) {
        return permissionRepository.findById(id)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Permission not found with id: " + id)));
    }

    @Override
    public Mono<Permission> findPermissionByName(String name) {
        return permissionRepository.findByName(name)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Permission not found with name: " + name)));
    }

    @Override
    public Flux<Permission> findAllPermissions() {
        return permissionRepository.findAll();
    }

    @Override
    public Flux<Permission> findPermissionsByRoleId(String roleId) {
        return permissionRepository.findByRoleId(roleId);
    }

    @Override
    public Flux<Permission> findPermissionsByModuleId(String moduleId) {
        return permissionRepository.findByModuleId(moduleId);
    }
}