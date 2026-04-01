package com.petplatform.integration;

import com.petplatform.entity.CommunityPost;
import com.petplatform.entity.PostComment;
import com.petplatform.mapper.CommunityPostMapper;
import com.petplatform.mapper.PostCommentMapper;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Sql(scripts = {
        "classpath:sql/integration-test-schema.sql",
        "classpath:sql/community-integration-data.sql"
}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class AdminCommentIntegrationTest extends IntegrationTestSupport {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PostCommentMapper postCommentMapper;

    @Autowired
    private CommunityPostMapper communityPostMapper;

    @Test
    @DisplayName("删除正常评论后应软删除评论并回滚帖子评论数")
    void deleteCommentShouldSoftDeleteAndRollbackCommentCount() throws Exception {
        mockMvc.perform(delete("/api/v1/admin/comments/1")
                        .with(currentAdmin(1L)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data").doesNotExist());

        PostComment comment = postCommentMapper.selectById(1L);
        CommunityPost post = communityPostMapper.selectById(1L);
        assertThat(comment.getStatus()).isEqualTo("DELETED");
        assertThat(post.getCommentCount()).isEqualTo(1);
    }
}
