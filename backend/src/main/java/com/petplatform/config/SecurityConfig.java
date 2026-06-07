package com.petplatform.config;

import java.util.List;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.header.writers.XXssProtectionHeaderWriter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.petplatform.security.JwtAuthenticationFilter;
import com.petplatform.security.JwtProperties;
import com.petplatform.security.RestAccessDeniedHandler;
import com.petplatform.security.RestAuthenticationEntryPoint;

@Configuration
@EnableMethodSecurity
@EnableConfigurationProperties({JwtProperties.class, VerifyCodeProperties.class, AiProperties.class})
public class SecurityConfig {

    @Value("${app.cors.allowed-origin-patterns:http://localhost:*,http://127.0.0.1:*,http://[::1]:*,http://10.*:*,http://172.*:*,http://192.168.*:*,https://*.vercel.app,https://pet-service-platform-eta.vercel.app}")
    private String allowedOriginPatterns;

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            JwtAuthenticationFilter jwtAuthenticationFilter,
            RestAuthenticationEntryPoint authenticationEntryPoint,
            RestAccessDeniedHandler accessDeniedHandler
    ) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
                .headers(headers -> headers
                        .xssProtection(xss -> xss.headerValue(XXssProtectionHeaderWriter.HeaderValue.ENABLED_MODE_BLOCK))
                        .contentSecurityPolicy(csp -> csp.policyDirectives(
                                "default-src 'self'; script-src 'self'; style-src 'self' 'unsafe-inline'; "
                                + "img-src 'self' data:; font-src 'self'; connect-src 'self'; "
                                + "frame-ancestors 'none'"
                        ))
                        .frameOptions(frame -> frame.sameOrigin())
                        .httpStrictTransportSecurity(hsts -> hsts
                                .includeSubDomains(true)
                                .maxAgeInSeconds(31536000)
                        )
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(authenticationEntryPoint)
                        .accessDeniedHandler(accessDeniedHandler)
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers(
                                "/swagger-ui.html",
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/uploads/**"
                        ).permitAll()
                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/v1/auth/register",
                                "/api/v1/auth/login",
                                "/api/v1/auth/verify-code",
                                "/api/v1/admin/auth/login",
                                "/api/v1/admin/auth/verify-code"
                        ).permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/ai/chat").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/auth/logout").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/v1/admin/auth/logout").authenticated()
                        .requestMatchers("/api/v1/files/**").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/v1/community/posts").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/v1/community/posts/*/comments").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/v1/community/posts/*/like").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/v1/community/posts/*/favorite").authenticated()
                        .requestMatchers("/api/v1/community/favorites/**").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/community/favorites/*").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/v1/services/merchants/*/reviews").authenticated()
                        .requestMatchers("/api/v1/adoption/applications/**").authenticated()
                        .requestMatchers("/api/v1/pets/**").authenticated()
                        .requestMatchers("/api/v1/services/bookings").authenticated()
                        .requestMatchers("/api/v1/services/bookings/**").authenticated()
                        .requestMatchers("/api/v1/shop/cart").authenticated()
                        .requestMatchers("/api/v1/shop/cart/**").authenticated()
                        .requestMatchers("/api/v1/shop/addresses/**").authenticated()
                        .requestMatchers("/api/v1/shop/coupons/**").authenticated()
                        .requestMatchers("/api/v1/shop/orders").authenticated()
                        .requestMatchers("/api/v1/shop/orders/**").authenticated()
                        .requestMatchers("/api/v1/messages/**").authenticated()
                        .requestMatchers("/api/v1/profile/**").authenticated()
                        .requestMatchers("/api/v1/admin/**").hasRole("ADMIN")
                        .anyRequest().permitAll()
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(Stream.of(allowedOriginPatterns.split(","))
                .map(String::trim)
                .filter(pattern -> !pattern.isEmpty())
                .toList());
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setExposedHeaders(List.of("Authorization"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
