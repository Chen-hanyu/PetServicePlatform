package com.petplatform.controller;

import com.petplatform.admin.controller.AdminAdoptionController;
import com.petplatform.admin.controller.AdminCommentController;
import com.petplatform.admin.controller.AdminMerchantReviewController;
import com.petplatform.admin.controller.AdminOrderController;
import com.petplatform.admin.controller.AdminPostController;
import com.petplatform.admin.controller.AdminServiceBookingController;
import com.petplatform.admin.controller.AdminUserController;
import com.petplatform.common.exception.GlobalExceptionHandler;
import com.petplatform.config.SecurityConfig;
import com.petplatform.dto.admin.PostReviewResponse;
import com.petplatform.dto.admin.ReviewAdoptionApplicationResponse;
import com.petplatform.dto.admin.UpdateOrderResponse;
import com.petplatform.dto.admin.UpdateServiceBookingResponse;
import com.petplatform.dto.admin.UpdateUserStatusResponse;
import com.petplatform.mapper.UserMapper;
import com.petplatform.security.JwtAuthenticationFilter;
import com.petplatform.security.JwtTokenProvider;
import com.petplatform.security.RestAccessDeniedHandler;
import com.petplatform.security.RestAuthenticationEntryPoint;
import com.petplatform.service.AdminAdoptionService;
import com.petplatform.service.AdminCommentService;
import com.petplatform.service.AdminCommunityService;
import com.petplatform.service.AdminMerchantReviewService;
import com.petplatform.service.AdminOrderService;
import com.petplatform.service.AdminServiceBookingService;
import com.petplatform.service.AdminUserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = AdminWriteCoreWebMvcTest.TestApplication.class)
@AutoConfigureMockMvc
class AdminWriteCoreWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AdminAdoptionService adminAdoptionService;

    @MockitoBean
    private AdminCommentService adminCommentService;

    @MockitoBean
    private AdminCommunityService adminCommunityService;

    @MockitoBean
    private AdminMerchantReviewService adminMerchantReviewService;

    @MockitoBean
    private AdminOrderService adminOrderService;

    @MockitoBean
    private AdminServiceBookingService adminServiceBookingService;

    @MockitoBean
    private AdminUserService adminUserService;

    @MockitoBean
    @SuppressWarnings("unused")
    private JwtTokenProvider jwtTokenProvider;

    @MockitoBean
    @SuppressWarnings("unused")
    private UserMapper userMapper;

    @SpringBootConfiguration
    @EnableAutoConfiguration
    @Import({
            AdminAdoptionController.class,
            AdminCommentController.class,
            AdminPostController.class,
            AdminMerchantReviewController.class,
            AdminOrderController.class,
            AdminServiceBookingController.class,
            AdminUserController.class,
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
    @DisplayName("review adoption application should return new state")
    void reviewAdoptionApplicationShouldReturnPayload() throws Exception {
        when(adminAdoptionService.reviewApplication(any(), any())).thenReturn(
                new ReviewAdoptionApplicationResponse(21L, "APPROVED", "Looks good")
        );

        mockMvc.perform(put("/api/v1/admin/adoption/applications/21/review")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "status": "APPROVED",
                                  "review_remark": "Looks good"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.status").value("APPROVED"))
                .andExpect(jsonPath("$.data.review_remark").value("Looks good"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("delete comment should return success wrapper")
    void deleteCommentShouldReturnSuccess() throws Exception {
        doNothing().when(adminCommentService).deleteComment(11L);

        mockMvc.perform(delete("/api/v1/admin/comments/11"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("review post should return new state")
    void reviewPostShouldReturnPayload() throws Exception {
        when(adminCommunityService.reviewPost(any(), any())).thenReturn(
                new PostReviewResponse(1L, "APPROVED", "Ready to publish")
        );

        mockMvc.perform(put("/api/v1/admin/posts/1/review")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "status": "APPROVED",
                                  "remark": "Ready to publish"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.status").value("APPROVED"))
                .andExpect(jsonPath("$.data.review_remark").value("Ready to publish"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("delete merchant review should return success wrapper")
    void deleteMerchantReviewShouldReturnSuccess() throws Exception {
        doNothing().when(adminMerchantReviewService).deleteReview(7L);

        mockMvc.perform(delete("/api/v1/admin/services/reviews/7"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("update order should return payload")
    void updateOrderShouldReturnPayload() throws Exception {
        when(adminOrderService.updateOrder(any(), any())).thenReturn(new UpdateOrderResponse(100L, "SHIPPED", "Express sent"));

        mockMvc.perform(put("/api/v1/admin/shop/orders/100")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "status": "SHIPPED",
                                  "remark": "Express sent"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.status").value("SHIPPED"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("update service booking should return payload")
    void updateServiceBookingShouldReturnPayload() throws Exception {
        when(adminServiceBookingService.updateBooking(any(), any())).thenReturn(
                new UpdateServiceBookingResponse(9L, "CONFIRMED", "See you tomorrow")
        );

        mockMvc.perform(put("/api/v1/admin/services/bookings/9")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "status": "CONFIRMED",
                                  "remark": "See you tomorrow"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.status").value("CONFIRMED"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("update user status should return payload")
    void updateUserStatusShouldReturnPayload() throws Exception {
        when(adminUserService.updateUserStatus(any(), any())).thenReturn(
                new UpdateUserStatusResponse(2L, "DISABLED", "Violation handled")
        );

        mockMvc.perform(put("/api/v1/admin/users/2/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "status": "DISABLED",
                                  "remark": "Violation handled"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.status").value("DISABLED"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("update user status should validate blank status")
    void updateUserStatusShouldValidateBlankStatus() throws Exception {
        mockMvc.perform(put("/api/v1/admin/users/2/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "status": "",
                                  "remark": "Violation handled"
                                }
                                """))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.code").value(10002));
    }
}
