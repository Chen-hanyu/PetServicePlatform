package com.petplatform.integration;

import com.petplatform.entity.AdoptionApplication;
import com.petplatform.mapper.AdoptionApplicationMapper;
import com.petplatform.support.IntegrationTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Sql(scripts = {
        "classpath:sql/integration-test-schema.sql",
        "classpath:sql/adoption-integration-data.sql"
}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class AdoptionApplicationIntegrationTest extends IntegrationTestSupport {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AdoptionApplicationMapper adoptionApplicationMapper;

    @Test
    @DisplayName("同一用户对同一宠物重复提交待审核申请时应返回冲突")
    void createApplicationShouldRejectDuplicatePendingApplication() throws Exception {
        mockMvc.perform(post("/api/v1/adoption/applications")
                        .with(currentUser(10L, "USER", "13800000010"))
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {
                                  "pet_id": 1,
                                  "contact_phone": "13800000010",
                                  "experience_desc": "有三年养猫经验",
                                  "living_condition_desc": "自有住房，已封窗"
                                }
                                """))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value(10007))
                .andExpect(jsonPath("$.message").value("请勿重复提交待审核中的领养申请"));
    }

    @Test
    @DisplayName("新用户提交领养申请后应成功入库")
    void createApplicationShouldPersistNewApplication() throws Exception {
        mockMvc.perform(post("/api/v1/adoption/applications")
                        .with(currentUser(12L, "USER", "13800000012"))
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {
                                  "pet_id": 1,
                                  "contact_phone": "13800000012",
                                  "experience_desc": "有稳定养宠经验",
                                  "living_condition_desc": "家庭支持领养，可长期照顾"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.status").value("PENDING"));

        List<AdoptionApplication> applications = adoptionApplicationMapper.selectList(null);
        assertThat(applications).hasSize(3);
        assertThat(applications)
                .anyMatch(application ->
                        application.getUserId().equals(12L)
                                && application.getPetId().equals(1L)
                                && "PENDING".equals(application.getStatus()));
    }
}
