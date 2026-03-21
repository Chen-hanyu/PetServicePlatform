package com.petplatform.controller;

import com.petplatform.common.exception.GlobalExceptionHandler;
import com.petplatform.config.SecurityConfig;
import com.petplatform.dto.community.CreateCommentResponse;
import com.petplatform.dto.community.ToggleFavoriteResponse;
import com.petplatform.dto.community.ToggleLikeResponse;
import com.petplatform.dto.message.MarkMessageReadResponse;
import com.petplatform.dto.pet.PetAlbumResponse;
import com.petplatform.dto.pet.PetProfileResponse;
import com.petplatform.dto.pet.PetVaccineResponse;
import com.petplatform.dto.pet.PetWeightResponse;
import com.petplatform.dto.service.CancelServiceBookingResponse;
import com.petplatform.dto.service.CreateMerchantReviewResponse;
import com.petplatform.dto.service.CreateServiceBookingResponse;
import com.petplatform.dto.shop.CartItemResponse;
import com.petplatform.dto.shop.CartResponse;
import com.petplatform.mapper.UserMapper;
import com.petplatform.security.JwtAuthenticationFilter;
import com.petplatform.security.JwtTokenProvider;
import com.petplatform.security.RestAccessDeniedHandler;
import com.petplatform.security.RestAuthenticationEntryPoint;
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
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = UserWriteEndpointsWebMvcTest.TestApplication.class)
@AutoConfigureMockMvc
class UserWriteEndpointsWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CommunityService communityService;

    @MockBean
    private ServiceBookingService serviceBookingService;

    @MockBean
    private ShopService shopService;

    @MockBean
    private PetService petService;

    @MockBean
    private MessageService messageService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private UserMapper userMapper;

    @SpringBootConfiguration
    @EnableAutoConfiguration
    @Import({
            CommunityController.class,
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
    @WithMockUser(roles = "USER")
    @DisplayName("create comment should return id")
    void createCommentShouldReturnId() throws Exception {
        when(communityService.createComment(any(), any())).thenReturn(new CreateCommentResponse(12L));

        mockMvc.perform(post("/api/v1/community/posts/1/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "content": "Helpful article."
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.id").value(12));
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("toggle like should return new state and count")
    void toggleLikeShouldReturnState() throws Exception {
        when(communityService.toggleLike(1L)).thenReturn(new ToggleLikeResponse(true, 13));

        mockMvc.perform(post("/api/v1/community/posts/1/like"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.liked").value(true))
                .andExpect(jsonPath("$.data.like_count").value(13));
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("toggle favorite should return new state and count")
    void toggleFavoriteShouldReturnState() throws Exception {
        when(communityService.toggleFavorite(1L)).thenReturn(new ToggleFavoriteResponse(true, 6));

        mockMvc.perform(post("/api/v1/community/posts/1/favorite"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.favorited").value(true))
                .andExpect(jsonPath("$.data.favorite_count").value(6));
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("create merchant review should return id and score")
    void createMerchantReviewShouldReturnPayload() throws Exception {
        when(serviceBookingService.createReview(any(), any())).thenReturn(
                new CreateMerchantReviewResponse(7L, 5, new BigDecimal("4.9"))
        );

        mockMvc.perform(post("/api/v1/services/merchants/1/reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "score": 5,
                                  "content": "Friendly and patient."
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.id").value(7))
                .andExpect(jsonPath("$.data.merchant_score").value(4.9));
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("create service booking should return id and status")
    void createServiceBookingShouldReturnPayload() throws Exception {
        when(serviceBookingService.createBooking(any())).thenReturn(new CreateServiceBookingResponse(9L, "PENDING"));

        mockMvc.perform(post("/api/v1/services/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "merchant_id": 1,
                                  "merchant_service_id": 3,
                                  "booking_time": "2026-03-22T14:00:00",
                                  "contact_name": "Alice",
                                  "contact_phone": "13800000001",
                                  "remark": "Please trim nails as well"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.id").value(9))
                .andExpect(jsonPath("$.data.status").value("PENDING"));
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("cancel service booking should return cancelled state")
    void cancelServiceBookingShouldReturnPayload() throws Exception {
        when(serviceBookingService.cancelBooking(9L)).thenReturn(new CancelServiceBookingResponse(9L, "CANCELLED"));

        mockMvc.perform(post("/api/v1/services/bookings/9/cancel"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.status").value("CANCELLED"));
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("add cart item should return refreshed cart")
    void addCartItemShouldReturnCart() throws Exception {
        when(shopService.addCartItem(any())).thenReturn(new CartResponse(
                List.of(new CartItemResponse(
                        1L,
                        10L,
                        "Freeze-Dried Chicken",
                        "High protein formula",
                        "/uploads/product-10.png",
                        new BigDecimal("59.90"),
                        2,
                        true,
                        new BigDecimal("119.80")
                )),
                new BigDecimal("119.80")
        ));

        mockMvc.perform(post("/api/v1/shop/cart/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "product_id": 10,
                                  "quantity": 2
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.items[0].product_id").value(10))
                .andExpect(jsonPath("$.data.total_amount").value(119.80));
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("update cart item should return refreshed cart")
    void updateCartItemShouldReturnCart() throws Exception {
        when(shopService.updateCartItem(any(), any())).thenReturn(new CartResponse(
                List.of(new CartItemResponse(
                        1L,
                        10L,
                        "Freeze-Dried Chicken",
                        "High protein formula",
                        "/uploads/product-10.png",
                        new BigDecimal("59.90"),
                        1,
                        false,
                        new BigDecimal("59.90")
                )),
                new BigDecimal("0.00")
        ));

        mockMvc.perform(put("/api/v1/shop/cart/items/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "quantity": 1,
                                  "checked": false
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.items[0].checked").value(false));
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("create pet should return pet profile")
    void createPetShouldReturnProfile() throws Exception {
        when(petService.createPet(any())).thenReturn(new PetProfileResponse(
                1L,
                "Mochi",
                "CAT",
                "Ragdoll",
                "FEMALE",
                LocalDate.of(2024, 4, 1),
                new BigDecimal("3.20"),
                "/uploads/pet-1.png",
                "Likes sleeping on the sofa"
        ));

        mockMvc.perform(post("/api/v1/pets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Mochi",
                                  "type": "CAT",
                                  "breed": "Ragdoll",
                                  "gender": "FEMALE",
                                  "birthday": "2024-04-01",
                                  "weight": 3.2,
                                  "avatar_url": "/uploads/pet-1.png",
                                  "description": "Likes sleeping on the sofa"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.name").value("Mochi"));
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("update pet should return updated profile")
    void updatePetShouldReturnProfile() throws Exception {
        when(petService.updatePet(any(), any())).thenReturn(new PetProfileResponse(
                1L,
                "Mochi",
                "CAT",
                "Ragdoll",
                "FEMALE",
                LocalDate.of(2024, 4, 1),
                new BigDecimal("3.40"),
                "/uploads/pet-1.png",
                "Updated profile"
        ));

        mockMvc.perform(put("/api/v1/pets/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Mochi",
                                  "type": "CAT",
                                  "breed": "Ragdoll",
                                  "gender": "FEMALE",
                                  "birthday": "2024-04-01",
                                  "weight": 3.4,
                                  "avatar_url": "/uploads/pet-1.png",
                                  "description": "Updated profile"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.weight").value(3.4));
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("create pet vaccine should return vaccine record")
    void createPetVaccineShouldReturnRecord() throws Exception {
        when(petService.createVaccine(any(), any())).thenReturn(new PetVaccineResponse(
                1L,
                "Rabies",
                LocalDate.of(2026, 3, 1),
                LocalDate.of(2027, 3, 1),
                "Annual shot"
        ));

        mockMvc.perform(post("/api/v1/pets/1/vaccines")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "vaccine_name": "Rabies",
                                  "vaccinated_at": "2026-03-01",
                                  "next_due_at": "2027-03-01",
                                  "remark": "Annual shot"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.vaccine_name").value("Rabies"));
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("create pet album should return album record")
    void createPetAlbumShouldReturnRecord() throws Exception {
        when(petService.createAlbum(any(), any())).thenReturn(new PetAlbumResponse(
                1L,
                "/uploads/pet-album-1.png",
                "First bath"
        ));

        mockMvc.perform(post("/api/v1/pets/1/albums")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "image_url": "/uploads/pet-album-1.png",
                                  "caption": "First bath"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.image_url").value("/uploads/pet-album-1.png"));
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("create pet weight should return weight record")
    void createPetWeightShouldReturnRecord() throws Exception {
        when(petService.createWeight(any(), any())).thenReturn(new PetWeightResponse(
                1L,
                new BigDecimal("3.30"),
                LocalDateTime.of(2026, 3, 20, 9, 0)
        ));

        mockMvc.perform(post("/api/v1/pets/1/weights")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "weight": 3.3,
                                  "recorded_at": "2026-03-20T09:00:00"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.weight").value(3.3));
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("mark message as read should return read state")
    void markMessageAsReadShouldReturnPayload() throws Exception {
        when(messageService.markAsRead(1L)).thenReturn(new MarkMessageReadResponse(1L, true));

        mockMvc.perform(post("/api/v1/messages/1/read"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.is_read").value(true));
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("add cart item should validate quantity")
    void addCartItemShouldValidateQuantity() throws Exception {
        mockMvc.perform(post("/api/v1/shop/cart/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "product_id": 10,
                                  "quantity": 0
                                }
                                """))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.code").value(10002));
    }
}
