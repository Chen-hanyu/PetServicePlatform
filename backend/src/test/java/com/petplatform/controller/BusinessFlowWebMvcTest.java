package com.petplatform.controller;

import com.petplatform.common.PageResponse;
import com.petplatform.common.exception.GlobalExceptionHandler;
import com.petplatform.config.SecurityConfig;
import com.petplatform.dto.adoption.AdoptionPetSummaryResponse;
import com.petplatform.dto.community.CreatePostResponse;
import com.petplatform.dto.community.PostAuthorResponse;
import com.petplatform.dto.community.PostSummaryResponse;
import com.petplatform.dto.shop.OrderSummaryResponse;
import com.petplatform.dto.shop.ProductSummaryResponse;
import com.petplatform.entity.AdoptionPet;
import com.petplatform.entity.Product;
import com.petplatform.mapper.UserMapper;
import com.petplatform.security.JwtAuthenticationFilter;
import com.petplatform.security.JwtTokenProvider;
import com.petplatform.security.RestAccessDeniedHandler;
import com.petplatform.security.RestAuthenticationEntryPoint;
import com.petplatform.service.AdoptionService;
import com.petplatform.service.CommunityService;
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
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = BusinessFlowWebMvcTest.TestApplication.class)
@AutoConfigureMockMvc
class BusinessFlowWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CommunityService communityService;

    @MockBean
    private AdoptionService adoptionService;

    @MockBean
    private ShopService shopService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private UserMapper userMapper;

    @SpringBootConfiguration
    @EnableAutoConfiguration
    @Import({
            CommunityController.class,
            AdoptionController.class,
            ShopController.class,
            SecurityConfig.class,
            JwtAuthenticationFilter.class,
            RestAuthenticationEntryPoint.class,
            RestAccessDeniedHandler.class,
            GlobalExceptionHandler.class
    })
    static class TestApplication {
    }

    @Test
    @DisplayName("社区帖子列表应允许匿名访问")
    void postListShouldAllowAnonymousAccess() throws Exception {
        PageResponse<PostSummaryResponse> pageResponse = new PageResponse<>(
                List.of(new PostSummaryResponse(
                        1L,
                        "春日养宠日记",
                        "养宠经验",
                        "/uploads/post-cover.png",
                        "给猫咪换粮的经验总结",
                        "APPROVED",
                        12,
                        3,
                        4,
                        new PostAuthorResponse(8L, "阿橘", "/uploads/avatar.png"),
                        List.of("新手养宠"),
                        LocalDateTime.of(2026, 3, 20, 9, 0)
                )),
                1,
                1,
                10
        );
        when(communityService.getPostPage(null, null, null, 1, 10)).thenReturn(pageResponse);

        mockMvc.perform(get("/api/v1/community/posts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.list[0].title").value("春日养宠日记"))
                .andExpect(jsonPath("$.data.list[0].author.nickname").value("阿橘"));
    }

    @Test
    @DisplayName("社区发帖接口未登录应返回未授权")
    void createPostShouldRejectAnonymousUser() throws Exception {
        mockMvc.perform(post("/api/v1/community/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "title": "新的发帖",
                                  "content": "分享一下我的洗护经验",
                                  "category": "养宠经验",
                                  "images": ["/uploads/post.png"],
                                  "tag_ids": [1, 2]
                                }
                                """))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value(10004));
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("登录用户发帖应返回统一成功结构")
    void createPostShouldAllowAuthenticatedUser() throws Exception {
        when(communityService.createPost(any())).thenReturn(new CreatePostResponse(11L, "PENDING"));

        mockMvc.perform(post("/api/v1/community/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "title": "新的发帖",
                                  "content": "分享一下我的洗护经验",
                                  "category": "养宠经验",
                                  "images": ["/uploads/post.png"],
                                  "tag_ids": [1, 2]
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.id").value(11))
                .andExpect(jsonPath("$.data.status").value("PENDING"));

        verify(communityService).createPost(any());
    }

    @Test
    @DisplayName("领养宠物列表应允许匿名访问")
    void adoptionPetListShouldAllowAnonymousAccess() throws Exception {
        AdoptionPet pet = new AdoptionPet();
        pet.setId(5L);
        pet.setName("团子");
        pet.setType("CAT");
        pet.setBreed("英短");
        pet.setGender("FEMALE");
        pet.setAgeDesc("2岁");
        pet.setCity("上海");
        pet.setHealthStatus("已绝育已免疫");
        pet.setStatus("ONLINE");
        pet.setCoverUrl("/uploads/adoption.png");
        when(adoptionService.getPetPage(null, null, null, 1, 10))
                .thenReturn(new PageResponse<>(List.of(AdoptionPetSummaryResponse.from(pet)), 1, 1, 10));

        mockMvc.perform(get("/api/v1/adoption/pets"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.list[0].name").value("团子"));
    }

    @Test
    @DisplayName("领养申请接口未登录应返回未授权")
    void adoptionApplicationShouldRejectAnonymousUser() throws Exception {
        mockMvc.perform(post("/api/v1/adoption/applications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "pet_id": 5,
                                  "contact_phone": "13800000000",
                                  "experience_desc": "有两年养猫经验",
                                  "living_condition_desc": "独居，家中可封窗"
                                }
                                """))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value(10004));
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("领养申请参数非法时应返回校验错误")
    void adoptionApplicationShouldValidatePayload() throws Exception {
        mockMvc.perform(post("/api/v1/adoption/applications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "pet_id": 5,
                                  "contact_phone": "123",
                                  "experience_desc": "有两年养猫经验",
                                  "living_condition_desc": "独居，家中可封窗"
                                }
                                """))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.code").value(10002))
                .andExpect(jsonPath("$.message").value("手机号格式不正确"));
    }

    @Test
    @DisplayName("商品列表应允许匿名访问")
    void productListShouldAllowAnonymousAccess() throws Exception {
        Product product = new Product();
        product.setId(3L);
        product.setCategoryId(1L);
        product.setName("冻干猫粮");
        product.setSubtitle("高蛋白配方");
        product.setImageUrl("/uploads/product.png");
        product.setPrice(new BigDecimal("59.90"));
        product.setStock(20);
        product.setPetType("CAT");
        product.setStatus("ON_SALE");
        when(shopService.getProductPage(null, null, null, null, 1, 10))
                .thenReturn(new PageResponse<>(List.of(ProductSummaryResponse.from(product)), 1, 1, 10));

        mockMvc.perform(get("/api/v1/shop/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.list[0].name").value("冻干猫粮"))
                .andExpect(jsonPath("$.data.list[0].price").value(59.90));
    }

    @Test
    @DisplayName("下单接口未登录应返回未授权")
    void createOrderShouldRejectAnonymousUser() throws Exception {
        mockMvc.perform(post("/api/v1/shop/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "item_ids": [1, 2],
                                  "receiver_name": "张三",
                                  "receiver_phone": "13800000000",
                                  "receiver_address": "上海市徐汇区",
                                  "remark": "请尽快发货"
                                }
                                """))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value(10004));
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("登录用户下单应返回统一成功结构")
    void createOrderShouldAllowAuthenticatedUser() throws Exception {
        when(shopService.createOrder(any())).thenReturn(new OrderSummaryResponse(
                100L,
                "PSP20260320010101ABCD1234",
                new BigDecimal("119.80"),
                new BigDecimal("119.80"),
                "PENDING",
                LocalDateTime.of(2026, 3, 20, 1, 1, 1)
        ));

        mockMvc.perform(post("/api/v1/shop/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "item_ids": [1, 2],
                                  "receiver_name": "张三",
                                  "receiver_phone": "13800000000",
                                  "receiver_address": "上海市徐汇区",
                                  "remark": "请尽快发货"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.id").value(100))
                .andExpect(jsonPath("$.data.status").value("PENDING"));

        verify(shopService).createOrder(any());
    }
}
