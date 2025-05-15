package com.uguimar.authms.application.port.input;

import com.uguimar.authms.domain.model.Role;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface RoleManagementUseCase {
    Mono<Role> createRole(Role role);

    Mono<Role> updateRole(String id, Role role);

    Mono<Void> deleteRole(String id);

    Mono<Role> findRoleById(String id);

    Mono<Role> findRoleByName(String name);

    Flux<Role> findAllRoles();

    Mono<Boolean> addPermissionToRole(String roleId, String permissionId);

    Mono<Boolean> removePermissionFromRole(String roleId, String permissionId);
}
