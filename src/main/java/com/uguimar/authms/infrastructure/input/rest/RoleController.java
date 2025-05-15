package com.uguimar.authms.infrastructure.input.rest;

import com.uguimar.authms.application.port.input.PermissionManagementUseCase;
import com.uguimar.authms.application.port.input.RoleManagementUseCase;
import com.uguimar.authms.domain.model.Permission;
import com.uguimar.authms.domain.model.Role;
import com.uguimar.authms.infrastructure.input.rest.dto.RoleDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/roles")
@RequiredArgsConstructor
@Log4j2
public class RoleController {

    private final RoleManagementUseCase roleManagementUseCase;
    private final PermissionManagementUseCase permissionManagementUseCase;

    @GetMapping
    public Flux<RoleDto> getAllRoles() {
        return roleManagementUseCase.findAllRoles()
                .map(this::mapToDto);
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<RoleDto>> getRoleById(@PathVariable String id) {
        return roleManagementUseCase.findRoleById(id)
                .map(this::mapToDto)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<RoleDto> createRole(@Valid @RequestBody RoleDto roleDto) {
        return createOrUpdateRole(roleDto, null);
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<RoleDto>> updateRole(@PathVariable String id, @Valid @RequestBody RoleDto roleDto) {
        return createOrUpdateRole(roleDto, id)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteRole(@PathVariable String id) {
        return roleManagementUseCase.deleteRole(id)
                .then(Mono.just(ResponseEntity.noContent().<Void>build()))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PostMapping("/{roleId}/permissions/{permissionId}")
    public Mono<ResponseEntity<Void>> addPermissionToRole(@PathVariable String roleId, @PathVariable String permissionId) {
        return roleManagementUseCase.addPermissionToRole(roleId, permissionId)
                .map(added -> added ? ResponseEntity.ok().<Void>build() : ResponseEntity.notFound().<Void>build());
    }

    @DeleteMapping("/{roleId}/permissions/{permissionId}")
    public Mono<ResponseEntity<Void>> removePermissionFromRole(@PathVariable String roleId, @PathVariable String permissionId) {
        return roleManagementUseCase.removePermissionFromRole(roleId, permissionId)
                .map(removed -> removed ? ResponseEntity.ok().<Void>build() : ResponseEntity.notFound().<Void>build());
    }

    private Mono<RoleDto> createOrUpdateRole(RoleDto roleDto, String id) {
        return processPermissions(roleDto.getPermissionIds())
                .flatMap(permissions -> {
                    Role role = mapToEntity(roleDto, permissions);
                    if (id != null) {
                        role.setId(id);
                        return roleManagementUseCase.updateRole(id, role);
                    } else {
                        return roleManagementUseCase.createRole(role);
                    }
                })
                .map(this::mapToDto);
    }

    private Mono<Set<Permission>> processPermissions(Set<String> permissionIds) {
        if (permissionIds == null || permissionIds.isEmpty()) {
            return Mono.just(new HashSet<>());
        }

        return Flux.fromIterable(permissionIds)
                .flatMap(permissionManagementUseCase::findPermissionById)
                .collect(Collectors.toSet());
    }

    private Role mapToEntity(RoleDto dto, Set<Permission> permissions) {
        return Role.builder()
                .id(dto.getId())
                .name(dto.getName())
                .description(dto.getDescription())
                .permissions(permissions)
                .build();
    }

    private RoleDto mapToDto(Role role) {
        Set<String> permissionIds = role.getPermissions() != null
                ? role.getPermissions().stream().map(Permission::getId).collect(Collectors.toSet())
                : new HashSet<>();

        return RoleDto.builder()
                .id(role.getId())
                .name(role.getName())
                .description(role.getDescription())
                .permissionIds(permissionIds)
                .build();
    }
}