package com.petplatform.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;

@Configuration
@EnableConfigurationProperties(AiProperties.class)
public class AiConfig {

    @Bean
    public RestClient aiRestClient(RestClient.Builder builder, AiProperties aiProperties) {
        RestClient.Builder configuredBuilder = builder.baseUrl(aiProperties.getBaseUrl());
        if (StringUtils.hasText(aiProperties.getApiKey())) {
            configuredBuilder.defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + aiProperties.getApiKey());
        }
        return configuredBuilder.build();
    }
}
