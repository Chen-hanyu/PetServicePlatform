package com.petplatform.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.petplatform.admin.controller.AdminUserController;
import com.petplatform.common.PageResponse;
import com.petplatform.common.exception.GlobalExceptionHandler;
import com.petplatform.config.SecurityConfig;
import com.petplatform.dto.admin.UpdateUserStatusRequest;
import com.petplatform.dto.auth.LoginResponse;
import com.petplatform.dto.auth.SendVerifyCodeResponse;
import com.petplatform.dto.pet.PetAlbumResponse;
import com.petplatform.dto.user.UserProfileResponse;
import com.petplatform.mapper.UserMapper;
import com.petplatform.security.JwtAuthenticationFilter;
import com.petplatform.security.JwtTokenProvider;
import com.petplatform.security.RestAccessDeniedHandler;
import com.petplatform.security.RestAuthenticationEntryPoint;
import com.petplatform.service.AdminUserService;
import com.petplatform.service.AuthService;
import com.petplatform.service.PetService;
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

import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = SecurityRoutingWebMvcTest.TestApplication.class)
@AutoConfigureMockMvc
class SecurityRoutingWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private PetService petService;

    @MockitoBean
    private AdminUserService adminUserService;

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
            PetController.class,
            AdminUserController.class,
            SecurityConfig.class,
            JwtAuthenticationFilter.class,
            RestAuthenticationEntryPoint.class,
            RestAccessDeniedHandler.class,
            GlobalExceptionHandler.class
    })
    static class TestApplication {
    }

    @Test
    @DisplayName("注册接口应允许匿名访问")
    void registerShouldAllowAnonymousAccess() throws Exception {
        when(authService.registerUser(any())).thenReturn(new LoginResponse(
                "register-token",
                "Bearer",
                7200L,
                new UserProfileResponse(8L, "USER", "13800000008", "Register User", null, null, null, "ACTIVE")
        ));

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "phone": "13800000008",
                                  "password": "password123",
                                  "nickname": "Register User"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.user.phone").value("13800000008"));
    }

    @Test
    @DisplayName("验证码接口应允许匿名访问")
    void sendVerifyCodeShouldAllowAnonymousAccess() throws Exception {
        SendVerifyCodeResponse response = new SendVerifyCodeResponse("13800000000", 300, "654321");
        when(authService.sendUserVerifyCode(any())).thenReturn(response);

        mockMvc.perform(post("/api/v1/auth/verify-code")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "phone": "13800000000"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.message").value("ok"))
                .andExpect(jsonPath("$.data.phone").value("13800000000"))
                .andExpect(jsonPath("$.data.expires_in").value(300))
                .andExpect(jsonPath("$.data.debug_code").value("654321"));

        verify(authService).sendUserVerifyCode(any());
    }

    @Test
    @DisplayName("登录接口应校验手机号格式")
    void loginShouldReturnValidationErrorWhenPhoneIsInvalid() throws Exception {
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "phone": "123",
                                  "password": "password123"
                                }
                                """))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.code").value(10002))
                .andExpect(jsonPath("$.message").value("Phone format is invalid"));
    }

    @Test
    @DisplayName("登出接口应拒绝匿名用户")
    void logoutShouldRejectAnonymousUser() throws Exception {
        mockMvc.perform(post("/api/v1/auth/logout"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value(10004));
    }

    @Test
    @DisplayName("宠物相关接口应拒绝匿名用户")
    void petEndpointsShouldRejectAnonymousUser() throws Exception {
        mockMvc.perform(get("/api/v1/pets"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value(10004));
    }

    @Test
    @DisplayName("管理端接口应拒绝匿名用户")
    void adminEndpointsShouldRejectAnonymousUser() throws Exception {
        mockMvc.perform(get("/api/v1/admin/users"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value(10004));
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("普通用户访问管理端接口时应被禁止")
    void adminEndpointsShouldRejectNormalUser() throws Exception {
        mockMvc.perform(get("/api/v1/admin/users"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value(10005));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("管理员应可访问管理端用户列表")
    void adminEndpointsShouldAllowAdminUser() throws Exception {
        PageResponse<UserProfileResponse> pageResponse = new PageResponse<>(
                List.of(new UserProfileResponse(1L, "USER", "13800000000", "Test User", null, null, null, "ACTIVE")),
                1,
                1,
                10
        );
        when(adminUserService.getUserPage(null, null, 1, 10)).thenReturn(pageResponse);

        mockMvc.perform(get("/api/v1/admin/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.total").value(1))
                .andExpect(jsonPath("$.data.list[0].phone").value("13800000000"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("管理端接口应校验请求参数")
    void adminEndpointsShouldValidateRequestParameters() throws Exception {
        mockMvc.perform(get("/api/v1/admin/users?page=0"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(10001));
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("已登录用户应可创建宠物相册记录")
    void authenticatedUserShouldCreatePetAlbum() throws Exception {
        when(petService.createAlbum(any(Long.class), any())).thenReturn(new PetAlbumResponse(9L, "/uploads/pet.png", "Growth"));

        Map<String, String> requestBody = Map.of(
                "image_url", "/uploads/pet.png",
                "caption", "Growth"
        );
        mockMvc.perform(post("/api/v1/pets/1/albums")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestBody)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.id").value(9))
                .andExpect(jsonPath("$.data.image_url").value("/uploads/pet.png"))
                .andExpect(jsonPath("$.data.caption").value("Growth"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("管理端状态更新接口应使用统一响应格式")
    void adminStatusUpdateShouldReturnUnifiedResponse() throws Exception {
        when(adminUserService.updateUserStatus(any(Long.class), any(UpdateUserStatusRequest.class)))
                .thenReturn(new com.petplatform.dto.admin.UpdateUserStatusResponse(1L, "DISABLED", "Flagged"));

        mockMvc.perform(put("/api/v1/admin/users/1/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "status": "DISABLED",
                                  "remark": "Flagged"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.status").value("DISABLED"));
    }
}
