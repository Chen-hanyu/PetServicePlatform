package com.petplatform.service;

import com.petplatform.common.ResultCode;
import com.petplatform.common.exception.BusinessException;
import com.petplatform.config.VerifyCodeProperties;
import com.petplatform.dto.auth.SendVerifyCodeResponse;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class VerifyCodeService {

    private final VerifyCodeProperties verifyCodeProperties;
    private final Map<String, VerifyCodeEntry> verifyCodeStore = new ConcurrentHashMap<>();

    public VerifyCodeService(VerifyCodeProperties verifyCodeProperties) {
        this.verifyCodeProperties = verifyCodeProperties;
    }

    public SendVerifyCodeResponse sendUserLoginCode(String phone) {
        return sendCode(phone, "USER_LOGIN");
    }

    public SendVerifyCodeResponse sendAdminLoginCode(String phone) {
        return sendCode(phone, "ADMIN_LOGIN");
    }

    public void validateUserLoginCode(String phone, String verifyCode) {
        validateCode(phone, "USER_LOGIN", verifyCode);
    }

    public void validateAdminLoginCode(String phone, String verifyCode) {
        validateCode(phone, "ADMIN_LOGIN", verifyCode);
    }

    private SendVerifyCodeResponse sendCode(String phone, String scene) {
        clearExpiredCodes();
        String code = generateCode();
        long expiresIn = verifyCodeProperties.getExpirationSeconds();
        Instant expiresAt = Instant.now().plusSeconds(expiresIn);
        verifyCodeStore.put(buildKey(phone, scene), new VerifyCodeEntry(code, expiresAt));
        return new SendVerifyCodeResponse(phone, expiresIn, code);
    }

    private void validateCode(String phone, String scene, String verifyCode) {
        clearExpiredCodes();
        if (verifyCodeProperties.isAllowDefaultCode() && verifyCodeProperties.getDefaultCode().equals(verifyCode)) {
            return;
        }
        VerifyCodeEntry entry = verifyCodeStore.get(buildKey(phone, scene));
        if (entry == null || Instant.now().isAfter(entry.expiresAt()) || !entry.code().equals(verifyCode)) {
            throw new BusinessException(ResultCode.UNAUTHORIZED, "验证码错误或已过期");
        }
        verifyCodeStore.remove(buildKey(phone, scene));
    }

    private void clearExpiredCodes() {
        Instant now = Instant.now();
        verifyCodeStore.entrySet().removeIf(entry -> now.isAfter(entry.getValue().expiresAt()));
    }

    private String generateCode() {
        return String.valueOf(ThreadLocalRandom.current().nextInt(100000, 1000000));
    }

    private String buildKey(String phone, String scene) {
        return scene + ":" + phone;
    }

    private record VerifyCodeEntry(String code, Instant expiresAt) {
    }
}
