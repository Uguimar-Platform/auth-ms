package com.uguimar.authms.application.port.output;

import com.uguimar.authms.domain.model.Permission;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface PermissionRepository {
    Mono<Permission> findById(String id);

    Mono<Permission> findByName(String name);

    Flux<Permission> findAll();

    Mono<Permission> save(Permission permission);

    Mono<Void> deleteById(String id);

    Flux<Permission> findByRoleId(String roleId);

    Flux<Permission> findByModuleId(String moduleId);
}
