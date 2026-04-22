package com.petplatform.controller;

import com.petplatform.common.ResultCode;
import com.petplatform.common.exception.BusinessException;
import com.petplatform.common.exception.GlobalExceptionHandler;
import com.petplatform.config.SecurityConfig;
import com.petplatform.dto.ai.AiChatResponse;
import com.petplatform.mapper.UserMapper;
import com.petplatform.security.JwtAuthenticationFilter;
import com.petplatform.security.JwtTokenProvider;
import com.petplatform.security.RestAccessDeniedHandler;
import com.petplatform.security.RestAuthenticationEntryPoint;
import com.petplatform.service.AiService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = AiChatWebMvcTest.TestApplication.class)
@AutoConfigureMockMvc
class AiChatWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AiService aiService;

    @MockitoBean
    @SuppressWarnings("unused")
    private JwtTokenProvider jwtTokenProvider;

    @MockitoBean
    @SuppressWarnings("unused")
    private UserMapper userMapper;

    @SpringBootConfiguration
    @EnableAutoConfiguration
    @Import({
            AiController.class,
            SecurityConfig.class,
            JwtAuthenticationFilter.class,
            RestAuthenticationEntryPoint.class,
            RestAccessDeniedHandler.class,
            GlobalExceptionHandler.class
    })
    static class TestApplication {
    }

    @Test
    @DisplayName("AI 对话接口应允许匿名访问并返回统一响应")
    void chatShouldAllowAnonymousAccess() throws Exception {
        when(aiService.chat(any())).thenReturn(new AiChatResponse(
                "先观察猫咪是否还伴随流鼻涕、食欲下降或精神差，如果持续超过 24 小时，建议就医。",
                List.of("观察症状持续时间", "留意是否有食欲下降", "持续加重时尽快就医")
        ));

        mockMvc.perform(post("/api/v1/ai/chat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "messages": [
                                    {
                                      "role": "user",
                                      "content": "我家猫咪一直打喷嚏是怎么回事？"
                                    }
                                  ]
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.reply").exists())
                .andExpect(jsonPath("$.data.suggestions[0]").value("观察症状持续时间"));
    }

    @Test
    @DisplayName("AI 对话接口应校验消息列表")
    void chatShouldValidateMessages() throws Exception {
        mockMvc.perform(post("/api/v1/ai/chat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "messages": []
                                }
                                """))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.code").value(10002))
                .andExpect(jsonPath("$.message").value("Messages are required"));
    }

    @Test
    @DisplayName("AI 对话接口遇到非法 JSON 时应返回参数错误")
    void chatShouldReturnParamErrorWhenJsonIsInvalid() throws Exception {
        mockMvc.perform(post("/api/v1/ai/chat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "messages": [
                                    {
                                      "role": "user",
                                      "content": "你好"
                                    }
                                  ]
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(10001))
                .andExpect(jsonPath("$.message").value("请求体格式错误，请检查 JSON 结构"));
    }

    @Test
    @DisplayName("AI 服务未配置时应返回统一错误")
    void chatShouldReturnBusinessErrorWhenAiIsNotConfigured() throws Exception {
        when(aiService.chat(any())).thenThrow(new BusinessException(ResultCode.AI_NOT_CONFIGURED, "未配置 AI_API_KEY，无法调用云端模型"));

        mockMvc.perform(post("/api/v1/ai/chat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "messages": [
                                    {
                                      "role": "user",
                                      "content": "你好"
                                    }
                                  ]
                                }
                                """))
                .andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.code").value(10011))
                .andExpect(jsonPath("$.message").value("未配置 AI_API_KEY，无法调用云端模型"));
    }
}
