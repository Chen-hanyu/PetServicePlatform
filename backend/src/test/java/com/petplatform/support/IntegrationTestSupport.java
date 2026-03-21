package com.petplatform.support;

import com.petplatform.security.CurrentUser;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;

public abstract class IntegrationTestSupport {

    protected RequestPostProcessor currentUser(Long userId) {
        return currentUser(userId, "USER", "13800000000");
    }

    protected RequestPostProcessor currentAdmin(Long userId) {
        return currentUser(userId, "ADMIN", "13900000000");
    }

    protected RequestPostProcessor currentUser(Long userId, String role, String phone) {
        CurrentUser currentUser = new CurrentUser(userId, role, phone);
        return authentication(new UsernamePasswordAuthenticationToken(currentUser, null, currentUser.authorities()));
    }
}
