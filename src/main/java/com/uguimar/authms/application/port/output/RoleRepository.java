package com.uguimar.authms.application.port.output;

import com.uguimar.authms.domain.model.Role;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface RoleRepository {
    Mono<Role> findById(String id);

    Mono<Role> findByName(String name);

    Flux<Role> findAll();

    Mono<Role> save(Role role);

    Mono<Void> deleteById(String id);

    Mono<Boolean> addPermissionToRole(String roleId, String permissionId);

    Mono<Boolean> removePermissionFromRole(String roleId, String permissionId);
}
