package com.uguimar.authms.application.port.output;

import com.uguimar.authms.domain.model.Module;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ModuleRepository {
    Mono<Module> findById(String id);

    Mono<Module> findByName(String name);

    Flux<Module> findAll();

    Mono<Module> save(Module module);

    Mono<Void> deleteById(String id);
}
