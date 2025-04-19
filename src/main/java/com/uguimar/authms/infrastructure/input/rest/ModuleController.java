package com.uguimar.authms.infrastructure.input.rest;
import com.uguimar.authms.application.service.ModuleService;
import com.uguimar.authms.domain.model.Module;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController()
@RequestMapping("/api/modules")
public class ModuleController {

    private final ModuleService moduleService;
    public ModuleController(ModuleService moduleService) {
        this.moduleService = moduleService;
    }

    @GetMapping("/getModules/{role}")
    public Mono<ResponseEntity<List<Module>>> getAllModulesByRole(@PathVariable String role) {
        return moduleService.getModulesByRole(role)
                .collectList()
                .map(ResponseEntity::ok)
                .onErrorResume(RuntimeException.class, e ->
                        Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND).build()));
    }
}
