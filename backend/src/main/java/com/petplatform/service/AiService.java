package com.petplatform.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.petplatform.common.ResultCode;
import com.petplatform.config.AiProperties;
import com.petplatform.dto.ai.AiChatRequest;
import com.petplatform.dto.ai.AiChatResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class AiService {

    private static final Logger log = LoggerFactory.getLogger(AiService.class);

    private static final String SYSTEM_PROMPT = """
            你是一位专业、温和的宠物健康顾问。请根据用户的问题，提供清晰、易懂、可执行的建议。

            重要规则：
            1. 回答要清晰、易懂、可执行
            2. 不编造医学检查结果或诊断结论
            3. 遇到持续呕吐、便血、呼吸困难、高烧、抽搐、误食有毒物等严重情况时，明确建议尽快前往正规宠物医院
            4. 尽量给出具体的观察建议和护理措施
            5. 回答控制在200字以内
            """;

    private static final String SUGGESTION_PROMPT = """
            Based on the user's question: "%s"
            Suggest 2-3 follow-up questions they might ask. Return ONLY a JSON array like ["suggestion 1", "suggestion 2", "suggestion 3"]. No other text.
            """;

    private final AiProperties aiProperties;
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public AiService(AiProperties aiProperties) {
        this.aiProperties = aiProperties;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(30))
                .build();
        this.objectMapper = new ObjectMapper();
    }

    public AiChatResponse chat(AiChatRequest request) {
        if (!StringUtils.hasText(aiProperties.getApiKey())) {
            log.warn("AI API key not configured, returning placeholder response");
            return new AiChatResponse(
                    "AI 功能暂未配置，请联系管理员配置 AI 服务。",
                    List.of("请联系管理员", "查看帮助文档")
            );
        }

        try {
            // 构建消息列表，添加系统提示词
            List<Map<String, String>> messages = new ArrayList<>();
            messages.add(Map.of("role", "system", "content", SYSTEM_PROMPT));

            // 添加用户历史消息
            for (AiChatRequest.ChatMessage msg : request.messages()) {
                if ("user".equals(msg.role()) || "assistant".equals(msg.role())) {
                    messages.add(Map.of("role", msg.role(), "content", msg.content()));
                }
            }

            // 构建请求体
            Map<String, Object> requestBody = Map.of(
                    "model", aiProperties.getModel(),
                    "messages", messages,
                    "max_tokens", 500,
                    "temperature", 0.7
            );

            String requestJson = objectMapper.writeValueAsString(requestBody);

            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(URI.create(aiProperties.getBaseUrl() + "/chat/completions"))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + aiProperties.getApiKey())
                    .POST(HttpRequest.BodyPublishers.ofString(requestJson))
                    .timeout(Duration.ofSeconds(60))
                    .build();

            HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                log.error("AI API returned status {}: {}", response.statusCode(), response.body());
                throw new RuntimeException("AI 服务调用失败: " + response.statusCode());
            }

            JsonNode responseJson = objectMapper.readTree(response.body());
            String reply = responseJson.path("choices")
                    .path(0)
                    .path("message")
                    .path("content")
                    .asText();

            // 生成建议
            List<String> suggestions = generateSuggestions(request, reply);

            return new AiChatResponse(reply, suggestions);

        } catch (Exception e) {
            log.error("AI chat failed", e);
            return new AiChatResponse(
                    "抱歉，AI 服务暂时不可用，请稍后重试或联系管理员。",
                    List.of("稍后重试", "查看常见问题")
            );
        }
    }

    private List<String> generateSuggestions(AiChatRequest request, String reply) {
        List<String> suggestions = new ArrayList<>();
        suggestions.add("继续咨询其他问题");

        String lastUserMessage = "";
        for (AiChatRequest.ChatMessage msg : request.messages()) {
            if ("user".equals(msg.role())) {
                lastUserMessage = msg.content();
            }
        }

        // 根据内容给出相关建议
        if (lastUserMessage.contains("呕吐") || lastUserMessage.contains("拉稀")) {
            suggestions.add("如何观察猫咪呕吐情况");
            suggestions.add("什么时候需要就医");
        } else if (lastUserMessage.contains("疫苗") || lastUserMessage.contains("打针")) {
            suggestions.add("猫咪需要打哪些疫苗");
            suggestions.add("疫苗接种注意事项");
        } else if (lastUserMessage.contains("饮食") || lastUserMessage.contains("吃什么")) {
            suggestions.add("幼猫喂食指南");
            suggestions.add("猫咪不能吃的食物");
        } else if (lastUserMessage.contains("洗澡") || lastUserMessage.contains("护理")) {
            suggestions.add("多久给猫咪洗一次澡");
            suggestions.add("如何给猫咪清洁耳朵");
        } else {
            suggestions.add("猫咪日常护理建议");
            suggestions.add("狗狗行为问题咨询");
        }

        return suggestions;
    }
}
