package com.petplatform.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.petplatform.admin.controller.AdminUserController;
import com.petplatform.common.PageResponse;
import com.petplatform.common.exception.GlobalExceptionHandler;
import com.petplatform.config.SecurityConfig;
import com.petplatform.dto.admin.UpdateUserStatusRequest;
import com.petplatform.dto.auth.SendVerifyCodeResponse;
import com.petplatform.dto.pet.PetAlbumResponse;
import com.petplatform.dto.user.UserProfileResponse;
import com.petplatform.security.JwtAuthenticationFilter;
import com.petplatform.security.JwtTokenProvider;
import com.petplatform.security.RestAccessDeniedHandler;
import com.petplatform.security.RestAuthenticationEntryPoint;
import com.petplatform.service.AdminUserService;
import com.petplatform.service.AuthService;
import com.petplatform.service.PetService;
import com.petplatform.mapper.UserMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

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

    @MockBean
    private AuthService authService;

    @MockBean
    private PetService petService;

    @MockBean
    private AdminUserService adminUserService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
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
    @DisplayName("用户验证码接口应允许匿名访问并返回统一结构")
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
    @DisplayName("登录参数非法时应返回校验错误")
    void loginShouldReturnValidationErrorWhenPhoneIsInvalid() throws Exception {
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "phone": "123",
                                  "verify_code": "123456"
                                }
                                """))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.code").value(10002))
                .andExpect(jsonPath("$.message").value("手机号格式不正确"));
    }

    @Test
    @DisplayName("宠物接口未登录访问时应返回未授权")
    void petEndpointsShouldRejectAnonymousUser() throws Exception {
        mockMvc.perform(get("/api/v1/pets"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value(10004))
                .andExpect(jsonPath("$.message").value("登录失效"));
    }

    @Test
    @DisplayName("管理端接口未登录访问时应返回未授权")
    void adminEndpointsShouldRejectAnonymousUser() throws Exception {
        mockMvc.perform(get("/api/v1/admin/users"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value(10004))
                .andExpect(jsonPath("$.message").value("登录失效"));
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("普通用户访问管理端接口时应返回无权限")
    void adminEndpointsShouldRejectNormalUser() throws Exception {
        mockMvc.perform(get("/api/v1/admin/users"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value(10005))
                .andExpect(jsonPath("$.message").value("无权限"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("管理员访问管理端用户列表时应成功")
    void adminEndpointsShouldAllowAdminUser() throws Exception {
        PageResponse<UserProfileResponse> pageResponse = new PageResponse<>(
                List.of(new UserProfileResponse(1L, "USER", "13800000000", "测试用户", null, null, null, "ACTIVE")),
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
    @DisplayName("管理端列表参数非法时应返回参数错误")
    void adminEndpointsShouldValidateRequestParameters() throws Exception {
        mockMvc.perform(get("/api/v1/admin/users?page=0"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(10001));
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("登录用户可新增宠物相册")
    void authenticatedUserShouldCreatePetAlbum() throws Exception {
        when(petService.createAlbum(any(Long.class), any())).thenReturn(new PetAlbumResponse(9L, "/uploads/pet.png", "成长记录"));

        mockMvc.perform(post("/api/v1/pets/1/albums")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new Object() {
                            public final String image_url = "/uploads/pet.png";
                            public final String caption = "成长记录";
                        })))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.id").value(9))
                .andExpect(jsonPath("$.data.image_url").value("/uploads/pet.png"))
                .andExpect(jsonPath("$.data.caption").value("成长记录"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("管理端更新用户状态接口应使用统一返回结构")
    void adminStatusUpdateShouldReturnUnifiedResponse() throws Exception {
        when(adminUserService.updateUserStatus(any(Long.class), any(UpdateUserStatusRequest.class)))
                .thenReturn(new com.petplatform.dto.admin.UpdateUserStatusResponse(1L, "DISABLED", "违规内容"));

        mockMvc.perform(put("/api/v1/admin/users/1/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "status": "DISABLED",
                                  "remark": "违规内容"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.status").value("DISABLED"));
    }
}
