package com.uguimar.authms.application.port.input;

import com.uguimar.authms.domain.model.Module;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ModuleManagementUseCase {
    Mono<Module> createModule(Module module);

    Mono<Module> updateModule(String id, Module module);

    Mono<Void> deleteModule(String id);

    Mono<Module> findModuleById(String id);

    Mono<Module> findModuleByName(String name);

    Flux<Module> findAllModules();
}