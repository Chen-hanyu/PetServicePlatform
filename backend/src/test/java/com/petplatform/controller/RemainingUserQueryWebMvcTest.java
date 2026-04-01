package com.petplatform.controller;

import com.petplatform.common.PageResponse;
import com.petplatform.common.exception.GlobalExceptionHandler;
import com.petplatform.config.SecurityConfig;
import com.petplatform.dto.adoption.AdoptionApplicationSummaryResponse;
import com.petplatform.dto.adoption.AdoptionPetDetailResponse;
import com.petplatform.dto.adoption.AdoptionPetSummaryResponse;
import com.petplatform.dto.adoption.AdoptionProcessResponse;
import com.petplatform.dto.community.PostAuthorResponse;
import com.petplatform.dto.community.PostCommentAuthorResponse;
import com.petplatform.dto.community.PostCommentResponse;
import com.petplatform.dto.community.PostDetailResponse;
import com.petplatform.dto.message.MessageResponse;
import com.petplatform.dto.pet.PetAlbumResponse;
import com.petplatform.dto.pet.PetDetailResponse;
import com.petplatform.dto.pet.PetProfileResponse;
import com.petplatform.dto.pet.PetVaccineResponse;
import com.petplatform.dto.pet.PetWeightResponse;
import com.petplatform.dto.service.MerchantDetailResponse;
import com.petplatform.dto.service.MerchantReviewResponse;
import com.petplatform.dto.service.MerchantServiceResponse;
import com.petplatform.dto.service.MerchantSummaryResponse;
import com.petplatform.dto.service.ServiceCategoryResponse;
import com.petplatform.dto.shop.OrderDetailResponse;
import com.petplatform.dto.shop.OrderItemResponse;
import com.petplatform.dto.shop.ProductCategoryResponse;
import com.petplatform.dto.shop.ProductDetailResponse;
import com.petplatform.mapper.UserMapper;
import com.petplatform.security.JwtAuthenticationFilter;
import com.petplatform.security.JwtTokenProvider;
import com.petplatform.security.RestAccessDeniedHandler;
import com.petplatform.security.RestAuthenticationEntryPoint;
import com.petplatform.service.AdoptionService;
import com.petplatform.service.CommunityService;
import com.petplatform.service.MessageService;
import com.petplatform.service.PetService;
import com.petplatform.service.ServiceBookingService;
import com.petplatform.service.ShopService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = RemainingUserQueryWebMvcTest.TestApplication.class)
@AutoConfigureMockMvc
class RemainingUserQueryWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CommunityService communityService;

    @MockitoBean
    private AdoptionService adoptionService;

    @MockitoBean
    private ServiceBookingService serviceBookingService;

    @MockitoBean
    private ShopService shopService;

    @MockitoBean
    private PetService petService;

    @MockitoBean
    private MessageService messageService;

    @MockitoBean
    @SuppressWarnings("unused")
    private JwtTokenProvider jwtTokenProvider;

    @MockitoBean
    @SuppressWarnings("unused")
    private UserMapper userMapper;

    @SpringBootConfiguration
    @EnableAutoConfiguration
    @Import({
            CommunityController.class,
            AdoptionController.class,
            ServiceController.class,
            ShopController.class,
            PetController.class,
            MessageController.class,
            SecurityConfig.class,
            JwtAuthenticationFilter.class,
            RestAuthenticationEntryPoint.class,
            RestAccessDeniedHandler.class,
            GlobalExceptionHandler.class
    })
    static class TestApplication {
    }

    @Test
    @DisplayName("community post detail should return author and flags")
    void communityPostDetailShouldReturnPayload() throws Exception {
        when(communityService.getPostDetail(1L)).thenReturn(new PostDetailResponse(
                1L,
                "Spring Grooming Tips",
                "Keep pets warm after bathing.",
                List.of("/uploads/post-1.png"),
                "CARE",
                List.of("NEWBIE", "HEALTH"),
                new PostAuthorResponse(8L, "Mimi", "/uploads/avatar.png"),
                12,
                4,
                3,
                true,
                false,
                LocalDateTime.of(2026, 3, 20, 9, 0)
        ));

        mockMvc.perform(get("/api/v1/community/posts/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.title").value("Spring Grooming Tips"))
                .andExpect(jsonPath("$.data.author.nickname").value("Mimi"))
                .andExpect(jsonPath("$.data.is_liked").value(true));
    }

    @Test
    @DisplayName("community comments should return page result")
    void communityCommentsShouldReturnPageResult() throws Exception {
        when(communityService.getCommentPage(1L, 1, 10)).thenReturn(new PageResponse<>(
                List.of(new PostCommentResponse(
                        11L,
                        "Very practical checklist.",
                        new PostCommentAuthorResponse(3L, "Pudding", "/uploads/u3.png"),
                        LocalDateTime.of(2026, 3, 20, 10, 0)
                )),
                1,
                1,
                10
        ));

        mockMvc.perform(get("/api/v1/community/posts/1/comments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.list[0].content").value("Very practical checklist."))
                .andExpect(jsonPath("$.data.list[0].author.nickname").value("Pudding"));
    }

    @Test
    @DisplayName("adoption detail should return pet information")
    void adoptionPetDetailShouldReturnPayload() throws Exception {
        when(adoptionService.getPetDetail(5L)).thenReturn(new AdoptionPetDetailResponse(
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
        ));

        mockMvc.perform(get("/api/v1/adoption/pets/5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.name").value("Dumpling"))
                .andExpect(jsonPath("$.data.adoption_requirements").value("Windows should be secured"));
    }

    @Test
    @DisplayName("adoption process should return steps and notes")
    void adoptionProcessShouldReturnSteps() throws Exception {
        when(adoptionService.getProcess()).thenReturn(new AdoptionProcessResponse(
                List.of("Submit application", "Interview", "Home visit", "Take pet home"),
                List.of("Prepare ID", "Confirm family consent")
        ));

        mockMvc.perform(get("/api/v1/adoption/process"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.steps[0]").value("Submit application"))
                .andExpect(jsonPath("$.data.notes[1]").value("Confirm family consent"));
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("my adoption applications should return paged result")
    void myApplicationsShouldReturnPageResult() throws Exception {
        when(adoptionService.getMyApplications("PENDING", 1, 10)).thenReturn(new PageResponse<>(
                List.of(new AdoptionApplicationSummaryResponse(
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
                        "PENDING",
                        null,
                        LocalDateTime.of(2026, 3, 20, 11, 0)
                )),
                1,
                1,
                10
        ));

        mockMvc.perform(get("/api/v1/adoption/applications")
                        .param("status", "PENDING"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.list[0].pet.name").value("Dumpling"))
                .andExpect(jsonPath("$.data.list[0].status").value("PENDING"));
    }

    @Test
    @DisplayName("service categories should return active categories")
    void serviceCategoriesShouldReturnList() throws Exception {
        when(serviceBookingService.getCategories()).thenReturn(List.of(
                new ServiceCategoryResponse(1L, "Grooming", 1, "ACTIVE"),
                new ServiceCategoryResponse(2L, "Checkup", 2, "ACTIVE")
        ));

        mockMvc.perform(get("/api/v1/services/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data[0].name").value("Grooming"))
                .andExpect(jsonPath("$.data[1].name").value("Checkup"));
    }

    @Test
    @DisplayName("merchant list should return page result")
    void merchantListShouldReturnPageResult() throws Exception {
        when(serviceBookingService.getMerchantPage("Grooming", "Pudong", "score", 1, 10)).thenReturn(new PageResponse<>(
                List.of(new MerchantSummaryResponse(
                        1L,
                        "Warm Paw House",
                        "Pudong",
                        "188 Jinxiu Road",
                        new BigDecimal("4.8"),
                        "10:00-20:00",
                        "ACTIVE"
                )),
                1,
                1,
                10
        ));

        mockMvc.perform(get("/api/v1/services/merchants")
                        .param("category", "Grooming")
                        .param("district", "Pudong")
                        .param("sort", "score"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.list[0].name").value("Warm Paw House"))
                .andExpect(jsonPath("$.data.list[0].score").value(4.8));
    }

    @Test
    @DisplayName("merchant detail should include services and reviews")
    void merchantDetailShouldReturnServicesAndReviews() throws Exception {
        when(serviceBookingService.getMerchantDetail(1L)).thenReturn(new MerchantDetailResponse(
                1L,
                "Warm Paw House",
                "Pudong",
                "188 Jinxiu Road",
                "13800000000",
                "10:00-20:00",
                new BigDecimal("4.8"),
                List.of(new MerchantServiceResponse(3L, 1L, "Basic Grooming", new BigDecimal("88.00"), 60, "ACTIVE")),
                List.of(new MerchantReviewResponse(
                        7L,
                        5,
                        "Friendly and patient.",
                        new PostAuthorResponse(2L, "Cookie", "/uploads/u2.png"),
                        LocalDateTime.of(2026, 3, 20, 8, 0)
                ))
        ));

        mockMvc.perform(get("/api/v1/services/merchants/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.services[0].name").value("Basic Grooming"))
                .andExpect(jsonPath("$.data.reviews[0].author.nickname").value("Cookie"));
    }

    @Test
    @DisplayName("shop categories should return category list")
    void productCategoriesShouldReturnList() throws Exception {
        when(shopService.getCategories()).thenReturn(List.of(
                new ProductCategoryResponse(1L, "Cat Food", "CAT", 1, "ACTIVE"),
                new ProductCategoryResponse(2L, "Dog Snacks", "DOG", 2, "ACTIVE")
        ));

        mockMvc.perform(get("/api/v1/shop/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data[0].name").value("Cat Food"))
                .andExpect(jsonPath("$.data[1].pet_type").value("DOG"));
    }

    @Test
    @DisplayName("product detail should return images and stock")
    void productDetailShouldReturnPayload() throws Exception {
        when(shopService.getProductDetail(10L)).thenReturn(new ProductDetailResponse(
                10L,
                1L,
                "Freeze-Dried Chicken",
                "High protein formula",
                "/uploads/product-10.png",
                List.of("/uploads/product-10.png"),
                new BigDecimal("59.90"),
                20,
                "CAT",
                "Suitable for kittens and adult cats.",
                "ON_SALE"
        ));

        mockMvc.perform(get("/api/v1/shop/products/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.name").value("Freeze-Dried Chicken"))
                .andExpect(jsonPath("$.data.images[0]").value("/uploads/product-10.png"));
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("order detail should return items and receiver info")
    void orderDetailShouldReturnPayload() throws Exception {
        when(shopService.getOrderDetail(100L)).thenReturn(new OrderDetailResponse(
                100L,
                "PSP20260320010101ABCD1234",
                "PENDING",
                new BigDecimal("119.80"),
                new BigDecimal("119.80"),
                "Alice",
                "13800000001",
                "188 Example Road",
                List.of(new OrderItemResponse(
                        1L,
                        10L,
                        "Freeze-Dried Chicken",
                        "/uploads/product-10.png",
                        new BigDecimal("59.90"),
                        2,
                        new BigDecimal("119.80")
                )),
                LocalDateTime.of(2026, 3, 20, 12, 0)
        ));

        mockMvc.perform(get("/api/v1/shop/orders/100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.order_no").value("PSP20260320010101ABCD1234"))
                .andExpect(jsonPath("$.data.items[0].product_name").value("Freeze-Dried Chicken"));
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("pets list should return current user pets")
    void petsShouldReturnCurrentUserPets() throws Exception {
        when(petService.getMyPets()).thenReturn(List.of(
                new PetProfileResponse(
                        1L,
                        "Mochi",
                        "CAT",
                        "Ragdoll",
                        "FEMALE",
                        LocalDate.of(2024, 4, 1),
                        new BigDecimal("3.20"),
                        "/uploads/pet-1.png",
                        "Likes sleeping on the sofa"
                )
        ));

        mockMvc.perform(get("/api/v1/pets"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data[0].name").value("Mochi"))
                .andExpect(jsonPath("$.data[0].avatar_url").value("/uploads/pet-1.png"));
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("pet detail should return profile and record lists")
    void petDetailShouldReturnProfileAndLists() throws Exception {
        when(petService.getPetDetail(1L)).thenReturn(new PetDetailResponse(
                new PetProfileResponse(
                        1L,
                        "Mochi",
                        "CAT",
                        "Ragdoll",
                        "FEMALE",
                        LocalDate.of(2024, 4, 1),
                        new BigDecimal("3.20"),
                        "/uploads/pet-1.png",
                        "Likes sleeping on the sofa"
                ),
                List.of(new PetVaccineResponse(1L, "Rabies", LocalDate.of(2026, 3, 1), LocalDate.of(2027, 3, 1), "Annual shot")),
                List.of(new PetWeightResponse(1L, new BigDecimal("3.20"), LocalDateTime.of(2026, 3, 20, 9, 0))),
                List.of(new PetAlbumResponse(1L, "/uploads/pet-album-1.png", "First bath"))
        ));

        mockMvc.perform(get("/api/v1/pets/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.pet.name").value("Mochi"))
                .andExpect(jsonPath("$.data.vaccines[0].vaccine_name").value("Rabies"))
                .andExpect(jsonPath("$.data.albums[0].caption").value("First bath"));
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("pet vaccines should return vaccine list")
    void petVaccinesShouldReturnList() throws Exception {
        when(petService.getVaccines(1L)).thenReturn(List.of(
                new PetVaccineResponse(1L, "Rabies", LocalDate.of(2026, 3, 1), LocalDate.of(2027, 3, 1), "Annual shot")
        ));

        mockMvc.perform(get("/api/v1/pets/1/vaccines"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data[0].vaccine_name").value("Rabies"));
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("pet weights should return weight history")
    void petWeightsShouldReturnList() throws Exception {
        when(petService.getWeights(1L)).thenReturn(List.of(
                new PetWeightResponse(1L, new BigDecimal("3.20"), LocalDateTime.of(2026, 3, 20, 9, 0))
        ));

        mockMvc.perform(get("/api/v1/pets/1/weights"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data[0].weight").value(3.20));
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("messages should return paged list")
    void messagesShouldReturnPageResult() throws Exception {
        when(messageService.getMessages(1, 10)).thenReturn(new PageResponse<>(
                List.of(new MessageResponse(
                        1L,
                        "SYSTEM",
                        "Application updated",
                        "Your adoption application is under review.",
                        false,
                        LocalDateTime.of(2026, 3, 20, 13, 0)
                )),
                1,
                1,
                10
        ));

        mockMvc.perform(get("/api/v1/messages"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.list[0].title").value("Application updated"))
                .andExpect(jsonPath("$.data.list[0].is_read").value(false));
    }
}
