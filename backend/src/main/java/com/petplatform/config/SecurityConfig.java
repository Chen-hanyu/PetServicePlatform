package com.petplatform.config;

import com.petplatform.security.JwtAuthenticationFilter;
import com.petplatform.security.JwtProperties;
import com.petplatform.security.RestAccessDeniedHandler;
import com.petplatform.security.RestAuthenticationEntryPoint;
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

@Configuration
@EnableMethodSecurity
@EnableConfigurationProperties({JwtProperties.class, VerifyCodeProperties.class})
public class SecurityConfig {

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
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(authenticationEntryPoint)
                        .accessDeniedHandler(accessDeniedHandler)
                )
                .authorizeHttpRequests(auth -> auth
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
                        .requestMatchers(HttpMethod.POST, "/api/v1/auth/logout").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/v1/admin/auth/logout").authenticated()
                        .requestMatchers("/api/v1/files/**").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/v1/community/posts").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/v1/community/posts/*/comments").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/v1/community/posts/*/like").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/v1/community/posts/*/favorite").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/v1/services/merchants/*/reviews").authenticated()
                        .requestMatchers("/api/v1/adoption/applications/**").authenticated()
                        .requestMatchers("/api/v1/pets/**").authenticated()
                        .requestMatchers("/api/v1/services/bookings").authenticated()
                        .requestMatchers("/api/v1/services/bookings/**").authenticated()
                        .requestMatchers("/api/v1/shop/cart").authenticated()
                        .requestMatchers("/api/v1/shop/cart/**").authenticated()
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
}
