package com.uguimar.authms.infrastructure.output.persistence;

import com.uguimar.authms.application.port.output.ModuleRepository;
import com.uguimar.authms.domain.model.Module;
import com.uguimar.authms.infrastructure.output.persistence.repository.R2dbcModuleCrudRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@RequiredArgsConstructor
@Repository
public class R2dbcModuleRepository implements ModuleRepository {

    private final R2dbcModuleCrudRepository moduleRepository;


    @Override
    public Flux<Module> getModuleByRole(String nameRole) {
        return moduleRepository.getModulesByRole(nameRole);
    }

}
