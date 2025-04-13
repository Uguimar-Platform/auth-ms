package com.yobel.authms.infrastructure.config;

import io.r2dbc.spi.ConnectionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;
import org.springframework.r2dbc.connection.init.ConnectionFactoryInitializer;
import org.springframework.r2dbc.connection.init.ResourceDatabasePopulator;
import org.springframework.web.reactive.config.EnableWebFlux;

@Configuration
@EnableWebFlux
@EnableR2dbcRepositories(basePackages = "com.yobel.authms.infrastructure.output.persistence.repository")
public class ApplicationConfig {

    @Bean
    public ConnectionFactoryInitializer initializer(ConnectionFactory connectionFactory) {
        ConnectionFactoryInitializer initializer = new ConnectionFactoryInitializer();
        initializer.setConnectionFactory(connectionFactory);
        // Cargar script de inicialización de la base de datos si existe
        ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
        try {
            populator.addScript(new ClassPathResource("schema.sql"));
            initializer.setDatabasePopulator(populator);
        } catch (Exception e) {
            // En caso de que no exista el archivo, solo loguear
        }
        return initializer;
    }
}
