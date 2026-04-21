package com.petplatform.service;

import com.petplatform.common.ResultCode;
import com.petplatform.common.exception.BusinessException;
import com.petplatform.config.AiProperties;
import com.petplatform.dto.ai.AiChatMessageRequest;
import com.petplatform.dto.ai.AiChatRequest;
import com.petplatform.dto.ai.AiChatResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
public class AiService {

    private static final Logger log = LoggerFactory.getLogger(AiService.class);
    private static final int MAX_CONTEXT_MESSAGES = 12;

    private final RestClient aiRestClient;
    private final AiProperties aiProperties;

    public AiService(RestClient aiRestClient, AiProperties aiProperties) {
        this.aiRestClient = aiRestClient;
        this.aiProperties = aiProperties;
    }

    public AiChatResponse chat(AiChatRequest request) {
        validateConfiguration();

        List<OpenAiMessage> messages = buildMessages(request.messages());
        String latestUserMessage = findLatestUserMessage(request.messages());

        try {
            OpenAiChatCompletionResponse response = aiRestClient.post()
                    .uri("/chat/completions")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new OpenAiChatCompletionRequest(aiProperties.getModel(), messages, false))
                    .retrieve()
                    .body(OpenAiChatCompletionResponse.class);

            return new AiChatResponse(extractReply(response), buildSuggestions(latestUserMessage));
        } catch (RestClientResponseException exception) {
            log.warn("AI provider returned error status: {}, body: {}", exception.getStatusCode(), exception.getResponseBodyAsString());
            throw new BusinessException(ResultCode.AI_SERVICE_ERROR, "AI 服务调用失败，请检查模型配置或稍后重试");
        } catch (RestClientException exception) {
            log.warn("AI provider request failed", exception);
            throw new BusinessException(ResultCode.AI_SERVICE_ERROR, "AI 服务暂时不可用，请稍后重试");
        }
    }

    private void validateConfiguration() {
        if (!StringUtils.hasText(aiProperties.getBaseUrl()) || !StringUtils.hasText(aiProperties.getModel())) {
            throw new BusinessException(ResultCode.AI_NOT_CONFIGURED, "AI 服务配置不完整，请检查 AI_BASE_URL 和 AI_MODEL");
        }
        if (requiresApiKey(aiProperties.getBaseUrl()) && !StringUtils.hasText(aiProperties.getApiKey())) {
            throw new BusinessException(ResultCode.AI_NOT_CONFIGURED, "未配置 AI_API_KEY，无法调用云端模型");
        }
    }

    private boolean requiresApiKey(String baseUrl) {
        String normalized = baseUrl.toLowerCase(Locale.ROOT);
        return !normalized.contains("localhost") && !normalized.contains("127.0.0.1");
    }

    private List<OpenAiMessage> buildMessages(List<AiChatMessageRequest> requestMessages) {
        List<OpenAiMessage> messages = new ArrayList<>();
        messages.add(new OpenAiMessage("system", aiProperties.getSystemPrompt()));

        int startIndex = Math.max(0, requestMessages.size() - MAX_CONTEXT_MESSAGES);
        for (AiChatMessageRequest requestMessage : requestMessages.subList(startIndex, requestMessages.size())) {
            messages.add(new OpenAiMessage(requestMessage.role(), requestMessage.content().trim()));
        }
        return messages;
    }

    private String findLatestUserMessage(List<AiChatMessageRequest> messages) {
        for (int index = messages.size() - 1; index >= 0; index--) {
            AiChatMessageRequest message = messages.get(index);
            if ("user".equals(message.role())) {
                return message.content().trim();
            }
        }
        throw new BusinessException(ResultCode.PARAM_ERROR, "At least one user message is required");
    }

    private String extractReply(OpenAiChatCompletionResponse response) {
        if (response == null || response.choices() == null || response.choices().isEmpty()) {
            throw new BusinessException(ResultCode.AI_SERVICE_ERROR, "AI 服务未返回有效内容");
        }
        OpenAiMessage message = response.choices().get(0).message();
        if (message == null || !StringUtils.hasText(message.content())) {
            throw new BusinessException(ResultCode.AI_SERVICE_ERROR, "AI 服务未返回有效内容");
        }
        return message.content().trim();
    }

    private List<String> buildSuggestions(String latestUserMessage) {
        String normalized = latestUserMessage == null ? "" : latestUserMessage.toLowerCase(Locale.ROOT);
        if (containsAny(normalized, "吐", "呕", "拉", "腹泻", "没精神", "发烧")) {
            return List.of("可以补充一下症状持续了多久", "观察精神状态、食欲和排便情况", "如果出现便血或持续呕吐，尽快就医");
        }
        if (containsAny(normalized, "吃", "饮食", "狗粮", "猫粮", "零食")) {
            return List.of("补充一下宠物年龄和体重", "优先查看配料和适龄阶段", "换粮时建议 7 天内逐步过渡");
        }
        if (containsAny(normalized, "洗澡", "耳朵", "毛", "护理", "清洁")) {
            return List.of("先确认宠物最近是否有皮肤异常", "护理前准备宠物专用用品", "若有红肿异味，建议先咨询医生");
        }
        return List.of("可以补充宠物年龄、品种和体重", "如果方便，也可以说明症状出现的时间", "症状加重时建议尽快线下就诊");
    }

    private boolean containsAny(String content, String... keywords) {
        for (String keyword : keywords) {
            if (content.contains(keyword)) {
                return true;
            }
        }
        return false;
    }

    private record OpenAiChatCompletionRequest(
            String model,
            List<OpenAiMessage> messages,
            Boolean stream
    ) {
    }

    private record OpenAiChatCompletionResponse(
            List<OpenAiChoice> choices
    ) {
    }

    private record OpenAiChoice(
            OpenAiMessage message
    ) {
    }

    private record OpenAiMessage(
            String role,
            String content
    ) {
    }
}
