package com.uguimar.authms.application.port.output;


import com.uguimar.authms.domain.model.Module;
import reactor.core.publisher.Flux;

public interface ModuleRepository {

    Flux<Module> getModuleByRole(String roleName);

}
