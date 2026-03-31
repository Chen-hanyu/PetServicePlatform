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
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = AuthSuccessWebMvcTest.TestApplication.class)
@AutoConfigureMockMvc
class AuthSuccessWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    @SuppressWarnings("unused")
    private JwtTokenProvider jwtTokenProvider;

    @MockitoBean
    @SuppressWarnings("unused")
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
    @DisplayName("用户注册成功时应返回 201、令牌和用户信息")
    void userRegisterShouldReturnPayload() throws Exception {
        when(authService.registerUser(any())).thenReturn(new LoginResponse(
                "register-token",
                "Bearer",
                7200L,
                new UserProfileResponse(5L, "USER", "13800000009", "New User", null, null, null, "ACTIVE")
        ));

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "phone": "13800000009",
                                  "password": "password123",
                                  "nickname": "New User"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.token").value("register-token"))
                .andExpect(jsonPath("$.data.user.role").value("USER"));
    }

    @Test
    @DisplayName("用户获取验证码时应返回手机号和调试验证码")
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
    @DisplayName("用户密码登录成功时应返回令牌和用户信息")
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
                                  "password": "password123"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.token").value("user-token"))
                .andExpect(jsonPath("$.data.user.role").value("USER"));
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("用户登出时应返回成功响应")
    void userLogoutShouldReturnSuccess() throws Exception {
        mockMvc.perform(post("/api/v1/auth/logout"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));

        verify(authService).logoutUser();
    }

    @Test
    @DisplayName("管理员获取验证码时应返回手机号和调试验证码")
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
    @DisplayName("管理员密码登录成功时应返回令牌和管理员信息")
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
                                  "password": "admin123"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.token").value("admin-token"))
                .andExpect(jsonPath("$.data.user.role").value("ADMIN"));
    }
}
