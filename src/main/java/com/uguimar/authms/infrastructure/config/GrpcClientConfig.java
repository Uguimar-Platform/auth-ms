package com.uguimar.authms.infrastructure.config;

import com.uguimar.notification.grpc.NotificationServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
public class GrpcClientConfig {

    @Value("${grpc.notification.host:notification-service}")
    private String notificationHost;

    @Value("${grpc.notification.port:9092}")
    private int notificationPort;

    private ManagedChannel notificationChannel;

    @Bean
    public ManagedChannel notificationChannel() {
        notificationChannel = ManagedChannelBuilder.forAddress(notificationHost, notificationPort)
                .usePlaintext()
                .build();
        return notificationChannel;
    }

    @Bean
    public NotificationServiceGrpc.NotificationServiceStub notificationServiceStub(ManagedChannel notificationChannel) {
        return NotificationServiceGrpc.newStub(notificationChannel);
    }

    @PreDestroy
    public void shutdown() {
        if (notificationChannel != null) {
            try {
                notificationChannel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
