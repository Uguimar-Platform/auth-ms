package com.uguimar.authms.infrastructure.output.persistence.repository;
import com.uguimar.authms.domain.model.Module;
import com.uguimar.authms.infrastructure.output.persistence.entity.ModuleEntity;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface R2dbcModuleCrudRepository extends ReactiveCrudRepository<ModuleEntity, String> {

    @Query(
            "SELECT modules.name FROM role_module " +
                    "INNER JOIN roles ON roles.id = role_module.role " +
                    "INNER JOIN modules ON modules.id = role_module.module_id " +
                    "WHERE roles.name = :name_role"
    )
    Flux<Module>getModulesByRole(@Param("name_role") String nameRole);

}
