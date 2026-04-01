package com.petplatform.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "verify-code")
public class VerifyCodeProperties {

    private long expirationSeconds = 300;
    private boolean allowDefaultCode = true;
    private String defaultCode = "123456";

    public long getExpirationSeconds() {
        return expirationSeconds;
    }

    public void setExpirationSeconds(long expirationSeconds) {
        this.expirationSeconds = expirationSeconds;
    }

    public boolean isAllowDefaultCode() {
        return allowDefaultCode;
    }

    public void setAllowDefaultCode(boolean allowDefaultCode) {
        this.allowDefaultCode = allowDefaultCode;
    }

    public String getDefaultCode() {
        return defaultCode;
    }

    public void setDefaultCode(String defaultCode) {
        this.defaultCode = defaultCode;
    }
}
