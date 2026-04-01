package com.petplatform.integration;

import com.petplatform.entity.AdoptionApplication;
import com.petplatform.entity.AdoptionPet;
import com.petplatform.mapper.AdoptionApplicationMapper;
import com.petplatform.mapper.AdoptionPetMapper;
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
        "classpath:sql/adoption-integration-data.sql"
}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class AdminAdoptionIntegrationTest extends IntegrationTestSupport {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AdoptionApplicationMapper adoptionApplicationMapper;

    @Autowired
    private AdoptionPetMapper adoptionPetMapper;

    @Test
    @DisplayName("管理员审核通过后应更新申请状态并同步宠物为已领养")
    void approveApplicationShouldUpdateApplicationAndPetStatus() throws Exception {
        mockMvc.perform(put("/api/v1/admin/adoption/applications/1/review")
                        .with(currentAdmin(99L))
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {
                                  "status": "APPROVED",
                                  "review_remark": "资料完整，安排接宠"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.status").value("APPROVED"))
                .andExpect(jsonPath("$.data.review_remark").value("资料完整，安排接宠"));

        AdoptionApplication application = adoptionApplicationMapper.selectById(1L);
        AdoptionPet pet = adoptionPetMapper.selectById(1L);
        assertThat(application.getStatus()).isEqualTo("APPROVED");
        assertThat(application.getReviewRemark()).isEqualTo("资料完整，安排接宠");
        assertThat(application.getReviewedBy()).isEqualTo(99L);
        assertThat(application.getReviewedAt()).isNotNull();
        assertThat(pet.getStatus()).isEqualTo("ADOPTED");
    }

    @Test
    @DisplayName("已处理申请重复审核时应返回冲突错误")
    void reviewShouldRejectAlreadyProcessedApplication() throws Exception {
        mockMvc.perform(put("/api/v1/admin/adoption/applications/2/review")
                        .with(currentAdmin(99L))
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {
                                  "status": "REJECTED",
                                  "review_remark": "重复审核"
                                }
                                """))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value(10009))
                .andExpect(jsonPath("$.message").value("领养申请审核已处理"));
    }
}
