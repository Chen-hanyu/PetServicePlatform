package com.petplatform.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
@EnableConfigurationProperties(FileStorageProperties.class)
public class WebMvcConfig implements WebMvcConfigurer {

    private final FileStorageProperties fileStorageProperties;

    public WebMvcConfig(FileStorageProperties fileStorageProperties) {
        this.fileStorageProperties = fileStorageProperties;
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        Path localStoragePath = Paths.get(fileStorageProperties.getLocalPath()).toAbsolutePath().normalize();
        String accessPath = ensureEndsWithSlash(fileStorageProperties.getAccessPath());
        registry.addResourceHandler(accessPath + "**")
                .addResourceLocations(localStoragePath.toUri().toString());

        // Serve static placeholder images from resource/images/
        registry.addResourceHandler("/static/images/**")
                .addResourceLocations("file:./resource/images/");
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins("http://localhost:5173", "http://127.0.0.1:5173")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")
                .allowedHeaders("*");
    }

    private String ensureEndsWithSlash(String value) {
        return value.endsWith("/") ? value : value + "/";
    }
}
