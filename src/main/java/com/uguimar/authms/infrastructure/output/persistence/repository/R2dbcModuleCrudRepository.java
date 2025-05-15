package com.uguimar.authms.infrastructure.output.persistence.repository;

import com.uguimar.authms.infrastructure.output.persistence.entity.ModuleEntity;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface R2dbcModuleCrudRepository extends ReactiveCrudRepository<ModuleEntity, String> {
    Mono<ModuleEntity> findByName(String name);

    @Query("SELECT EXISTS(SELECT 1 FROM module_permissions WHERE module_id = :moduleId AND permission_id = :permissionId)")
    Mono<Boolean> hasPermission(String moduleId, String permissionId);
}
