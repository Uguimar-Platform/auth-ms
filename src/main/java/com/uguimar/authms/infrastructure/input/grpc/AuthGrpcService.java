package com.uguimar.authms.infrastructure.input.grpc;

import com.uguimar.authms.application.port.input.TokenValidationUseCase;
import com.uguimar.authms.application.port.output.UserRepository;
import com.uguimar.authms.domain.model.Role;
import com.uguimar.authms.domain.model.User;
import com.uguimar.authms.grpc.AuthServiceGrpc;
import com.uguimar.authms.grpc.UserDetails;
import com.uguimar.authms.grpc.UserDetailsRequest;
import com.uguimar.authms.grpc.UserDetailsResponse;
import com.uguimar.authms.grpc.ValidateTokenRequest;
import com.uguimar.authms.grpc.ValidateTokenResponse;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.grpc.server.service.GrpcService;

import java.util.stream.Collectors;

@GrpcService
@RequiredArgsConstructor
@Log4j2
public class AuthGrpcService extends AuthServiceGrpc.AuthServiceImplBase {

    private final TokenValidationUseCase tokenValidationUseCase;
    private final UserRepository userRepository;

    @Override
    public void validateToken(ValidateTokenRequest request, StreamObserver<ValidateTokenResponse> responseObserver) {
        log.debug("Validando token mediante gRPC");
        String token = request.getToken();

        tokenValidationUseCase.validateToken(token)
                .map(this::mapToUserDetails)
                .map(userDetails -> ValidateTokenResponse
                        .newBuilder()
                        .setValid(true)
                        .setUserDetails(userDetails)
                        .build()
                )
                .defaultIfEmpty(ValidateTokenResponse.newBuilder()
                        .setValid(false)
                        .build()
                )
                .onErrorReturn(ValidateTokenResponse.newBuilder()
                        .setValid(false)
                        .build()
                )
                .subscribe(
                        response -> {
                            responseObserver.onNext(response);
                            responseObserver.onCompleted();
                        },
                        error -> {
                            log.error("Error al validar token via gRPC: {}", error.getMessage());
                            responseObserver.onNext(ValidateTokenResponse.newBuilder()
                                    .setValid(false)
                                    .build());
                            responseObserver.onCompleted();
                        }
                );
    }

    @Override
    public void getUserDetails(UserDetailsRequest request, StreamObserver<UserDetailsResponse> responseObserver) {
        log.debug("Obteniendo detalles de usuario mediante gRPC para id: {}", request.getUserId());
        String userId = request.getUserId();

        userRepository.findById(userId)
                .map(this::mapToUserDetails)
                .map(userDetails -> UserDetailsResponse.newBuilder()
                        .setUserDetails(userDetails)
                        .build()
                )
                .subscribe(
                        response -> {
                            responseObserver.onNext(response);
                            responseObserver.onCompleted();
                        },
                        error -> {
                            log.error("Error al obtener detalles de usuario via gRPC: {}", error.getMessage());
                            responseObserver.onError(error);
                        },
                        () -> {
                            log.debug("Usuario no encontrado");
                            responseObserver.onCompleted();
                        }
                );
    }

    private UserDetails mapToUserDetails(User user) {
        return UserDetails.newBuilder()
                .setId(user.getId())
                .setUsername(user.getUsername())
                .setEmail(user.getEmail())
                .addAllRoles(user.getRoles().stream()
                        .map(Role::getName)
                        .collect(Collectors.toList()))
                .build();
    }
}