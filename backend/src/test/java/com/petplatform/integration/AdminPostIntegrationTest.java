package com.petplatform.integration;

import com.petplatform.entity.CommunityPost;
import com.petplatform.mapper.CommunityPostMapper;
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
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Sql(scripts = {
        "classpath:sql/integration-test-schema.sql",
        "classpath:sql/community-integration-data.sql"
}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class AdminPostIntegrationTest extends IntegrationTestSupport {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CommunityPostMapper communityPostMapper;

    @Test
    @DisplayName("管理员审核通过帖子后应更新状态、备注和发布时间")
    void reviewPostShouldApprovePendingPost() throws Exception {
        mockMvc.perform(put("/api/v1/admin/posts/2/review")
                        .with(currentAdmin(1L))
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {
                                  "status": "APPROVED",
                                  "remark": "内容符合社区规范"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.id").value(2))
                .andExpect(jsonPath("$.data.status").value("APPROVED"))
                .andExpect(jsonPath("$.data.review_remark").value("内容符合社区规范"));

        CommunityPost post = communityPostMapper.selectById(2L);
        assertThat(post.getStatus()).isEqualTo("APPROVED");
        assertThat(post.getReviewRemark()).isEqualTo("内容符合社区规范");
        assertThat(post.getPublishedAt()).isNotNull();
    }

    @Test
    @DisplayName("已审核帖子重复审核时应返回冲突")
    void reviewPostShouldRejectAlreadyReviewedPost() throws Exception {
        mockMvc.perform(put("/api/v1/admin/posts/1/review")
                        .with(currentAdmin(1L))
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {
                                  "status": "REJECTED",
                                  "remark": "重复审核"
                                }
                                """))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value(10009))
                .andExpect(jsonPath("$.message").value("帖子审核已处理"));
    }
}
