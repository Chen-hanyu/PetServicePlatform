package com.petplatform.service;

import com.petplatform.common.ResultCode;
import com.petplatform.common.exception.BusinessException;
import com.petplatform.dto.service.CreateMerchantReviewRequest;
import com.petplatform.dto.service.CreateMerchantReviewResponse;
import com.petplatform.dto.service.CreateServiceBookingRequest;
import com.petplatform.entity.Merchant;
import com.petplatform.entity.MerchantReview;
import com.petplatform.entity.MerchantService;
import com.petplatform.mapper.MerchantMapper;
import com.petplatform.mapper.MerchantReviewMapper;
import com.petplatform.mapper.MerchantServiceMapper;
import com.petplatform.mapper.ServiceBookingMapper;
import com.petplatform.mapper.ServiceCategoryMapper;
import com.petplatform.mapper.UserMapper;
import com.petplatform.security.CurrentUser;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ServiceBookingServiceTest {

    @Mock
    private ServiceCategoryMapper serviceCategoryMapper;

    @Mock
    private MerchantMapper merchantMapper;

    @Mock
    private MerchantServiceMapper merchantServiceMapper;

    @Mock
    private ServiceBookingMapper serviceBookingMapper;

    @Mock
    private MerchantReviewMapper merchantReviewMapper;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private ServiceBookingService serviceBookingService;

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("预约时间冲突时应拒绝创建预约")
    void shouldRejectBookingWhenTimeConflicts() {
        mockCurrentUser(10L);
        when(merchantMapper.selectById(1L)).thenReturn(activeMerchant(1L));
        when(merchantServiceMapper.selectById(2L)).thenReturn(activeMerchantService(2L, 1L));
        when(serviceBookingMapper.selectCount(any())).thenReturn(1L);

        CreateServiceBookingRequest request = new CreateServiceBookingRequest(
                1L,
                2L,
                LocalDateTime.of(2026, 3, 20, 10, 0),
                "张三",
                "13800000000",
                "洗护预约"
        );

        assertThatThrownBy(() -> serviceBookingService.createBooking(request))
                .isInstanceOf(BusinessException.class)
                .satisfies(exception -> assertThat(((BusinessException) exception).getCode())
                        .isEqualTo(ResultCode.BOOKING_TIME_CONFLICT.getCode()));
    }

    @Test
    @DisplayName("未完成服务时不允许评价商家")
    void shouldRejectReviewWhenBookingNotCompleted() {
        mockCurrentUser(10L);
        when(merchantMapper.selectById(1L)).thenReturn(activeMerchant(1L));
        when(serviceBookingMapper.selectCount(any())).thenReturn(0L);

        CreateMerchantReviewRequest request = new CreateMerchantReviewRequest(5, "服务很好");

        assertThatThrownBy(() -> serviceBookingService.createReview(1L, request))
                .isInstanceOf(BusinessException.class)
                .satisfies(exception -> assertThat(((BusinessException) exception).getCode())
                        .isEqualTo(ResultCode.INVALID_OPERATION.getCode()));
    }

    @Test
    @DisplayName("评价商家成功后应写入评价并重算平均分")
    void shouldCreateReviewAndRecalculateMerchantScore() {
        mockCurrentUser(10L);
        Merchant merchant = activeMerchant(1L);
        when(merchantMapper.selectById(1L)).thenReturn(merchant);
        when(serviceBookingMapper.selectCount(any())).thenReturn(1L);
        when(merchantReviewMapper.selectCount(any())).thenReturn(0L);
        doAnswer(invocation -> {
            MerchantReview review = invocation.getArgument(0);
            review.setId(12L);
            return 1;
        }).when(merchantReviewMapper).insert(any(MerchantReview.class));
        when(merchantReviewMapper.selectList(any())).thenReturn(List.of(
                buildReview(1L, 10L, 5, "非常专业"),
                buildReview(1L, 11L, 4, "体验不错")
        ));

        CreateMerchantReviewResponse response = serviceBookingService.createReview(
                1L,
                new CreateMerchantReviewRequest(5, "  非常专业  ")
        );

        ArgumentCaptor<MerchantReview> reviewCaptor = ArgumentCaptor.forClass(MerchantReview.class);
        verify(merchantReviewMapper).insert(reviewCaptor.capture());
        assertThat(reviewCaptor.getValue().getContent()).isEqualTo("非常专业");
        assertThat(response.id()).isEqualTo(12L);
        assertThat(response.score()).isEqualTo(5);
        assertThat(response.merchantScore()).isEqualByComparingTo("4.5");
        assertThat(merchant.getScore()).isEqualByComparingTo(new BigDecimal("4.5"));
        verify(merchantMapper).updateById(merchant);
    }

    private void mockCurrentUser(Long userId) {
        CurrentUser currentUser = new CurrentUser(userId, "USER", "13800000000");
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(currentUser, null, currentUser.authorities())
        );
    }

    private Merchant activeMerchant(Long id) {
        Merchant merchant = new Merchant();
        merchant.setId(id);
        merchant.setName("安心宠护");
        merchant.setStatus("ACTIVE");
        merchant.setScore(BigDecimal.ZERO);
        return merchant;
    }

    private MerchantService activeMerchantService(Long id, Long merchantId) {
        MerchantService merchantService = new MerchantService();
        merchantService.setId(id);
        merchantService.setMerchantId(merchantId);
        merchantService.setName("基础洗护");
        merchantService.setStatus("ACTIVE");
        return merchantService;
    }

    private MerchantReview buildReview(Long merchantId, Long userId, Integer score, String content) {
        MerchantReview review = new MerchantReview();
        review.setMerchantId(merchantId);
        review.setUserId(userId);
        review.setScore(score);
        review.setContent(content);
        return review;
    }
}
