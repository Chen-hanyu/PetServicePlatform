package com.petplatform.config;

import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import jakarta.annotation.PostConstruct;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.TimeZone;

@Configuration
public class JacksonConfig {

    private static final TimeZone APP_TIME_ZONE = TimeZone.getTimeZone("Asia/Shanghai");

    @PostConstruct
    public void configureDefaultTimeZone() {
        TimeZone.setDefault(APP_TIME_ZONE);
    }

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer longToStringCustomizer() {
        return builder -> builder
                .timeZone(APP_TIME_ZONE)
                .serializerByType(Long.class, ToStringSerializer.instance);
    }
}
