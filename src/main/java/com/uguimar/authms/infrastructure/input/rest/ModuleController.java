package com.uguimar.authms.infrastructure.input.rest;

import com.uguimar.authms.application.port.input.ModuleManagementUseCase;
import com.uguimar.authms.application.port.input.PermissionManagementUseCase;
import com.uguimar.authms.domain.model.Module;
import com.uguimar.authms.domain.model.Permission;
import com.uguimar.authms.infrastructure.input.rest.dto.ModuleDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/modules")
@RequiredArgsConstructor
public class ModuleController {

    private final ModuleManagementUseCase moduleManagementUseCase;
    private final PermissionManagementUseCase permissionManagementUseCase;

    @GetMapping
    public Flux<ModuleDto> getAllModules() {
        return moduleManagementUseCase.findAllModules()
                .map(this::mapToDto);
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<ModuleDto>> getModuleById(@PathVariable String id) {
        return moduleManagementUseCase.findModuleById(id)
                .map(this::mapToDto)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<ModuleDto> createModule(@Valid @RequestBody ModuleDto moduleDto) {
        return createOrUpdateModule(moduleDto, null);
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<ModuleDto>> updateModule(@PathVariable String id, @Valid @RequestBody ModuleDto moduleDto) {
        return createOrUpdateModule(moduleDto, id)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteModule(@PathVariable String id) {
        return moduleManagementUseCase.deleteModule(id)
                .then(Mono.just(ResponseEntity.noContent().<Void>build()))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    private Mono<ModuleDto> createOrUpdateModule(ModuleDto moduleDto, String id) {
        return processPermissions(moduleDto.getPermissionIds())
                .flatMap(permissions -> {
                    Module module = mapToEntity(moduleDto, permissions);
                    if (id != null) {
                        module.setId(id);
                        return moduleManagementUseCase.updateModule(id, module);
                    } else {
                        return moduleManagementUseCase.createModule(module);
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

    private Module mapToEntity(ModuleDto dto, Set<Permission> permissions) {
        return Module.builder()
                .id(dto.getId())
                .name(dto.getName())
                .description(dto.getDescription())
                .permissions(permissions)
                .build();
    }

    private ModuleDto mapToDto(Module module) {
        Set<String> permissionIds = module.getPermissions() != null
                ? module.getPermissions().stream().map(Permission::getId).collect(Collectors.toSet())
                : new HashSet<>();

        return ModuleDto.builder()
                .id(module.getId())
                .name(module.getName())
                .description(module.getDescription())
                .permissionIds(permissionIds)
                .build();
    }
}