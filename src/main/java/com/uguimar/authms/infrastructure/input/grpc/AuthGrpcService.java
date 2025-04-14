package com.uguimar.authms.infrastructure.input.grpc;

import com.uguimar.authms.application.port.input.TokenValidationUseCase;
import com.uguimar.authms.application.port.output.UserRepository;
import com.uguimar.authms.domain.model.Role;
import com.uguimar.authms.domain.model.User;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.grpc.server.service.GrpcService;

import java.util.stream.Collectors;

@GrpcService
@RequiredArgsConstructor
@Log4j2
public class AuthGrpcService extends com.uguimar.authms.grpc.AuthServiceGrpc.AuthServiceImplBase {

    private final TokenValidationUseCase tokenValidationUseCase;
    private final UserRepository userRepository;

    @Override
    public void validateToken(com.uguimar.authms.grpc.ValidateTokenRequest request, StreamObserver<com.uguimar.authms.grpc.ValidateTokenResponse> responseObserver) {
        log.debug("Validando token mediante gRPC");
        String token = request.getToken();

        tokenValidationUseCase.validateToken(token)
                .map(this::mapToUserDetails)
                .map(userDetails -> com.uguimar.authms.grpc.ValidateTokenResponse
                        .newBuilder()
                        .setValid(true)
                        .setUserDetails(userDetails)
                        .build()
                )
                .defaultIfEmpty(com.uguimar.authms.grpc.ValidateTokenResponse.newBuilder()
                        .setValid(false)
                        .build()
                )
                .onErrorReturn(com.uguimar.authms.grpc.ValidateTokenResponse.newBuilder()
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
                            responseObserver.onNext(com.uguimar.authms.grpc.ValidateTokenResponse.newBuilder()
                                    .setValid(false)
                                    .build());
                            responseObserver.onCompleted();
                        }
                );
    }

    @Override
    public void getUserDetails(com.uguimar.authms.grpc.UserDetailsRequest request, StreamObserver<com.uguimar.authms.grpc.UserDetailsResponse> responseObserver) {
        log.debug("Obteniendo detalles de usuario mediante gRPC para id: {}", request.getUserId());
        String userId = request.getUserId();

        userRepository.findById(userId)
                .map(this::mapToUserDetails)
                .map(userDetails -> com.uguimar.authms.grpc.UserDetailsResponse.newBuilder()
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

    private com.uguimar.authms.grpc.UserDetails mapToUserDetails(User user) {
        return com.uguimar.authms.grpc.UserDetails.newBuilder()
                .setId(user.getId())
                .setUsername(user.getUsername())
                .setEmail(user.getEmail())
                .addAllRoles(user.getRoles().stream()
                        .map(Role::getName)
                        .collect(Collectors.toList()))
                .build();
    }
}