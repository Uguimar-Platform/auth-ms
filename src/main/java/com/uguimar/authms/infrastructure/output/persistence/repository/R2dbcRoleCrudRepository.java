package com.uguimar.authms.infrastructure.output.persistence.repository;

import com.uguimar.authms.infrastructure.output.persistence.entity.RoleEntity;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface R2dbcRoleCrudRepository extends ReactiveCrudRepository<RoleEntity, String> {
    Mono<RoleEntity> findByName(String name);

    @Modifying
    @Query("INSERT INTO role_permissions (role_id, permission_id) VALUES (:roleId, :permissionId) ON CONFLICT DO NOTHING")
    Mono<Integer> addPermission(String roleId, String permissionId);

    @Modifying
    @Query("DELETE FROM role_permissions WHERE role_id = :roleId AND permission_id = :permissionId")
    Mono<Integer> removePermission(String roleId, String permissionId);

    @Query("SELECT EXISTS(SELECT 1 FROM role_permissions WHERE role_id = :roleId AND permission_id = :permissionId)")
    Mono<Boolean> hasPermission(String roleId, String permissionId);
}
