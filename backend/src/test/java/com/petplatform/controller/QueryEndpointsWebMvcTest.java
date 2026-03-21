package com.petplatform.controller;

import com.petplatform.admin.controller.AdminCommentController;
import com.petplatform.admin.controller.AdminOrderController;
import com.petplatform.admin.controller.AdminServiceBookingController;
import com.petplatform.admin.controller.AdminUserController;
import com.petplatform.common.PageResponse;
import com.petplatform.common.exception.GlobalExceptionHandler;
import com.petplatform.config.SecurityConfig;
import com.petplatform.dto.admin.AdminCommentResponse;
import com.petplatform.dto.admin.AdminOrderResponse;
import com.petplatform.dto.admin.AdminServiceBookingResponse;
import com.petplatform.dto.profile.ProfileOverviewResponse;
import com.petplatform.dto.service.MerchantSummaryResponse;
import com.petplatform.dto.service.ServiceBookingSummaryResponse;
import com.petplatform.dto.shop.CartItemResponse;
import com.petplatform.dto.shop.CartResponse;
import com.petplatform.dto.shop.OrderSummaryResponse;
import com.petplatform.dto.user.UserProfileResponse;
import com.petplatform.mapper.UserMapper;
import com.petplatform.security.JwtAuthenticationFilter;
import com.petplatform.security.JwtTokenProvider;
import com.petplatform.security.RestAccessDeniedHandler;
import com.petplatform.security.RestAuthenticationEntryPoint;
import com.petplatform.service.AdminCommentService;
import com.petplatform.service.AdminOrderService;
import com.petplatform.service.AdminServiceBookingService;
import com.petplatform.service.AdminUserService;
import com.petplatform.service.ProfileService;
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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = QueryEndpointsWebMvcTest.TestApplication.class)
@AutoConfigureMockMvc
class QueryEndpointsWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProfileService profileService;

    @MockBean
    private ShopService shopService;

    @MockBean
    private ServiceBookingService serviceBookingService;

    @MockBean
    private AdminUserService adminUserService;

    @MockBean
    private AdminCommentService adminCommentService;

    @MockBean
    private AdminOrderService adminOrderService;

    @MockBean
    private AdminServiceBookingService adminServiceBookingService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private UserMapper userMapper;

    @SpringBootConfiguration
    @EnableAutoConfiguration
    @Import({
            ProfileController.class,
            ShopController.class,
            ServiceController.class,
            AdminUserController.class,
            AdminCommentController.class,
            AdminOrderController.class,
            AdminServiceBookingController.class,
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
    @DisplayName("个人资料接口应返回当前用户资料")
    void profileMeShouldReturnCurrentUserProfile() throws Exception {
        when(profileService.getCurrentUserProfile()).thenReturn(new UserProfileResponse(
                2L, "USER", "13800000001", "团子妈", "/uploads/avatar.png", "FEMALE", "两只猫的铲屎官", "ACTIVE"
        ));

        mockMvc.perform(get("/api/v1/profile/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.id").value(2))
                .andExpect(jsonPath("$.data.nickname").value("团子妈"));
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("个人中心概览接口应返回聚合统计")
    void profileOverviewShouldReturnAggregatedCounts() throws Exception {
        when(profileService.getCurrentUserOverview()).thenReturn(new ProfileOverviewResponse(
                new UserProfileResponse(2L, "USER", "13800000001", "团子妈", null, "FEMALE", "两只猫的铲屎官", "ACTIVE"),
                2, 3, 4, 1, 2, 1, 5
        ));

        mockMvc.perform(get("/api/v1/profile/overview"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.pet_count").value(2))
                .andExpect(jsonPath("$.data.unread_message_count").value(5));
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("购物车接口应返回商品列表和总金额")
    void cartShouldReturnItemsAndTotalAmount() throws Exception {
        when(shopService.getCart()).thenReturn(new CartResponse(
                List.of(new CartItemResponse(1L, 10L, "幼猫主粮", "高蛋白配方", "/uploads/product-1.png", new BigDecimal("49.90"), 2, true, new BigDecimal("99.80"))),
                new BigDecimal("99.80")
        ));

        mockMvc.perform(get("/api/v1/shop/cart"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.items[0].name").value("幼猫主粮"))
                .andExpect(jsonPath("$.data.total_amount").value(99.80));
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("我的订单列表接口应返回分页结果")
    void ordersShouldReturnPageResult() throws Exception {
        when(shopService.getOrderPage("PENDING", 1, 10)).thenReturn(new PageResponse<>(
                List.of(new OrderSummaryResponse(1L, "PSP20260320000001AAAA1111", new BigDecimal("99.80"), new BigDecimal("99.80"), "PENDING", LocalDateTime.of(2026, 3, 20, 10, 0))),
                1, 1, 10
        ));

        mockMvc.perform(get("/api/v1/shop/orders")
                        .param("status", "PENDING"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.total").value(1))
                .andExpect(jsonPath("$.data.list[0].order_no").value("PSP20260320000001AAAA1111"));
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("我的预约列表接口应返回分页结果")
    void bookingsShouldReturnPageResult() throws Exception {
        when(serviceBookingService.getMyBookings("CONFIRMED", 1, 10)).thenReturn(new PageResponse<>(
                List.of(new ServiceBookingSummaryResponse(
                        1L,
                        new MerchantSummaryResponse(1L, "毛孩子洗护屋", "浦东新区", "锦绣路 188 号", new BigDecimal("4.8"), "10:00-20:00", "ACTIVE"),
                        "基础洗护",
                        LocalDateTime.of(2026, 3, 21, 14, 0),
                        "CONFIRMED"
                )),
                1, 1, 10
        ));

        mockMvc.perform(get("/api/v1/services/bookings")
                        .param("status", "CONFIRMED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.list[0].service_name").value("基础洗护"))
                .andExpect(jsonPath("$.data.list[0].merchant.name").value("毛孩子洗护屋"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("后台用户列表接口应支持筛选参数并返回分页结果")
    void adminUsersShouldReturnPageResult() throws Exception {
        when(adminUserService.getUserPage("团子", "ACTIVE", 1, 10)).thenReturn(new PageResponse<>(
                List.of(new UserProfileResponse(2L, "USER", "13800000001", "团子妈", null, "FEMALE", "两只猫的铲屎官", "ACTIVE")),
                1, 1, 10
        ));

        mockMvc.perform(get("/api/v1/admin/users")
                        .param("keyword", "团子")
                        .param("status", "ACTIVE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.total").value(1))
                .andExpect(jsonPath("$.data.list[0].nickname").value("团子妈"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("后台评论列表接口应返回评论和作者信息")
    void adminCommentsShouldReturnPageResult() throws Exception {
        when(adminCommentService.getCommentPage("清楚", 1, 10)).thenReturn(new PageResponse<>(
                List.of(new AdminCommentResponse(
                        1L,
                        10L,
                        "幼猫疫苗时间表整理",
                        "这篇整理得很清楚",
                        new AdminCommentResponse.Author(3L, "柴犬研究员", "13800000002"),
                        "NORMAL",
                        LocalDateTime.of(2026, 3, 20, 9, 0)
                )),
                1, 1, 10
        ));

        mockMvc.perform(get("/api/v1/admin/comments")
                        .param("keyword", "清楚"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.list[0].post_title").value("幼猫疫苗时间表整理"))
                .andExpect(jsonPath("$.data.list[0].author.nickname").value("柴犬研究员"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("后台订单列表接口应返回用户和收货信息")
    void adminOrdersShouldReturnPageResult() throws Exception {
        when(adminOrderService.getOrderPage("SHIPPED", "PSP", 1, 10)).thenReturn(new PageResponse<>(
                List.of(new AdminOrderResponse(
                        1L,
                        "PSP20260320000001AAAA1111",
                        new AdminOrderResponse.UserProfileLite(2L, "团子妈", "13800000001"),
                        new BigDecimal("99.80"),
                        new BigDecimal("99.80"),
                        "SHIPPED",
                        "李女士",
                        "13800000001",
                        "上海市浦东新区示例路 188 号",
                        LocalDateTime.of(2026, 3, 20, 10, 0)
                )),
                1, 1, 10
        ));

        mockMvc.perform(get("/api/v1/admin/shop/orders")
                        .param("status", "SHIPPED")
                        .param("keyword", "PSP"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.list[0].user.nickname").value("团子妈"))
                .andExpect(jsonPath("$.data.list[0].receiver_name").value("李女士"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("后台服务预约列表接口应返回用户和商家信息")
    void adminServiceBookingsShouldReturnPageResult() throws Exception {
        when(adminServiceBookingService.getBookingPage("PENDING", 1L, 1, 10)).thenReturn(new PageResponse<>(
                List.of(new AdminServiceBookingResponse(
                        1L,
                        new UserProfileResponse(2L, "USER", "13800000001", "团子妈", null, "FEMALE", "两只猫的铲屎官", "ACTIVE"),
                        new MerchantSummaryResponse(1L, "毛孩子洗护屋", "浦东新区", "锦绣路 188 号", new BigDecimal("4.8"), "10:00-20:00", "ACTIVE"),
                        "基础洗护",
                        LocalDateTime.of(2026, 3, 21, 14, 0),
                        "李女士",
                        "13800000001",
                        "PENDING",
                        "想预约周末洗护",
                        LocalDateTime.of(2026, 3, 20, 9, 0)
                )),
                1, 1, 10
        ));

        mockMvc.perform(get("/api/v1/admin/services/bookings")
                        .param("status", "PENDING")
                        .param("merchant_id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.list[0].user.nickname").value("团子妈"))
                .andExpect(jsonPath("$.data.list[0].merchant.name").value("毛孩子洗护屋"))
                .andExpect(jsonPath("$.data.list[0].service_name").value("基础洗护"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("后台列表分页参数非法时应返回参数错误")
    void adminQueryEndpointsShouldValidatePageParameters() throws Exception {
        mockMvc.perform(get("/api/v1/admin/shop/orders")
                        .param("page", "0"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(10001));
    }
}
