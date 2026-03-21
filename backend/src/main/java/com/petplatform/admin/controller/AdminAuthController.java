package com.petplatform.admin.controller;

import com.petplatform.common.ApiResponse;
import com.petplatform.dto.auth.LoginRequest;
import com.petplatform.dto.auth.LoginResponse;
import com.petplatform.dto.auth.SendVerifyCodeRequest;
import com.petplatform.dto.auth.SendVerifyCodeResponse;
import com.petplatform.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin/auth")
public class AdminAuthController {

    private final AuthService authService;

    public AdminAuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/verify-code")
    public ApiResponse<SendVerifyCodeResponse> sendVerifyCode(@Valid @RequestBody SendVerifyCodeRequest request) {
        return ApiResponse.success(authService.sendAdminVerifyCode(request));
    }

    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return ApiResponse.success(authService.loginAdmin(request));
    }
}
