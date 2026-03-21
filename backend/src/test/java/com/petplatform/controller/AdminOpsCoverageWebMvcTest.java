package com.petplatform.controller;

import com.petplatform.admin.controller.AdminAdoptionController;
import com.petplatform.admin.controller.AdminDashboardController;
import com.petplatform.admin.controller.AdminMerchantReviewController;
import com.petplatform.admin.controller.AdminOpsController;
import com.petplatform.admin.controller.AdminPostController;
import com.petplatform.common.PageResponse;
import com.petplatform.common.exception.GlobalExceptionHandler;
import com.petplatform.config.SecurityConfig;
import com.petplatform.dto.admin.AdminAdoptionApplicationResponse;
import com.petplatform.dto.admin.AdminAdoptionPetResponse;
import com.petplatform.dto.admin.AdminBannerResponse;
import com.petplatform.dto.admin.AdminMerchantResponse;
import com.petplatform.dto.admin.AdminMerchantReviewResponse;
import com.petplatform.dto.admin.AdminMerchantServiceResponse;
import com.petplatform.dto.admin.AdminProductResponse;
import com.petplatform.dto.admin.AdminRecommendationResponse;
import com.petplatform.dto.admin.AdminTagResponse;
import com.petplatform.dto.admin.DashboardOverviewResponse;
import com.petplatform.dto.adoption.AdoptionPetSummaryResponse;
import com.petplatform.dto.community.PostAuthorResponse;
import com.petplatform.dto.community.PostSummaryResponse;
import com.petplatform.dto.service.ServiceCategoryResponse;
import com.petplatform.dto.shop.ProductCategoryResponse;
import com.petplatform.dto.user.UserProfileResponse;
import com.petplatform.mapper.UserMapper;
import com.petplatform.security.JwtAuthenticationFilter;
import com.petplatform.security.JwtTokenProvider;
import com.petplatform.security.RestAccessDeniedHandler;
import com.petplatform.security.RestAuthenticationEntryPoint;
import com.petplatform.service.AdminAdoptionService;
import com.petplatform.service.AdminCommunityService;
import com.petplatform.service.AdminDashboardService;
import com.petplatform.service.AdminMerchantReviewService;
import com.petplatform.service.AdminOpsService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = AdminOpsCoverageWebMvcTest.TestApplication.class)
@AutoConfigureMockMvc
class AdminOpsCoverageWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AdminDashboardService adminDashboardService;

    @MockBean
    private AdminAdoptionService adminAdoptionService;

    @MockBean
    private AdminCommunityService adminCommunityService;

    @MockBean
    private AdminMerchantReviewService adminMerchantReviewService;

    @MockBean
    private AdminOpsService adminOpsService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private UserMapper userMapper;

    @SpringBootConfiguration
    @EnableAutoConfiguration
    @Import({
            AdminDashboardController.class,
            AdminAdoptionController.class,
            AdminPostController.class,
            AdminMerchantReviewController.class,
            AdminOpsController.class,
            SecurityConfig.class,
            JwtAuthenticationFilter.class,
            RestAuthenticationEntryPoint.class,
            RestAccessDeniedHandler.class,
            GlobalExceptionHandler.class
    })
    static class TestApplication {
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("admin dashboard should return overview counters")
    void adminDashboardShouldReturnOverview() throws Exception {
        when(adminDashboardService.getOverview()).thenReturn(new DashboardOverviewResponse(
                10L,
                8L,
                6L,
                4L,
                2L,
                1L,
                List.of(new DashboardOverviewResponse.TrendPoint("03-20", 6L)),
                List.of(new DashboardOverviewResponse.TrendPoint("03-20", 4L))
        ));

        mockMvc.perform(get("/api/v1/admin/dashboard"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.user_total").value(10))
                .andExpect(jsonPath("$.data.order_trend[0].label").value("03-20"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("admin adoption applications should return page result")
    void adminAdoptionApplicationsShouldReturnPageResult() throws Exception {
        when(adminAdoptionService.getApplicationPage("PENDING", 5L, 1, 10)).thenReturn(new PageResponse<>(
                List.of(new AdminAdoptionApplicationResponse(
                        21L,
                        new AdoptionPetSummaryResponse(
                                5L,
                                "Dumpling",
                                "CAT",
                                "British Shorthair",
                                "FEMALE",
                                "2 years",
                                "Shanghai",
                                "Vaccinated",
                                "ONLINE",
                                "/uploads/adoption-5.png"
                        ),
                        new UserProfileResponse(2L, "USER", "13800000001", "Alice", null, "FEMALE", "Pet lover", "ACTIVE"),
                        "13800000001",
                        "Two years experience",
                        "All windows are secured",
                        "PENDING",
                        null,
                        LocalDateTime.of(2026, 3, 20, 10, 0),
                        null
                )),
                1,
                1,
                10
        ));

        mockMvc.perform(get("/api/v1/admin/adoption/applications")
                        .param("status", "PENDING")
                        .param("pet_id", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.list[0].pet.name").value("Dumpling"))
                .andExpect(jsonPath("$.data.list[0].user.nickname").value("Alice"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("admin posts should return page result")
    void adminPostsShouldReturnPageResult() throws Exception {
        when(adminCommunityService.getPostPage("PENDING", "CARE", "spring", 1, 10)).thenReturn(new PageResponse<>(
                List.of(new PostSummaryResponse(
                        1L,
                        "Spring Grooming Tips",
                        "CARE",
                        "/uploads/post-1.png",
                        "Keep pets warm after bathing.",
                        "PENDING",
                        12,
                        4,
                        3,
                        new PostAuthorResponse(8L, "Mimi", "/uploads/avatar.png"),
                        List.of("NEWBIE"),
                        LocalDateTime.of(2026, 3, 20, 9, 0)
                )),
                1,
                1,
                10
        ));

        mockMvc.perform(get("/api/v1/admin/posts")
                        .param("status", "PENDING")
                        .param("category", "CARE")
                        .param("keyword", "spring"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.list[0].title").value("Spring Grooming Tips"))
                .andExpect(jsonPath("$.data.list[0].status").value("PENDING"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("admin merchant reviews should return page result")
    void adminMerchantReviewsShouldReturnPageResult() throws Exception {
        when(adminMerchantReviewService.getReviewPage(1L, "patient", 1, 10)).thenReturn(new PageResponse<>(
                List.of(new AdminMerchantReviewResponse(
                        1L,
                        1L,
                        "Warm Paw House",
                        5,
                        "Friendly and patient.",
                        new AdminMerchantReviewResponse.Author(2L, "Alice", "13800000001"),
                        LocalDateTime.of(2026, 3, 20, 8, 0)
                )),
                1,
                1,
                10
        ));

        mockMvc.perform(get("/api/v1/admin/services/reviews")
                        .param("merchant_id", "1")
                        .param("keyword", "patient"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.list[0].merchant_name").value("Warm Paw House"))
                .andExpect(jsonPath("$.data.list[0].author.nickname").value("Alice"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("admin adoption pets should return page result")
    void adminAdoptionPetsShouldReturnPageResult() throws Exception {
        when(adminOpsService.getAdoptionPetPage("ONLINE", "CAT", "Shanghai", 1, 10)).thenReturn(new PageResponse<>(
                List.of(new AdminAdoptionPetResponse(
                        5L,
                        "Dumpling",
                        "CAT",
                        "British Shorthair",
                        "FEMALE",
                        "2 years",
                        "Shanghai",
                        "Vaccinated",
                        "Gentle and calm",
                        "Windows should be secured",
                        "Rescued from a rainy day",
                        "/uploads/adoption-5.png",
                        "ONLINE"
                )),
                1,
                1,
                10
        ));

        mockMvc.perform(get("/api/v1/admin/adoption/pets")
                        .param("status", "ONLINE")
                        .param("type", "CAT")
                        .param("city", "Shanghai"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.list[0].name").value("Dumpling"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("admin product categories should return list")
    void adminProductCategoriesShouldReturnList() throws Exception {
        when(adminOpsService.getProductCategories()).thenReturn(List.of(
                new ProductCategoryResponse(1L, "Cat Food", "CAT", 1, "ACTIVE"),
                new ProductCategoryResponse(2L, "Dog Toys", "DOG", 2, "ACTIVE")
        ));

        mockMvc.perform(get("/api/v1/admin/shop/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data[0].name").value("Cat Food"))
                .andExpect(jsonPath("$.data[1].pet_type").value("DOG"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("admin products should return page result")
    void adminProductsShouldReturnPageResult() throws Exception {
        when(adminOpsService.getProductPage("ON_SALE", "food", 1, 10)).thenReturn(new PageResponse<>(
                List.of(new AdminProductResponse(
                        10L,
                        1L,
                        "Freeze-Dried Chicken",
                        "High protein formula",
                        "/uploads/product-10.png",
                        new BigDecimal("59.90"),
                        20,
                        "CAT",
                        "Suitable for kittens and adult cats.",
                        "ON_SALE"
                )),
                1,
                1,
                10
        ));

        mockMvc.perform(get("/api/v1/admin/shop/products")
                        .param("status", "ON_SALE")
                        .param("keyword", "food"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.list[0].name").value("Freeze-Dried Chicken"))
                .andExpect(jsonPath("$.data.list[0].status").value("ON_SALE"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("admin service categories should return list")
    void adminServiceCategoriesShouldReturnList() throws Exception {
        when(adminOpsService.getServiceCategories()).thenReturn(List.of(
                new ServiceCategoryResponse(1L, "Grooming", 1, "ACTIVE"),
                new ServiceCategoryResponse(2L, "Checkup", 2, "ACTIVE")
        ));

        mockMvc.perform(get("/api/v1/admin/services/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data[0].name").value("Grooming"))
                .andExpect(jsonPath("$.data[1].name").value("Checkup"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("admin merchants should return page result")
    void adminMerchantsShouldReturnPageResult() throws Exception {
        when(adminOpsService.getMerchantPage("warm", "ACTIVE", 1, 10)).thenReturn(new PageResponse<>(
                List.of(new AdminMerchantResponse(
                        1L,
                        "Warm Paw House",
                        "Pudong",
                        "188 Jinxiu Road",
                        "13800000000",
                        "10:00-20:00",
                        new BigDecimal("4.8"),
                        "ACTIVE"
                )),
                1,
                1,
                10
        ));

        mockMvc.perform(get("/api/v1/admin/services/merchants")
                        .param("keyword", "warm")
                        .param("status", "ACTIVE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.list[0].name").value("Warm Paw House"))
                .andExpect(jsonPath("$.data.list[0].district").value("Pudong"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("admin service items should return page result")
    void adminServiceItemsShouldReturnPageResult() throws Exception {
        when(adminOpsService.getMerchantServicePage(1L, 2L, "ACTIVE", "basic", 1, 10)).thenReturn(new PageResponse<>(
                List.of(new AdminMerchantServiceResponse(
                        3L,
                        1L,
                        2L,
                        "Basic Grooming",
                        new BigDecimal("88.00"),
                        60,
                        "ACTIVE"
                )),
                1,
                1,
                10
        ));

        mockMvc.perform(get("/api/v1/admin/services/items")
                        .param("merchant_id", "1")
                        .param("category_id", "2")
                        .param("status", "ACTIVE")
                        .param("keyword", "basic"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.list[0].name").value("Basic Grooming"))
                .andExpect(jsonPath("$.data.list[0].duration_minutes").value(60));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("admin banners should return list")
    void adminBannersShouldReturnList() throws Exception {
        when(adminOpsService.getBanners()).thenReturn(List.of(
                new AdminBannerResponse(1L, "Spring Campaign", "/uploads/banner-1.png", "/pages/campaign", "ACTIVE", 1)
        ));

        mockMvc.perform(get("/api/v1/admin/banners"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data[0].title").value("Spring Campaign"))
                .andExpect(jsonPath("$.data[0].image_url").value("/uploads/banner-1.png"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("admin tags should return page result")
    void adminTagsShouldReturnPageResult() throws Exception {
        when(adminOpsService.getTagPage("POST", "ACTIVE", "new", 1, 10)).thenReturn(new PageResponse<>(
                List.of(new AdminTagResponse(1L, "NEWBIE", "POST", "ACTIVE", 1)),
                1,
                1,
                10
        ));

        mockMvc.perform(get("/api/v1/admin/tags")
                        .param("type", "POST")
                        .param("status", "ACTIVE")
                        .param("keyword", "new"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.list[0].name").value("NEWBIE"))
                .andExpect(jsonPath("$.data.list[0].type").value("POST"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("admin recommendations should return page result")
    void adminRecommendationsShouldReturnPageResult() throws Exception {
        when(adminOpsService.getRecommendationPage("HOME_POST", "POST", "ACTIVE", 1, 10)).thenReturn(new PageResponse<>(
                List.of(new AdminRecommendationResponse(1L, "POST", 3L, "HOME_POST", "ACTIVE", 1, 1L)),
                1,
                1,
                10
        ));

        mockMvc.perform(get("/api/v1/admin/recommendations")
                        .param("slot_code", "HOME_POST")
                        .param("biz_type", "POST")
                        .param("status", "ACTIVE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.list[0].slot_code").value("HOME_POST"))
                .andExpect(jsonPath("$.data.list[0].biz_id").value(3));
    }
}
