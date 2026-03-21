package com.petplatform.controller;

import com.petplatform.admin.controller.AdminOpsController;
import com.petplatform.common.exception.GlobalExceptionHandler;
import com.petplatform.config.SecurityConfig;
import com.petplatform.dto.admin.AdminAdoptionPetResponse;
import com.petplatform.dto.admin.AdminBannerResponse;
import com.petplatform.dto.admin.AdminMerchantResponse;
import com.petplatform.dto.admin.AdminMerchantServiceResponse;
import com.petplatform.dto.admin.AdminProductResponse;
import com.petplatform.dto.admin.AdminRecommendationResponse;
import com.petplatform.dto.admin.AdminTagResponse;
import com.petplatform.dto.admin.UpdateProductStatusResponse;
import com.petplatform.dto.service.ServiceCategoryResponse;
import com.petplatform.mapper.UserMapper;
import com.petplatform.security.JwtAuthenticationFilter;
import com.petplatform.security.JwtTokenProvider;
import com.petplatform.security.RestAccessDeniedHandler;
import com.petplatform.security.RestAuthenticationEntryPoint;
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
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = AdminOpsMutationsWebMvcTest.TestApplication.class)
@AutoConfigureMockMvc
class AdminOpsMutationsWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AdminOpsService adminOpsService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private UserMapper userMapper;

    @SpringBootConfiguration
    @EnableAutoConfiguration
    @Import({
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
    @DisplayName("create adoption pet should return payload")
    void createAdoptionPetShouldReturnPayload() throws Exception {
        when(adminOpsService.createAdoptionPet(any())).thenReturn(new AdminAdoptionPetResponse(
                5L, "Dumpling", "CAT", "British Shorthair", "FEMALE", "2 years",
                "Shanghai", "Vaccinated", "Gentle and calm", "Windows should be secured",
                "Rescued from a rainy day", "/uploads/adoption-5.png", "ONLINE"
        ));

        mockMvc.perform(post("/api/v1/admin/adoption/pets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Dumpling",
                                  "type": "CAT",
                                  "breed": "British Shorthair",
                                  "gender": "FEMALE",
                                  "age_desc": "2 years",
                                  "city": "Shanghai",
                                  "health_status": "Vaccinated",
                                  "personality": "Gentle and calm",
                                  "adoption_requirements": "Windows should be secured",
                                  "story": "Rescued from a rainy day",
                                  "cover_url": "/uploads/adoption-5.png",
                                  "status": "ONLINE"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.name").value("Dumpling"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("update adoption pet should return payload")
    void updateAdoptionPetShouldReturnPayload() throws Exception {
        when(adminOpsService.updateAdoptionPet(any(), any())).thenReturn(new AdminAdoptionPetResponse(
                5L, "Dumpling", "CAT", "British Shorthair", "FEMALE", "2 years",
                "Shanghai", "Vaccinated", "Gentle and calm", "Updated requirements",
                "Rescued from a rainy day", "/uploads/adoption-5.png", "ONLINE"
        ));

        mockMvc.perform(put("/api/v1/admin/adoption/pets/5")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Dumpling",
                                  "type": "CAT",
                                  "breed": "British Shorthair",
                                  "gender": "FEMALE",
                                  "age_desc": "2 years",
                                  "city": "Shanghai",
                                  "health_status": "Vaccinated",
                                  "personality": "Gentle and calm",
                                  "adoption_requirements": "Updated requirements",
                                  "story": "Rescued from a rainy day",
                                  "cover_url": "/uploads/adoption-5.png",
                                  "status": "ONLINE"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.adoption_requirements").value("Updated requirements"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("create product should return payload")
    void createProductShouldReturnPayload() throws Exception {
        when(adminOpsService.createProduct(any())).thenReturn(new AdminProductResponse(
                10L, 1L, "Freeze-Dried Chicken", "High protein formula", "/uploads/product-10.png",
                new BigDecimal("59.90"), 20, "CAT", "Suitable for kittens and adult cats.", "ON_SALE"
        ));

        mockMvc.perform(post("/api/v1/admin/shop/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "category_id": 1,
                                  "name": "Freeze-Dried Chicken",
                                  "subtitle": "High protein formula",
                                  "image_url": "/uploads/product-10.png",
                                  "price": 59.9,
                                  "stock": 20,
                                  "pet_type": "CAT",
                                  "description": "Suitable for kittens and adult cats.",
                                  "status": "ON_SALE"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.name").value("Freeze-Dried Chicken"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("update product should return payload")
    void updateProductShouldReturnPayload() throws Exception {
        when(adminOpsService.updateProduct(any(), any())).thenReturn(new AdminProductResponse(
                10L, 1L, "Freeze-Dried Chicken", "Updated subtitle", "/uploads/product-10.png",
                new BigDecimal("59.90"), 18, "CAT", "Updated description", "ON_SALE"
        ));

        mockMvc.perform(put("/api/v1/admin/shop/products/10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "category_id": 1,
                                  "name": "Freeze-Dried Chicken",
                                  "subtitle": "Updated subtitle",
                                  "image_url": "/uploads/product-10.png",
                                  "price": 59.9,
                                  "stock": 18,
                                  "pet_type": "CAT",
                                  "description": "Updated description",
                                  "status": "ON_SALE"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.stock").value(18));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("update product status should return payload")
    void updateProductStatusShouldReturnPayload() throws Exception {
        when(adminOpsService.updateProductStatus(any(), any())).thenReturn(new UpdateProductStatusResponse(10L, "OFF_SHELF"));

        mockMvc.perform(put("/api/v1/admin/shop/products/10/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "status": "OFF_SHELF"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.status").value("OFF_SHELF"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("create service category should return payload")
    void createServiceCategoryShouldReturnPayload() throws Exception {
        when(adminOpsService.createServiceCategory(any())).thenReturn(new ServiceCategoryResponse(1L, "Grooming", 1, "ACTIVE"));

        mockMvc.perform(post("/api/v1/admin/services/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Grooming",
                                  "sort": 1,
                                  "status": "ACTIVE"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.name").value("Grooming"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("update service category should return payload")
    void updateServiceCategoryShouldReturnPayload() throws Exception {
        when(adminOpsService.updateServiceCategory(any(), any())).thenReturn(new ServiceCategoryResponse(1L, "Checkup", 2, "ACTIVE"));

        mockMvc.perform(put("/api/v1/admin/services/categories/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Checkup",
                                  "sort": 2,
                                  "status": "ACTIVE"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.name").value("Checkup"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("create merchant should return payload")
    void createMerchantShouldReturnPayload() throws Exception {
        when(adminOpsService.createMerchant(any())).thenReturn(new AdminMerchantResponse(
                1L, "Warm Paw House", "Pudong", "188 Jinxiu Road", "13800000000", "10:00-20:00", new BigDecimal("4.8"), "ACTIVE"
        ));

        mockMvc.perform(post("/api/v1/admin/services/merchants")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Warm Paw House",
                                  "district": "Pudong",
                                  "address": "188 Jinxiu Road",
                                  "phone": "13800000000",
                                  "business_hours": "10:00-20:00",
                                  "status": "ACTIVE"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.name").value("Warm Paw House"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("update merchant should return payload")
    void updateMerchantShouldReturnPayload() throws Exception {
        when(adminOpsService.updateMerchant(any(), any())).thenReturn(new AdminMerchantResponse(
                1L, "Warm Paw House", "Pudong", "Updated Address", "13800000000", "10:00-20:00", new BigDecimal("4.8"), "ACTIVE"
        ));

        mockMvc.perform(put("/api/v1/admin/services/merchants/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Warm Paw House",
                                  "district": "Pudong",
                                  "address": "Updated Address",
                                  "phone": "13800000000",
                                  "business_hours": "10:00-20:00",
                                  "status": "ACTIVE"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.address").value("Updated Address"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("create merchant service should return payload")
    void createMerchantServiceShouldReturnPayload() throws Exception {
        when(adminOpsService.createMerchantService(any())).thenReturn(new AdminMerchantServiceResponse(
                3L, 1L, 2L, "Basic Grooming", new BigDecimal("88.00"), 60, "ACTIVE"
        ));

        mockMvc.perform(post("/api/v1/admin/services/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "merchant_id": 1,
                                  "category_id": 2,
                                  "name": "Basic Grooming",
                                  "price": 88,
                                  "duration_minutes": 60,
                                  "status": "ACTIVE"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.name").value("Basic Grooming"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("update merchant service should return payload")
    void updateMerchantServiceShouldReturnPayload() throws Exception {
        when(adminOpsService.updateMerchantService(any(), any())).thenReturn(new AdminMerchantServiceResponse(
                3L, 1L, 2L, "Deluxe Grooming", new BigDecimal("128.00"), 90, "ACTIVE"
        ));

        mockMvc.perform(put("/api/v1/admin/services/items/3")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "merchant_id": 1,
                                  "category_id": 2,
                                  "name": "Deluxe Grooming",
                                  "price": 128,
                                  "duration_minutes": 90,
                                  "status": "ACTIVE"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.duration_minutes").value(90));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("create banner should return payload")
    void createBannerShouldReturnPayload() throws Exception {
        when(adminOpsService.createBanner(any())).thenReturn(new AdminBannerResponse(
                1L, "Spring Campaign", "/uploads/banner-1.png", "/pages/campaign", "ACTIVE", 1
        ));

        mockMvc.perform(post("/api/v1/admin/banners")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "title": "Spring Campaign",
                                  "image_url": "/uploads/banner-1.png",
                                  "link_url": "/pages/campaign",
                                  "status": "ACTIVE",
                                  "sort": 1
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.title").value("Spring Campaign"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("update banner should return payload")
    void updateBannerShouldReturnPayload() throws Exception {
        when(adminOpsService.updateBanner(any(), any())).thenReturn(new AdminBannerResponse(
                1L, "Updated Campaign", "/uploads/banner-1.png", "/pages/campaign", "ACTIVE", 2
        ));

        mockMvc.perform(put("/api/v1/admin/banners/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "title": "Updated Campaign",
                                  "image_url": "/uploads/banner-1.png",
                                  "link_url": "/pages/campaign",
                                  "status": "ACTIVE",
                                  "sort": 2
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.sort").value(2));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("create tag should return payload")
    void createTagShouldReturnPayload() throws Exception {
        when(adminOpsService.createTag(any())).thenReturn(new AdminTagResponse(1L, "NEWBIE", "POST", "ACTIVE", 1));

        mockMvc.perform(post("/api/v1/admin/tags")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "NEWBIE",
                                  "type": "POST",
                                  "status": "ACTIVE",
                                  "sort": 1
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.name").value("NEWBIE"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("update tag should return payload")
    void updateTagShouldReturnPayload() throws Exception {
        when(adminOpsService.updateTag(any(), any())).thenReturn(new AdminTagResponse(1L, "HEALTH", "POST", "ACTIVE", 2));

        mockMvc.perform(put("/api/v1/admin/tags/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "HEALTH",
                                  "type": "POST",
                                  "status": "ACTIVE",
                                  "sort": 2
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.name").value("HEALTH"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("create recommendation should return payload")
    void createRecommendationShouldReturnPayload() throws Exception {
        when(adminOpsService.createRecommendation(any())).thenReturn(new AdminRecommendationResponse(
                1L, "POST", 3L, "HOME_POST", "ACTIVE", 1, 1L
        ));

        mockMvc.perform(post("/api/v1/admin/recommendations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "biz_type": "POST",
                                  "biz_id": 3,
                                  "slot_code": "HOME_POST",
                                  "status": "ACTIVE",
                                  "sort": 1
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.biz_type").value("POST"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("update recommendation should return payload")
    void updateRecommendationShouldReturnPayload() throws Exception {
        when(adminOpsService.updateRecommendation(any(), any())).thenReturn(new AdminRecommendationResponse(
                1L, "PRODUCT", 10L, "HOME_PRODUCT", "ACTIVE", 2, 1L
        ));

        mockMvc.perform(put("/api/v1/admin/recommendations/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "biz_type": "PRODUCT",
                                  "biz_id": 10,
                                  "slot_code": "HOME_PRODUCT",
                                  "status": "ACTIVE",
                                  "sort": 2
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.slot_code").value("HOME_PRODUCT"));
    }
}
