package com.petplatform.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.petplatform.common.ResultCode;
import com.petplatform.common.exception.BusinessException;
import com.petplatform.dto.service.CancelServiceBookingResponse;
import com.petplatform.dto.service.CreateMerchantReviewRequest;
import com.petplatform.dto.service.CreateMerchantReviewResponse;
import com.petplatform.dto.service.CreateServiceBookingRequest;
import com.petplatform.dto.service.CreateServiceBookingResponse;
import com.petplatform.dto.service.MerchantDetailResponse;
import com.petplatform.entity.Merchant;
import com.petplatform.entity.MerchantReview;
import com.petplatform.entity.MerchantService;
import com.petplatform.entity.ServiceBooking;
import com.petplatform.entity.ServiceCategory;
import com.petplatform.entity.User;
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
import org.springframework.dao.DuplicateKeyException;
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
    @DisplayName("服务分类、商家列表和商家详情应映射为前台响应")
    void shouldReturnCategoriesMerchantsAndMerchantDetail() {
        ServiceCategory category = new ServiceCategory();
        category.setId(1L);
        category.setName("洗护");
        category.setSort(1);
        category.setStatus("ACTIVE");
        when(serviceCategoryMapper.selectList(any())).thenReturn(List.of(category));

        Page<Merchant> merchantPage = new Page<>(1, 10);
        merchantPage.setRecords(List.of(activeMerchant(1L)));
        merchantPage.setTotal(1);
        when(merchantMapper.selectPage(any(), any())).thenReturn(merchantPage);

        MerchantService service = activeMerchantService(2L, 1L);
        service.setCategoryId(1L);
        service.setPrice(new BigDecimal("88.00"));
        service.setDurationMinutes(60);
        MerchantReview review = buildReview(1L, 10L, 5, "专业");
        review.setId(9L);
        when(merchantMapper.selectById(1L)).thenReturn(activeMerchant(1L));
        when(merchantServiceMapper.selectList(any())).thenReturn(List.of(service));
        when(merchantReviewMapper.selectList(any())).thenReturn(List.of(review));
        when(userMapper.selectByIds(any())).thenReturn(List.of(user(10L)));

        assertThat(serviceBookingService.getCategories())
                .extracting("name")
                .containsExactly("洗护");
        assertThat(serviceBookingService.getMerchantPage(null, null, "score_desc", 1, 10).list())
                .extracting("name")
                .containsExactly("安心宠护");
        MerchantDetailResponse detail = serviceBookingService.getMerchantDetail(1L);
        assertThat(detail.services()).extracting("name").containsExactly("基础洗护");
        assertThat(detail.reviews()).extracting("content").containsExactly("专业");
    }

    @Test
    @DisplayName("按不存在分类筛选商家时应返回空分页")
    void shouldReturnEmptyMerchantPageWhenCategoryMissing() {
        when(serviceCategoryMapper.selectOne(any())).thenReturn(null);

        assertThat(serviceBookingService.getMerchantPage("不存在", null, null, 1, 10).list()).isEmpty();
    }

    @Test
    @DisplayName("预约成功时应写入预约并返回待确认状态")
    void shouldCreateBookingSuccessfully() {
        mockCurrentUser(10L);
        when(merchantMapper.selectById(1L)).thenReturn(activeMerchant(1L));
        when(merchantServiceMapper.selectById(2L)).thenReturn(activeMerchantService(2L, 1L));
        when(merchantServiceMapper.lockById(2L)).thenReturn(2L);
        when(serviceBookingMapper.selectCount(any())).thenReturn(0L);
        doAnswer(invocation -> {
            ServiceBooking booking = invocation.getArgument(0);
            booking.setId(22L);
            return 1;
        }).when(serviceBookingMapper).insert(any(ServiceBooking.class));

        CreateServiceBookingResponse response = serviceBookingService.createBooking(new CreateServiceBookingRequest(
                1L,
                2L,
                LocalDateTime.of(2026, 3, 20, 10, 0),
                " 张三 ",
                "13800000000",
                "洗护预约"
        ));

        assertThat(response.id()).isEqualTo(22L);
        assertThat(response.status()).isEqualTo("PENDING");
    }

    @Test
    @DisplayName("我的预约列表和取消预约应返回当前用户数据")
    void shouldReturnAndCancelMyBookings() {
        mockCurrentUser(10L);
        ServiceBooking booking = booking(30L, 10L, "PENDING");
        Page<ServiceBooking> bookingPage = new Page<>(1, 10);
        bookingPage.setRecords(List.of(booking));
        bookingPage.setTotal(1);
        MerchantService service = activeMerchantService(2L, 1L);
        when(serviceBookingMapper.selectPage(any(), any())).thenReturn(bookingPage);
        when(merchantMapper.selectByIds(any())).thenReturn(List.of(activeMerchant(1L)));
        when(merchantServiceMapper.selectByIds(any())).thenReturn(List.of(service));
        when(serviceBookingMapper.selectById(30L)).thenReturn(booking);

        assertThat(serviceBookingService.getMyBookings("PENDING", 1, 10).list())
                .extracting("serviceName")
                .containsExactly("基础洗护");
        CancelServiceBookingResponse response = serviceBookingService.cancelBooking(30L);
        assertThat(response.status()).isEqualTo("CANCELLED");
        verify(serviceBookingMapper).updateById(booking);
    }

    @Test
    @DisplayName("已完成预约不允许取消")
    void shouldRejectCancelCompletedBooking() {
        mockCurrentUser(10L);
        when(serviceBookingMapper.selectById(30L)).thenReturn(booking(30L, 10L, "COMPLETED"));

        assertThatThrownBy(() -> serviceBookingService.cancelBooking(30L))
                .isInstanceOf(BusinessException.class)
                .satisfies(exception -> assertThat(((BusinessException) exception).getCode())
                        .isEqualTo(ResultCode.INVALID_OPERATION.getCode()));
    }

    @Test
    @DisplayName("预约时间冲突时应拒绝创建预约")
    void shouldRejectBookingWhenTimeConflicts() {
        mockCurrentUser(10L);
        when(merchantMapper.selectById(1L)).thenReturn(activeMerchant(1L));
        when(merchantServiceMapper.selectById(2L)).thenReturn(activeMerchantService(2L, 1L));
        when(merchantServiceMapper.lockById(2L)).thenReturn(2L);
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
        verify(merchantServiceMapper).lockById(2L);
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

    @Test
    @DisplayName("并发重复评价触发唯一约束时应返回重复提交错误")
    void shouldThrowDuplicateDataWhenInsertReviewViolatesUniqueConstraint() {
        mockCurrentUser(10L);
        when(merchantMapper.selectById(1L)).thenReturn(activeMerchant(1L));
        when(serviceBookingMapper.selectCount(any())).thenReturn(1L);
        when(merchantReviewMapper.selectCount(any())).thenReturn(0L);
        when(merchantReviewMapper.insert(any(MerchantReview.class))).thenThrow(new DuplicateKeyException("duplicate"));

        CreateMerchantReviewRequest request = new CreateMerchantReviewRequest(5, "服务很好");

        assertThatThrownBy(() -> serviceBookingService.createReview(1L, request))
                .isInstanceOf(BusinessException.class)
                .satisfies(exception -> assertThat(((BusinessException) exception).getCode())
                        .isEqualTo(ResultCode.DUPLICATE_DATA.getCode()));
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
        merchant.setDistrict("Pudong");
        merchant.setAddress("No.1");
        merchant.setPhone("13800000000");
        merchant.setBusinessHours("09:00-18:00");
        merchant.setStatus("ACTIVE");
        merchant.setScore(BigDecimal.ZERO);
        return merchant;
    }

    private MerchantService activeMerchantService(Long id, Long merchantId) {
        MerchantService merchantService = new MerchantService();
        merchantService.setId(id);
        merchantService.setMerchantId(merchantId);
        merchantService.setCategoryId(1L);
        merchantService.setName("基础洗护");
        merchantService.setPrice(new BigDecimal("88.00"));
        merchantService.setDurationMinutes(60);
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

    private ServiceBooking booking(Long id, Long userId, String status) {
        ServiceBooking booking = new ServiceBooking();
        booking.setId(id);
        booking.setUserId(userId);
        booking.setMerchantId(1L);
        booking.setMerchantServiceId(2L);
        booking.setBookingTime(LocalDateTime.of(2026, 3, 20, 10, 0));
        booking.setContactName("张三");
        booking.setContactPhone("13800000000");
        booking.setStatus(status);
        return booking;
    }

    private User user(Long id) {
        User user = new User();
        user.setId(id);
        user.setNickname("Alice");
        user.setAvatarUrl("/uploads/avatar.png");
        return user;
    }
}
