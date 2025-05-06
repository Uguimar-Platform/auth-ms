package com.uguimar.authms.infrastructure.config;

import com.uguimar.authms.domain.model.AuditActionType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.ReactiveAuditorAware;
import org.springframework.data.r2dbc.config.EnableR2dbcAuditing;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import reactor.core.publisher.Mono;

@Configuration
@EnableR2dbcAuditing
public class AuditingConfig {

    // Stores the specific auditor for this transaction
    private static final ThreadLocal<String> CURRENT_AUDITOR = new ThreadLocal<>();

    public static void setAuditor(String auditor) {
        CURRENT_AUDITOR.set(auditor);
    }

    public static void clearAuditor() {
        CURRENT_AUDITOR.remove();
    }

    @Bean
    ReactiveAuditorAware<String> auditorAware() {
        return () -> {
            // If there is a specific auditor for this transaction, use it
            String specificAuditor = CURRENT_AUDITOR.get();
            if (specificAuditor != null) {
                return Mono.just(specificAuditor);
            }

            // Otherwise, get the authenticated user or use SYSTEM as the default value
            return ReactiveSecurityContextHolder.getContext()
                    .map(SecurityContext::getAuthentication)
                    .filter(Authentication::isAuthenticated)
                    .map(Authentication::getName)
                    .switchIfEmpty(Mono.just(AuditActionType.SYSTEM.getValue()));
        };
    }
}
