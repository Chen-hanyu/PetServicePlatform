package com.petplatform.controller;

import com.petplatform.common.PageResponse;
import com.petplatform.common.exception.GlobalExceptionHandler;
import com.petplatform.config.SecurityConfig;
import com.petplatform.dto.home.BannerResponse;
import com.petplatform.dto.home.HomeResponse;
import com.petplatform.dto.home.PetCardResponse;
import com.petplatform.dto.home.QuickEntryResponse;
import com.petplatform.dto.home.TipResponse;
import com.petplatform.dto.search.SearchResultResponse;
import com.petplatform.mapper.UserMapper;
import com.petplatform.security.JwtAuthenticationFilter;
import com.petplatform.security.JwtTokenProvider;
import com.petplatform.security.RestAccessDeniedHandler;
import com.petplatform.security.RestAuthenticationEntryPoint;
import com.petplatform.service.HomeService;
import com.petplatform.service.SearchService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = HomeSearchWebMvcTest.TestApplication.class)
@AutoConfigureMockMvc
class HomeSearchWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private HomeService homeService;

    @MockitoBean
    private SearchService searchService;

    @MockitoBean
    @SuppressWarnings("unused")
    private JwtTokenProvider jwtTokenProvider;

    @MockitoBean
    @SuppressWarnings("unused")
    private UserMapper userMapper;

    @SpringBootConfiguration
    @EnableAutoConfiguration
    @Import({
            HomeController.class,
            SearchController.class,
            SecurityConfig.class,
            JwtAuthenticationFilter.class,
            RestAuthenticationEntryPoint.class,
            RestAccessDeniedHandler.class,
            GlobalExceptionHandler.class
    })
    static class TestApplication {
    }

    @Test
    @DisplayName("首页接口应允许匿名访问并返回聚合数据")
    void homeShouldAllowAnonymousAccess() throws Exception {
        HomeResponse response = new HomeResponse(
                List.of(new BannerResponse(1L, "春季养宠指南", "/uploads/banner.png", "/community/posts/1")),
                List.of(new QuickEntryResponse("community", "社区", "/community")),
                List.of(),
                List.of(),
                List.of(),
                List.of(new TipResponse("春季护理", "注意驱虫与换毛护理")),
                List.of(new PetCardResponse("奶糖", "2岁 · 上海", "/uploads/pet-card.png"))
        );
        when(homeService.getHome()).thenReturn(response);

        mockMvc.perform(get("/api/v1/home"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.banners[0].title").value("春季养宠指南"))
                .andExpect(jsonPath("$.data.quick_entries[0].code").value("community"))
                .andExpect(jsonPath("$.data.tips[0].title").value("春季护理"))
                .andExpect(jsonPath("$.data.pet_cards[0].title").value("奶糖"));
    }

    @Test
    @DisplayName("搜索接口应允许匿名访问并返回分页结果")
    void searchShouldAllowAnonymousAccess() throws Exception {
        PageResponse<SearchResultResponse> response = new PageResponse<>(
                List.of(new SearchResultResponse("community", 1L, "幼猫疫苗时间表整理", "整理了常见接种时间", "/uploads/post-1.png", "APPROVED")),
                1,
                1,
                10
        );
        when(searchService.search("疫苗", "community", 1, 10)).thenReturn(response);

        mockMvc.perform(get("/api/v1/search")
                        .param("keyword", "疫苗")
                        .param("module", "community"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.total").value(1))
                .andExpect(jsonPath("$.data.list[0].module").value("community"))
                .andExpect(jsonPath("$.data.list[0].title").value("幼猫疫苗时间表整理"));
    }

    @Test
    @DisplayName("搜索关键词为空时应返回校验错误")
    void searchShouldValidateKeyword() throws Exception {
        mockMvc.perform(get("/api/v1/search")
                        .param("keyword", " "))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(10001));
    }

    @Test
    @DisplayName("搜索分页参数非法时应返回参数错误")
    void searchShouldValidatePageParameters() throws Exception {
        mockMvc.perform(get("/api/v1/search")
                        .param("keyword", "猫")
                        .param("page", "0"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(10001));
    }
}
