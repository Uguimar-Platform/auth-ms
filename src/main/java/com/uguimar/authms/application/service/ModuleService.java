package com.uguimar.authms.application.service;

import com.uguimar.authms.application.port.output.ModuleRepository;
import com.uguimar.authms.domain.model.Module;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@RequiredArgsConstructor
@Service
public class ModuleService {

    private final ModuleRepository moduleRepository;

    public Flux<Module> getModulesByRole(String role) {
        return moduleRepository.getModuleByRole(role)
                .switchIfEmpty(Mono.error(new RuntimeException("Role '" + role + "' not found")))
                .doOnError(throwable ->
                        System.err.println("Error occurred while fetching modules: " + throwable.getMessage())
                );
    }
}
