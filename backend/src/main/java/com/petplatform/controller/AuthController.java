package com.petplatform.controller;

import com.petplatform.common.ApiResponse;
import com.petplatform.dto.auth.LoginRequest;
import com.petplatform.dto.auth.LoginResponse;
import com.petplatform.dto.auth.RegisterRequest;
import com.petplatform.dto.auth.SendVerifyCodeRequest;
import com.petplatform.dto.auth.SendVerifyCodeResponse;
import com.petplatform.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<LoginResponse>> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(authService.registerUser(request)));
    }

    @PostMapping("/verify-code")
    public ApiResponse<SendVerifyCodeResponse> sendVerifyCode(@Valid @RequestBody SendVerifyCodeRequest request) {
        return ApiResponse.success(authService.sendUserVerifyCode(request));
    }

    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return ApiResponse.success(authService.loginUser(request));
    }

    @PostMapping("/logout")
    public ApiResponse<Void> logout() {
        authService.logoutUser();
        return ApiResponse.success();
    }
}
