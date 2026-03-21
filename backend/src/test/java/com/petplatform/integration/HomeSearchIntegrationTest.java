package com.petplatform.integration;

import com.petplatform.support.IntegrationTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Sql(scripts = {
        "classpath:sql/integration-test-schema.sql",
        "classpath:sql/home-search-integration-data.sql"
}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class HomeSearchIntegrationTest extends IntegrationTestSupport {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("home should aggregate banners, recommendations and pet cards from database")
    void homeShouldAggregateDatabaseData() throws Exception {
        mockMvc.perform(get("/api/v1/home"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.banners.length()").value(2))
                .andExpect(jsonPath("$.data.banners[0].title").value("Spring Care Week"))
                .andExpect(jsonPath("$.data.recommended_posts[0].title").value("Cozy Pet Morning"))
                .andExpect(jsonPath("$.data.recommended_services[0].name").value("Warm Vet Clinic"))
                .andExpect(jsonPath("$.data.recommended_products[0].name").value("Daily Dog Snacks"))
                .andExpect(jsonPath("$.data.pet_cards[0].title").value("Spring Dumpling"))
                .andExpect(jsonPath("$.data.quick_entries.length()").value(4));
    }

    @Test
    @DisplayName("search should aggregate matching results from all public modules")
    void searchShouldAggregateResultsAcrossModules() throws Exception {
        mockMvc.perform(get("/api/v1/search")
                        .param("keyword", "spring"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.total").value(4))
                .andExpect(jsonPath("$.data.list.length()").value(4));
    }

    @Test
    @DisplayName("search should filter by requested module")
    void searchShouldFilterByModule() throws Exception {
        mockMvc.perform(get("/api/v1/search")
                        .param("keyword", "spring")
                        .param("module", "community"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.total").value(1))
                .andExpect(jsonPath("$.data.list[0].module").value("community"))
                .andExpect(jsonPath("$.data.list[0].title").value("Spring Grooming Tips"));
    }
}
