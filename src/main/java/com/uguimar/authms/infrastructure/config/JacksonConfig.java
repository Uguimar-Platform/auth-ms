package com.uguimar.authms.infrastructure.config;

import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.http.codec.json.Jackson2JsonEncoder;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.web.reactive.config.WebFluxConfigurer;

import java.time.format.DateTimeFormatter;

@Configuration
public class JacksonConfig {
    private static final String DATE_FORMAT = "yyyy-MM-dd";
    private static final String DATETIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";

    @Bean
    public Jackson2ObjectMapperBuilder jacksonBuilder() {
        // Crear módulo de tiempo personalizado
        JavaTimeModule module = new JavaTimeModule();
        // Configurar serializador específico para LocalDate
        module.addSerializer(new LocalDateSerializer(DateTimeFormatter.ofPattern(DATE_FORMAT)));
        // Construir el objeto Jackson con las configuraciones específicas
        Jackson2ObjectMapperBuilder builder = new Jackson2ObjectMapperBuilder();
        builder.featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        builder.featuresToDisable(SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS);
        builder.modules(module);
        builder.simpleDateFormat(DATETIME_FORMAT);
        return builder;
    }

    @Bean
    public WebFluxConfigurer webFluxConfigurer(Jackson2ObjectMapperBuilder builder) {
        return new WebFluxConfigurer() {
            @Override
            public void configureHttpMessageCodecs(ServerCodecConfigurer configurer) {
                configurer.defaultCodecs().jackson2JsonEncoder(
                        new Jackson2JsonEncoder(builder.build())
                );
                configurer.defaultCodecs().jackson2JsonDecoder(
                        new Jackson2JsonDecoder(builder.build())
                );
            }
        };
    }
}