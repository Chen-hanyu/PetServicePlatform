package com.petplatform.controller;

import com.petplatform.admin.controller.AdminAuthController;
import com.petplatform.common.exception.GlobalExceptionHandler;
import com.petplatform.config.SecurityConfig;
import com.petplatform.dto.auth.LoginResponse;
import com.petplatform.dto.auth.SendVerifyCodeResponse;
import com.petplatform.dto.user.UserProfileResponse;
import com.petplatform.mapper.UserMapper;
import com.petplatform.security.JwtAuthenticationFilter;
import com.petplatform.security.JwtTokenProvider;
import com.petplatform.security.RestAccessDeniedHandler;
import com.petplatform.security.RestAuthenticationEntryPoint;
import com.petplatform.service.AuthService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = AuthSuccessWebMvcTest.TestApplication.class)
@AutoConfigureMockMvc
class AuthSuccessWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private UserMapper userMapper;

    @SpringBootConfiguration
    @EnableAutoConfiguration
    @Import({
            AuthController.class,
            AdminAuthController.class,
            SecurityConfig.class,
            JwtAuthenticationFilter.class,
            RestAuthenticationEntryPoint.class,
            RestAccessDeniedHandler.class,
            GlobalExceptionHandler.class
    })
    static class TestApplication {
    }

    @Test
    @DisplayName("user verify code should return phone and debug code")
    void userVerifyCodeShouldReturnPayload() throws Exception {
        when(authService.sendUserVerifyCode(any())).thenReturn(new SendVerifyCodeResponse(
                "13800000001", 300L, "123456"
        ));

        mockMvc.perform(post("/api/v1/auth/verify-code")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "phone": "13800000001"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.phone").value("13800000001"))
                .andExpect(jsonPath("$.data.debug_code").value("123456"));
    }

    @Test
    @DisplayName("user login should return token and user profile")
    void userLoginShouldReturnPayload() throws Exception {
        when(authService.loginUser(any())).thenReturn(new LoginResponse(
                "user-token",
                "Bearer",
                7200L,
                new UserProfileResponse(2L, "USER", "13800000001", "Alice", null, "FEMALE", "Pet lover", "ACTIVE")
        ));

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "phone": "13800000001",
                                  "verify_code": "123456"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.token").value("user-token"))
                .andExpect(jsonPath("$.data.user.role").value("USER"));
    }

    @Test
    @DisplayName("admin verify code should return phone and debug code")
    void adminVerifyCodeShouldReturnPayload() throws Exception {
        when(authService.sendAdminVerifyCode(any())).thenReturn(new SendVerifyCodeResponse(
                "13900000000", 300L, "654321"
        ));

        mockMvc.perform(post("/api/v1/admin/auth/verify-code")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "phone": "13900000000"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.phone").value("13900000000"))
                .andExpect(jsonPath("$.data.debug_code").value("654321"));
    }

    @Test
    @DisplayName("admin login should return token and admin profile")
    void adminLoginShouldReturnPayload() throws Exception {
        when(authService.loginAdmin(any())).thenReturn(new LoginResponse(
                "admin-token",
                "Bearer",
                7200L,
                new UserProfileResponse(1L, "ADMIN", "13900000000", "Admin", null, null, "Administrator", "ACTIVE")
        ));

        mockMvc.perform(post("/api/v1/admin/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "phone": "13900000000",
                                  "verify_code": "654321"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.token").value("admin-token"))
                .andExpect(jsonPath("$.data.user.role").value("ADMIN"));
    }
}
