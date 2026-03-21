package com.petplatform.integration;

import com.petplatform.entity.Message;
import com.petplatform.mapper.MessageMapper;
import com.petplatform.support.IntegrationTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Sql(scripts = {
        "classpath:sql/integration-test-schema.sql",
        "classpath:sql/message-integration-data.sql"
}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class MessageControllerIntegrationTest extends IntegrationTestSupport {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MessageMapper messageMapper;

    @Test
    @DisplayName("消息列表应只返回当前用户消息并按未读优先排序")
    void getMessagesShouldReturnCurrentUsersMessagesOnly() throws Exception {
        mockMvc.perform(get("/api/v1/messages")
                        .with(currentUser(1L)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.total").value(2))
                .andExpect(jsonPath("$.data.list.length()").value(2))
                .andExpect(jsonPath("$.data.list[0].id").value(1))
                .andExpect(jsonPath("$.data.list[0].is_read").value(false))
                .andExpect(jsonPath("$.data.list[1].id").value(2));
    }

    @Test
    @DisplayName("标记消息已读后应更新数据库状态")
    void markMessageReadShouldPersistReadState() throws Exception {
        mockMvc.perform(post("/api/v1/messages/1/read")
                        .with(currentUser(1L)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.is_read").value(true));

        Message message = messageMapper.selectById(1L);
        assertThat(message.getRead()).isTrue();
    }

    @Test
    @DisplayName("用户标记他人消息已读时应返回资源不存在")
    void markOtherUsersMessageReadShouldFail() throws Exception {
        mockMvc.perform(post("/api/v1/messages/3/read")
                        .with(currentUser(1L)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(10003))
                .andExpect(jsonPath("$.message").value("消息不存在"));
    }
}
