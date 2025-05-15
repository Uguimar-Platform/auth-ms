package com.uguimar.authms.application.port.input;

import com.uguimar.authms.domain.model.Permission;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface PermissionManagementUseCase {
    Mono<Permission> createPermission(Permission permission);

    Mono<Permission> updatePermission(String id, Permission permission);

    Mono<Void> deletePermission(String id);

    Mono<Permission> findPermissionById(String id);

    Mono<Permission> findPermissionByName(String name);

    Flux<Permission> findAllPermissions();

    Flux<Permission> findPermissionsByRoleId(String roleId);

    Flux<Permission> findPermissionsByModuleId(String moduleId);
}