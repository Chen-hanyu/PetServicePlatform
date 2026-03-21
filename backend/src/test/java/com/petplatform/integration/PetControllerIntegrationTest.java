package com.petplatform.integration;

import com.petplatform.entity.Pet;
import com.petplatform.entity.PetWeight;
import com.petplatform.mapper.PetMapper;
import com.petplatform.mapper.PetWeightMapper;
import com.petplatform.support.IntegrationTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Sql(scripts = {
        "classpath:sql/integration-test-schema.sql",
        "classpath:sql/pet-integration-data.sql"
}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class PetControllerIntegrationTest extends IntegrationTestSupport {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PetMapper petMapper;

    @Autowired
    private PetWeightMapper petWeightMapper;

    @Test
    @DisplayName("新增体重记录后应写入数据库并同步当前宠物体重")
    void createWeightShouldPersistRecordAndUpdatePetWeight() throws Exception {
        mockMvc.perform(post("/api/v1/pets/1/weights")
                        .with(currentUser(1L))
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {
                                  "weight": 3.58,
                                  "recorded_at": "2026-03-20T10:15:00"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.weight").value(3.58))
                .andExpect(jsonPath("$.data.recorded_at").value("2026-03-20T10:15:00"));

        Pet pet = petMapper.selectById(1L);
        assertThat(pet.getWeight()).isEqualByComparingTo("3.58");

        List<PetWeight> weights = petWeightMapper.selectList(null);
        assertThat(weights).hasSize(2);
        assertThat(weights)
                .extracting(PetWeight::getWeight)
                .anyMatch(weight -> weight.compareTo(new BigDecimal("3.58")) == 0);
    }

    @Test
    @DisplayName("宠物时间轴应按时间正序聚合疫苗、体重和相册记录")
    void timelineShouldAggregatePetEventsInChronologicalOrder() throws Exception {
        mockMvc.perform(get("/api/v1/pets/1/timeline")
                        .with(currentUser(1L)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.pet.name").value("奶糖"))
                .andExpect(jsonPath("$.data.events.length()").value(3))
                .andExpect(jsonPath("$.data.events[0].type").value("VACCINE"))
                .andExpect(jsonPath("$.data.events[0].title").value("猫三联"))
                .andExpect(jsonPath("$.data.events[1].type").value("WEIGHT"))
                .andExpect(jsonPath("$.data.events[2].type").value("ALBUM"))
                .andExpect(jsonPath("$.data.events[2].image_url").value("/uploads/pets/album-1.png"));
    }

    @Test
    @DisplayName("用户访问他人宠物档案时应返回无权限")
    void shouldRejectAccessToOtherUsersPet() throws Exception {
        mockMvc.perform(get("/api/v1/pets/2")
                        .with(currentUser(1L)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value(10005))
                .andExpect(jsonPath("$.message").value("无权访问该宠物档案"));
    }
}
