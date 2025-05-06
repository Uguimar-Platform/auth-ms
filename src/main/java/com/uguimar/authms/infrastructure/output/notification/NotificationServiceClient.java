package com.uguimar.authms.infrastructure.output.notification;

import com.uguimar.authms.application.port.output.EmailService;
import com.uguimar.authms.application.port.output.PasswordResetNotificationService;
import com.uguimar.notification.grpc.NotificationServiceGrpc;
import com.uguimar.notification.grpc.VerificationCodeRequest;
import com.uguimar.notification.grpc.VerificationCodeResponse;
import io.grpc.StatusRuntimeException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Log4j2
public class NotificationServiceClient implements EmailService, PasswordResetNotificationService {

    private final NotificationServiceGrpc.NotificationServiceStub notificationServiceStub;

    @Override
    public Mono<Boolean> sendVerificationCode(String email, String username, String code) {
        return Mono.create(sink -> {
            try {
                VerificationCodeRequest request = VerificationCodeRequest.newBuilder()
                        .setEmail(email)
                        .setUsername(username)
                        .setVerificationCode(code)
                        .build();

                notificationServiceStub.sendVerificationCode(request, new io.grpc.stub.StreamObserver<>() {
                    @Override
                    public void onNext(VerificationCodeResponse response) {
                        if (response.getSuccess()) {
                            sink.success(true);
                        } else {
                            log.error("Error sending verification code: {}", response.getMessage());
                            sink.success(false);
                        }
                    }

                    @Override
                    public void onError(Throwable t) {
                        log.error("Error in notification service: {}", t.getMessage());
                        sink.error(t);
                    }

                    @Override
                    public void onCompleted() {
                        // Nothing to do
                    }
                });
            } catch (StatusRuntimeException e) {
                log.error("gRPC error: {}", e.getMessage());
                sink.error(e);
            } catch (Exception e) {
                log.error("Unexpected error: {}", e.getMessage());
                sink.error(e);
            }
        });
    }

    @Override
    public Mono<Boolean> sendPasswordResetCode(String email, String username, String code) {
        return Mono.create(sink -> {
            try {
                PasswordResetCodeRequest request = PasswordResetCodeRequest.newBuilder()
                        .setEmail(email)
                        .setUsername(username)
                        .setResetCode(code)
                        .build();

                notificationServiceStub.sendPasswordResetCode(request, new io.grpc.stub.StreamObserver<PasswordResetCodeResponse>() {
                    @Override
                    public void onNext(PasswordResetCodeResponse response) {
                        if (response.getSuccess()) {
                            sink.success(true);
                        } else {
                            log.error("Error sending password reset code: {}", response.getMessage());
                            sink.success(false);
                        }
                    }

                    @Override
                    public void onError(Throwable t) {
                        log.error("Error in notification service: {}", t.getMessage());
                        sink.error(t);
                    }

                    @Override
                    public void onCompleted() {
                        // Nothing to do
                    }
                });
            } catch (Exception e) {
                log.error("Unexpected error sending password reset code: {}", e.getMessage());
                sink.error(e);
            }
        });
    }
}