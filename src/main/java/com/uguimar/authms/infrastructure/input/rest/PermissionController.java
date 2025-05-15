package com.uguimar.authms.infrastructure.input.rest;

import com.uguimar.authms.application.port.input.PermissionManagementUseCase;
import com.uguimar.authms.domain.model.Permission;
import com.uguimar.authms.infrastructure.input.rest.dto.PermissionDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/admin/permissions")
@RequiredArgsConstructor
public class PermissionController {

    private final PermissionManagementUseCase permissionManagementUseCase;

    @GetMapping
    public Flux<PermissionDto> getAllPermissions() {
        return permissionManagementUseCase.findAllPermissions()
                .map(this::mapToDto);
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<PermissionDto>> getPermissionById(@PathVariable String id) {
        return permissionManagementUseCase.findPermissionById(id)
                .map(this::mapToDto)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @GetMapping("/role/{roleId}")
    public Flux<PermissionDto> getPermissionsByRoleId(@PathVariable String roleId) {
        return permissionManagementUseCase.findPermissionsByRoleId(roleId)
                .map(this::mapToDto);
    }

    @GetMapping("/module/{moduleId}")
    public Flux<PermissionDto> getPermissionsByModuleId(@PathVariable String moduleId) {
        return permissionManagementUseCase.findPermissionsByModuleId(moduleId)
                .map(this::mapToDto);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<PermissionDto> createPermission(@Valid @RequestBody PermissionDto permissionDto) {
        Permission permission = mapToEntity(permissionDto);
        return permissionManagementUseCase.createPermission(permission)
                .map(this::mapToDto);
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<PermissionDto>> updatePermission(@PathVariable String id, @Valid @RequestBody PermissionDto permissionDto) {
        Permission permission = mapToEntity(permissionDto);
        return permissionManagementUseCase.updatePermission(id, permission)
                .map(this::mapToDto)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deletePermission(@PathVariable String id) {
        return permissionManagementUseCase.deletePermission(id)
                .then(Mono.just(ResponseEntity.noContent().<Void>build()))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    private Permission mapToEntity(PermissionDto dto) {
        return Permission.builder()
                .id(dto.getId())
                .name(dto.getName())
                .description(dto.getDescription())
                .build();
    }

    private PermissionDto mapToDto(Permission permission) {
        return PermissionDto.builder()
                .id(permission.getId())
                .name(permission.getName())
                .description(permission.getDescription())
                .build();
    }
}