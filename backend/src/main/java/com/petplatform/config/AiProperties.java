package com.petplatform.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "ai")
public class AiProperties {

    private String apiKey = "";
    private String baseUrl = "https://api.deepseek.com";
    private String model = "deepseek-chat";
    private String systemPrompt = """
            你是一名专业、温和的宠物健康顾问。
            请结合用户提供的上下文，给出清晰、易懂、可执行的建议。
            如果问题涉及持续呕吐、便血、呼吸困难、高烧、抽搐、误食有毒物等严重情况，请明确建议尽快前往正规宠物医院。
            不要编造检查结果或诊断结论，优先给出观察要点、居家护理建议和就医判断。
            """;

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getSystemPrompt() {
        return systemPrompt;
    }

    public void setSystemPrompt(String systemPrompt) {
        this.systemPrompt = systemPrompt;
    }
}
