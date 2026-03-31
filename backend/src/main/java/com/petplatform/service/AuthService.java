package com.petplatform.service;

import com.petplatform.common.ResultCode;
import com.petplatform.common.exception.BusinessException;
import com.petplatform.dto.auth.LoginRequest;
import com.petplatform.dto.auth.LoginResponse;
import com.petplatform.dto.auth.RegisterRequest;
import com.petplatform.dto.auth.SendVerifyCodeRequest;
import com.petplatform.dto.auth.SendVerifyCodeResponse;
import com.petplatform.entity.User;
import com.petplatform.security.JwtTokenProvider;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;
    private final VerifyCodeService verifyCodeService;

    public AuthService(
            UserService userService,
            JwtTokenProvider jwtTokenProvider,
            VerifyCodeService verifyCodeService
    ) {
        this.userService = userService;
        this.jwtTokenProvider = jwtTokenProvider;
        this.verifyCodeService = verifyCodeService;
    }

    public SendVerifyCodeResponse sendUserVerifyCode(SendVerifyCodeRequest request) {
        return verifyCodeService.sendUserLoginCode(request.phone());
    }

    public SendVerifyCodeResponse sendAdminVerifyCode(SendVerifyCodeRequest request) {
        return verifyCodeService.sendAdminLoginCode(request.phone());
    }

    public LoginResponse registerUser(RegisterRequest request) {
        User user = userService.createUser(request.phone(), request.password(), request.nickname());
        return buildLoginResponse(user);
    }

    public LoginResponse loginUser(LoginRequest request) {
        User user = userService.findByPhone(request.phone());
        if (user == null) {
            throw new BusinessException(ResultCode.UNAUTHORIZED, "Invalid phone or password");
        }
        if (!"USER".equals(user.getRole())) {
            throw new BusinessException(ResultCode.FORBIDDEN, "Current account is not a user");
        }
        if (!userService.matchesPassword(user, request.password())) {
            throw new BusinessException(ResultCode.UNAUTHORIZED, "Invalid phone or password");
        }
        userService.ensureActive(user);
        return buildLoginResponse(user);
    }

    public LoginResponse loginAdmin(LoginRequest request) {
        User user = userService.findByPhone(request.phone());
        if (user == null || !"ADMIN".equals(user.getRole())) {
            throw new BusinessException(ResultCode.UNAUTHORIZED, "Invalid phone or password");
        }
        if (!userService.matchesPassword(user, request.password())) {
            throw new BusinessException(ResultCode.UNAUTHORIZED, "Invalid phone or password");
        }
        userService.ensureActive(user);
        return buildLoginResponse(user);
    }

    public void logoutUser() {
        // JWT is stateless. Current logout behavior is client-side token discard.
    }

    private LoginResponse buildLoginResponse(User user) {
        return new LoginResponse(
                jwtTokenProvider.generateToken(user),
                "Bearer",
                jwtTokenProvider.getExpirationSeconds(),
                userService.toUserProfile(user)
        );
    }
}
