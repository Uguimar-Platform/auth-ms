package com.uguimar.authms.infrastructure.output.persistence.repository;

import com.uguimar.authms.infrastructure.output.persistence.entity.PermissionEntity;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface R2dbcPermissionCrudRepository extends ReactiveCrudRepository<PermissionEntity, String> {
    Mono<PermissionEntity> findByName(String name);

    @Query("SELECT p.* FROM permissions p JOIN role_permissions rp ON p.id = rp.permission_id WHERE rp.role_id = :roleId")
    Flux<PermissionEntity> findByRoleId(String roleId);

    @Query("SELECT p.* FROM permissions p JOIN module_permissions mp ON p.id = mp.permission_id WHERE mp.module_id = :moduleId")
    Flux<PermissionEntity> findByModuleId(String moduleId);
}
