package com.petplatform.service;

import com.petplatform.common.ResultCode;
import com.petplatform.common.exception.BusinessException;
import com.petplatform.dto.auth.LoginRequest;
import com.petplatform.dto.auth.LoginResponse;
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

    public LoginResponse loginUser(LoginRequest request) {
        verifyCodeService.validateUserLoginCode(request.phone(), request.verifyCode());
        User user = userService.findByPhone(request.phone());
        if (user == null) {
            user = userService.createUserByPhone(request.phone());
        }
        if (!"USER".equals(user.getRole())) {
            throw new BusinessException(ResultCode.FORBIDDEN, "当前账号不是用户角色");
        }
        userService.ensureActive(user);
        return buildLoginResponse(user);
    }

    public LoginResponse loginAdmin(LoginRequest request) {
        verifyCodeService.validateAdminLoginCode(request.phone(), request.verifyCode());
        User user = userService.findByPhone(request.phone());
        if (user == null || !"ADMIN".equals(user.getRole())) {
            throw new BusinessException(ResultCode.UNAUTHORIZED, "管理员账号或验证码错误");
        }
        userService.ensureActive(user);
        return buildLoginResponse(user);
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
