package com.uguimar.authms.application.service;

import com.uguimar.authms.application.port.input.RoleManagementUseCase;
import com.uguimar.authms.application.port.output.RoleRepository;
import com.uguimar.authms.domain.exception.ResourceNotFoundException;
import com.uguimar.authms.domain.model.AuditActionType;
import com.uguimar.authms.domain.model.Role;
import com.uguimar.authms.infrastructure.config.AuditingConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class RoleManagementService implements RoleManagementUseCase {

    private final RoleRepository roleRepository;

    @Override
    public Mono<Role> createRole(Role role) {
        AuditingConfig.setAuditor(AuditActionType.SYSTEM.getValue());
        return roleRepository.save(role)
                .doFinally(signal -> AuditingConfig.clearAuditor());
    }

    @Override
    public Mono<Role> updateRole(String id, Role role) {
        return roleRepository.findById(id)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Role not found with id: " + id)))
                .flatMap(existingRole -> {
                    role.setId(id);
                    AuditingConfig.setAuditor(AuditActionType.SYSTEM.getValue());
                    return roleRepository.save(role)
                            .doFinally(signal -> AuditingConfig.clearAuditor());
                });
    }

    @Override
    public Mono<Void> deleteRole(String id) {
        return roleRepository.findById(id)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Role not found with id: " + id)))
                .flatMap(role -> roleRepository.deleteById(id));
    }

    @Override
    public Mono<Role> findRoleById(String id) {
        return roleRepository.findById(id)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Role not found with id: " + id)));
    }

    @Override
    public Mono<Role> findRoleByName(String name) {
        return roleRepository.findByName(name)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Role not found with name: " + name)));
    }

    @Override
    public Flux<Role> findAllRoles() {
        return roleRepository.findAll();
    }

    @Override
    public Mono<Boolean> addPermissionToRole(String roleId, String permissionId) {
        return roleRepository.addPermissionToRole(roleId, permissionId);
    }

    @Override
    public Mono<Boolean> removePermissionFromRole(String roleId, String permissionId) {
        return roleRepository.removePermissionFromRole(roleId, permissionId);
    }
}