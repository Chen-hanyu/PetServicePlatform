package com.petplatform.service;

import com.petplatform.common.exception.BusinessException;
import com.petplatform.config.VerifyCodeProperties;
import com.petplatform.dto.auth.SendVerifyCodeResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class VerifyCodeServiceTest {

    @Test
    @DisplayName("发送验证码后应允许使用返回验证码完成一次性校验")
    void shouldValidateAndConsumeIssuedUserLoginCode() {
        VerifyCodeService service = new VerifyCodeService(buildProperties(true, "123456"));

        SendVerifyCodeResponse response = service.sendUserLoginCode("13800000000");

        service.validateUserLoginCode("13800000000", response.debugCode());

        assertThatThrownBy(() -> service.validateUserLoginCode("13800000000", response.debugCode()))
                .isInstanceOf(BusinessException.class)
                .hasMessage("验证码错误或已过期");
    }

    @Test
    @DisplayName("验证码错误时应抛出业务异常")
    void shouldRejectInvalidVerifyCode() {
        VerifyCodeService service = new VerifyCodeService(buildProperties(false, "123456"));
        service.sendUserLoginCode("13800000001");

        assertThatThrownBy(() -> service.validateUserLoginCode("13800000001", "000000"))
                .isInstanceOf(BusinessException.class)
                .hasMessage("验证码错误或已过期");
    }

    @Test
    @DisplayName("允许默认验证码时应接受默认值")
    void shouldAllowDefaultCodeWhenConfigured() {
        VerifyCodeService service = new VerifyCodeService(buildProperties(true, "123456"));

        service.validateAdminLoginCode("13900000000", "123456");

        assertThat(true).isTrue();
    }

    private VerifyCodeProperties buildProperties(boolean allowDefaultCode, String defaultCode) {
        VerifyCodeProperties properties = new VerifyCodeProperties();
        properties.setExpirationSeconds(300);
        properties.setAllowDefaultCode(allowDefaultCode);
        properties.setDefaultCode(defaultCode);
        return properties;
    }
}
