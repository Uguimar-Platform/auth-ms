package com.uguimar.authms.application.service;

import com.uguimar.authms.application.port.input.ModuleManagementUseCase;
import com.uguimar.authms.application.port.output.ModuleRepository;
import com.uguimar.authms.domain.exception.ResourceNotFoundException;
import com.uguimar.authms.domain.model.AuditActionType;
import com.uguimar.authms.domain.model.Module;
import com.uguimar.authms.infrastructure.config.AuditingConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class ModuleManagementService implements ModuleManagementUseCase {

    private final ModuleRepository moduleRepository;

    @Override
    public Mono<Module> createModule(Module module) {
        AuditingConfig.setAuditor(AuditActionType.SYSTEM.getValue());
        return moduleRepository.save(module)
                .doFinally(signal -> AuditingConfig.clearAuditor());
    }

    @Override
    public Mono<Module> updateModule(String id, Module module) {
        return moduleRepository.findById(id)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Module not found with id: " + id)))
                .flatMap(existingModule -> {
                    module.setId(id);
                    AuditingConfig.setAuditor(AuditActionType.SYSTEM.getValue());
                    return moduleRepository.save(module)
                            .doFinally(signal -> AuditingConfig.clearAuditor());
                });
    }

    @Override
    public Mono<Void> deleteModule(String id) {
        return moduleRepository.findById(id)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Module not found with id: " + id)))
                .flatMap(module -> moduleRepository.deleteById(id));
    }

    @Override
    public Mono<Module> findModuleById(String id) {
        return moduleRepository.findById(id)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Module not found with id: " + id)));
    }

    @Override
    public Mono<Module> findModuleByName(String name) {
        return moduleRepository.findByName(name)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Module not found with name: " + name)));
    }

    @Override
    public Flux<Module> findAllModules() {
        return moduleRepository.findAll();
    }
}