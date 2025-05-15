package com.uguimar.authms.infrastructure.output.persistence;

import com.uguimar.authms.application.port.output.UserRepository;
import com.uguimar.authms.domain.model.Permission;
import com.uguimar.authms.domain.model.Role;
import com.uguimar.authms.domain.model.User;
import com.uguimar.authms.infrastructure.output.persistence.entity.PermissionEntity;
import com.uguimar.authms.infrastructure.output.persistence.entity.RoleEntity;
import com.uguimar.authms.infrastructure.output.persistence.entity.UserEntity;
import com.uguimar.authms.infrastructure.output.persistence.repository.R2dbcUserCrudRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class R2dbcUserRepository implements UserRepository {

    private final R2dbcUserCrudRepository userRepository;
    private final DatabaseClient databaseClient;

    @Override
    public Mono<User> findById(String id) {
        return userRepository.findById(id)
                .flatMap(this::enrichUserWithRoles)
                .map(this::mapToDomain);
    }

    @Override
    public Mono<User> findByUsername(String username) {
        return userRepository.findByUsername(username)
                .flatMap(this::enrichUserWithRoles)
                .map(this::mapToDomain);
    }

    @Override
    public Mono<User> findByEmail(String email) {
        return userRepository.findByEmail(email)
                .flatMap(this::enrichUserWithRoles)
                .map(this::mapToDomain);
    }

    @Override
    @Transactional
    public Mono<User> save(User user) {
        if (user.getId() == null) {
            user.setId(UUID.randomUUID().toString());
        }

        UserEntity userEntity = mapToEntity(user);
        userEntity.markNew();

        return userRepository.save(userEntity)
                .flatMap(savedUser -> {
                    if (user.getRoles() != null && !user.getRoles().isEmpty()) {
                        return saveUserRoles(savedUser.getId(), user.getRoles())
                                .thenReturn(savedUser);
                    }
                    return Mono.just(savedUser);
                })
                .flatMap(this::enrichUserWithRoles)
                .map(this::mapToDomain);
    }

    @Override
    public Mono<Boolean> existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    @Override
    public Mono<Boolean> existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    private Mono<UserEntity> enrichUserWithRoles(UserEntity user) {
        return Flux.from(databaseClient.sql(
                                "SELECT r.id, r.name FROM roles r " +
                                        "JOIN user_roles ur ON r.id = ur.role_id " +
                                        "WHERE ur.user_id = :userId")
                        .bind("userId", user.getId())
                        .map((row, metadata) -> {
                            RoleEntity role = new RoleEntity();
                            role.setId(row.get("id", String.class));
                            role.setName(row.get("name", String.class));
                            return role;
                        })
                        .all())
                .collectList()
                .map(roles -> {
                    user.setRoles(new HashSet<>(roles));
                    return user;
                })
                .defaultIfEmpty(user);
    }

    private Mono<Void> saveUserRoles(String userId, Set<Role> roles) {
        return Flux.fromIterable(roles)
                .flatMap(role -> {
                    // Primero verificar si el rol existe, si no, crearlo
                    return databaseClient.sql("SELECT id FROM roles WHERE name = :name")
                            .bind("name", role.getName())
                            .map((row, metadata) -> row.get("id", String.class))
                            .one()
                            .switchIfEmpty(Mono.defer(() -> {
                                String roleId = UUID.randomUUID().toString();
                                return databaseClient.sql("INSERT INTO roles (id, name) VALUES (:id, :name)")
                                        .bind("id", roleId)
                                        .bind("name", role.getName())
                                        .fetch()
                                        .rowsUpdated()
                                        .thenReturn(roleId);
                            }))
                            .flatMap(roleId ->
                                    databaseClient.sql("INSERT INTO user_roles (user_id, role_id) VALUES (:userId, :roleId)")
                                            .bind("userId", userId)
                                            .bind("roleId", roleId)
                                            .fetch()
                                            .rowsUpdated()
                            );
                })
                .then();
    }

    private User mapToDomain(UserEntity entity) {
        Set<Role> roles = entity.getRoles() != null
                ? entity.getRoles().stream()
                .map(this::mapRoleToDomain)
                .collect(Collectors.toSet())
                : new HashSet<>();

        User user = User.builder()
                .id(entity.getId())
                .username(entity.getUsername())
                .email(entity.getEmail())
                .password(entity.getPassword())
                .firstName(entity.getFirstName())
                .lastName(entity.getLastName())
                .birthDate(entity.getBirthDate())
                .roles(roles)
                .verified(entity.isVerified())
                .enabled(entity.isEnabled())
                .build();

        // Set audit fields
        user.setCreatedBy(entity.getCreatedBy());
        user.setCreatedDate(entity.getCreatedDate());
        user.setLastModifiedBy(entity.getLastModifiedBy());
        user.setLastModifiedDate(entity.getLastModifiedDate());

        return user;
    }

    private Role mapRoleToDomain(RoleEntity entity) {
        Set<Permission> permissions = entity.getPermissions() != null
                ? entity.getPermissions().stream()
                .map(this::mapPermissionToDomain)
                .collect(Collectors.toSet())
                : new HashSet<>();

        Role role = Role.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .permissions(permissions)
                .build();

        // Set audit fields
        role.setCreatedBy(entity.getCreatedBy());
        role.setCreatedDate(entity.getCreatedDate());
        role.setLastModifiedBy(entity.getLastModifiedBy());
        role.setLastModifiedDate(entity.getLastModifiedDate());

        return role;
    }

    private Permission mapPermissionToDomain(PermissionEntity entity) {
        Permission permission = Permission.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .build();

        // Set audit fields
        permission.setCreatedBy(entity.getCreatedBy());
        permission.setCreatedDate(entity.getCreatedDate());
        permission.setLastModifiedBy(entity.getLastModifiedBy());
        permission.setLastModifiedDate(entity.getLastModifiedDate());

        return permission;
    }

    private UserEntity mapToEntity(User domain) {
        UserEntity entity = UserEntity.builder()
                .id(domain.getId())
                .username(domain.getUsername())
                .email(domain.getEmail())
                .password(domain.getPassword())
                .firstName(domain.getFirstName())
                .lastName(domain.getLastName())
                .birthDate(domain.getBirthDate())
                .enabled(domain.isEnabled())
                .build();

        // Set audit fields from domain if available (for updates)
        entity.setCreatedBy(domain.getCreatedBy());
        entity.setCreatedDate(domain.getCreatedDate());
        entity.setLastModifiedBy(domain.getLastModifiedBy());
        entity.setLastModifiedDate(domain.getLastModifiedDate());

        return entity;
    }
}
