package com.petplatform.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.petplatform.common.PageResponse;
import com.petplatform.common.ResultCode;
import com.petplatform.common.exception.BusinessException;
import com.petplatform.dto.community.PostAuthorResponse;
import com.petplatform.dto.service.CancelServiceBookingResponse;
import com.petplatform.dto.service.CreateMerchantReviewRequest;
import com.petplatform.dto.service.CreateMerchantReviewResponse;
import com.petplatform.dto.service.CreateServiceBookingRequest;
import com.petplatform.dto.service.CreateServiceBookingResponse;
import com.petplatform.dto.service.MerchantDetailResponse;
import com.petplatform.dto.service.MerchantReviewResponse;
import com.petplatform.dto.service.MerchantServiceResponse;
import com.petplatform.dto.service.MerchantSummaryResponse;
import com.petplatform.dto.service.ServiceBookingSummaryResponse;
import com.petplatform.dto.service.ServiceCategoryResponse;
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
import com.petplatform.security.SecurityUtils;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class ServiceBookingService {

    private final ServiceCategoryMapper serviceCategoryMapper;
    private final MerchantMapper merchantMapper;
    private final MerchantServiceMapper merchantServiceMapper;
    private final ServiceBookingMapper serviceBookingMapper;
    private final MerchantReviewMapper merchantReviewMapper;
    private final UserMapper userMapper;

    public ServiceBookingService(
            ServiceCategoryMapper serviceCategoryMapper,
            MerchantMapper merchantMapper,
            MerchantServiceMapper merchantServiceMapper,
            ServiceBookingMapper serviceBookingMapper,
            MerchantReviewMapper merchantReviewMapper,
            UserMapper userMapper
    ) {
        this.serviceCategoryMapper = serviceCategoryMapper;
        this.merchantMapper = merchantMapper;
        this.merchantServiceMapper = merchantServiceMapper;
        this.serviceBookingMapper = serviceBookingMapper;
        this.merchantReviewMapper = merchantReviewMapper;
        this.userMapper = userMapper;
    }

    public List<ServiceCategoryResponse> getCategories() {
        return serviceCategoryMapper.selectList(new LambdaQueryWrapper<ServiceCategory>()
                        .eq(ServiceCategory::getStatus, "ACTIVE")
                        .orderByAsc(ServiceCategory::getSort)
                        .orderByAsc(ServiceCategory::getId))
                .stream()
                .map(ServiceCategoryResponse::from)
                .toList();
    }

    public PageResponse<MerchantSummaryResponse> getMerchantPage(
            String category,
            String district,
            String sort,
            int page,
            int pageSize
    ) {
        Page<Merchant> pager = new Page<>(page, pageSize);
        LambdaQueryWrapper<Merchant> queryWrapper = new LambdaQueryWrapper<Merchant>()
                .eq(Merchant::getStatus, "ACTIVE")
                .eq(StringUtils.hasText(district), Merchant::getDistrict, district);

        if (StringUtils.hasText(category)) {
            ServiceCategory serviceCategory = serviceCategoryMapper.selectOne(new LambdaQueryWrapper<ServiceCategory>()
                    .eq(ServiceCategory::getName, category)
                    .eq(ServiceCategory::getStatus, "ACTIVE")
                    .last("limit 1"));
            if (serviceCategory == null) {
                return new PageResponse<>(List.of(), 0, page, pageSize);
            }
            queryWrapper.inSql(
                    Merchant::getId,
                    "select merchant_id from merchant_services where category_id = " + serviceCategory.getId() + " and status = 'ACTIVE'"
            );
        }

        if ("score_desc".equalsIgnoreCase(sort) || "distance_asc".equalsIgnoreCase(sort)) {
            queryWrapper.orderByDesc(Merchant::getScore);
        } else {
            queryWrapper.orderByDesc(Merchant::getId);
        }

        IPage<Merchant> merchantPage = merchantMapper.selectPage(pager, queryWrapper);
        List<MerchantSummaryResponse> list = merchantPage.getRecords().stream()
                .map(MerchantSummaryResponse::from)
                .toList();
        return new PageResponse<>(list, merchantPage.getTotal(), page, pageSize);
    }

    public MerchantDetailResponse getMerchantDetail(Long merchantId) {
        Merchant merchant = merchantMapper.selectById(merchantId);
        if (merchant == null || !"ACTIVE".equals(merchant.getStatus())) {
            throw new BusinessException(ResultCode.RESOURCE_NOT_FOUND, "商家不存在");
        }
        List<MerchantServiceResponse> services = merchantServiceMapper.selectList(new LambdaQueryWrapper<MerchantService>()
                        .eq(MerchantService::getMerchantId, merchantId)
                        .eq(MerchantService::getStatus, "ACTIVE")
                        .orderByAsc(MerchantService::getCategoryId)
                        .orderByAsc(MerchantService::getId))
                .stream()
                .map(MerchantServiceResponse::from)
                .toList();

        List<MerchantReview> reviewEntities = merchantReviewMapper.selectList(new LambdaQueryWrapper<MerchantReview>()
                        .eq(MerchantReview::getMerchantId, merchantId)
                        .orderByDesc(MerchantReview::getCreatedAt)
                        .last("limit 10"));
        Map<Long, User> users = loadUsers(reviewEntities.stream().map(MerchantReview::getUserId).toList());
        List<MerchantReviewResponse> reviews = reviewEntities.stream()
                .map(review -> new MerchantReviewResponse(
                        review.getId(),
                        review.getScore() == null ? 0 : review.getScore(),
                        review.getContent(),
                        PostAuthorResponse.from(users.get(review.getUserId())),
                        review.getCreatedAt()
                ))
                .toList();
        return MerchantDetailResponse.from(merchant, services, reviews);
    }

    @Transactional
    public CreateMerchantReviewResponse createReview(Long merchantId, CreateMerchantReviewRequest request) {
        Long userId = SecurityUtils.getCurrentUser().id();
        Merchant merchant = merchantMapper.selectById(merchantId);
        if (merchant == null || !"ACTIVE".equals(merchant.getStatus())) {
            throw new BusinessException(ResultCode.RESOURCE_NOT_FOUND, "商家不存在");
        }

        Long completedBookingCount = serviceBookingMapper.selectCount(new LambdaQueryWrapper<ServiceBooking>()
                .eq(ServiceBooking::getUserId, userId)
                .eq(ServiceBooking::getMerchantId, merchantId)
                .eq(ServiceBooking::getStatus, "COMPLETED"));
        if (completedBookingCount == null || completedBookingCount <= 0) {
            throw new BusinessException(ResultCode.INVALID_OPERATION, "完成服务后才可评价商家");
        }

        Long reviewCount = merchantReviewMapper.selectCount(new LambdaQueryWrapper<MerchantReview>()
                .eq(MerchantReview::getMerchantId, merchantId)
                .eq(MerchantReview::getUserId, userId));
        if (reviewCount != null && reviewCount > 0) {
            throw new BusinessException(ResultCode.DUPLICATE_DATA, "当前商家已评价，请勿重复提交");
        }

        MerchantReview review = new MerchantReview();
        review.setMerchantId(merchantId);
        review.setUserId(userId);
        review.setScore(request.score());
        review.setContent(request.content().trim());
        try {
            merchantReviewMapper.insert(review);
        } catch (DuplicateKeyException exception) {
            throw new BusinessException(ResultCode.DUPLICATE_DATA, "当前商家已评价，请勿重复提交");
        }

        BigDecimal merchantScore = recalculateMerchantScore(merchantId);
        merchant.setScore(merchantScore);
        merchantMapper.updateById(merchant);

        return new CreateMerchantReviewResponse(review.getId(), request.score(), merchantScore);
    }

    @Transactional
    public CreateServiceBookingResponse createBooking(CreateServiceBookingRequest request) {
        Long userId = SecurityUtils.getCurrentUser().id();
        Merchant merchant = merchantMapper.selectById(request.merchantId());
        if (merchant == null || !"ACTIVE".equals(merchant.getStatus())) {
            throw new BusinessException(ResultCode.RESOURCE_NOT_FOUND, "商家不存在");
        }
        MerchantService merchantService = merchantServiceMapper.selectById(request.merchantServiceId());
        if (merchantService == null
                || !"ACTIVE".equals(merchantService.getStatus())
                || !merchantService.getMerchantId().equals(request.merchantId())) {
            throw new BusinessException(ResultCode.RESOURCE_NOT_FOUND, "服务项目不存在");
        }

        // Serialize bookings on the same service item to avoid concurrent double-booking race.
        merchantServiceMapper.lockById(request.merchantServiceId());

        Long conflictCount = serviceBookingMapper.selectCount(new LambdaQueryWrapper<ServiceBooking>()
                .eq(ServiceBooking::getMerchantId, request.merchantId())
                .eq(ServiceBooking::getMerchantServiceId, request.merchantServiceId())
                .eq(ServiceBooking::getBookingTime, request.bookingTime())
                .in(ServiceBooking::getStatus, List.of("PENDING", "CONFIRMED")));
        if (conflictCount > 0) {
            throw new BusinessException(ResultCode.BOOKING_TIME_CONFLICT);
        }

        ServiceBooking booking = new ServiceBooking();
        booking.setUserId(userId);
        booking.setMerchantId(request.merchantId());
        booking.setMerchantServiceId(request.merchantServiceId());
        booking.setBookingTime(request.bookingTime());
        booking.setContactName(request.contactName().trim());
        booking.setContactPhone(request.contactPhone());
        booking.setStatus("PENDING");
        booking.setRemark(request.remark());
        serviceBookingMapper.insert(booking);

        return new CreateServiceBookingResponse(booking.getId(), booking.getStatus());
    }

    public PageResponse<ServiceBookingSummaryResponse> getMyBookings(String status, int page, int pageSize) {
        Long userId = SecurityUtils.getCurrentUser().id();
        Page<ServiceBooking> pager = new Page<>(page, pageSize);
        IPage<ServiceBooking> bookingPage = serviceBookingMapper.selectPage(
                pager,
                new LambdaQueryWrapper<ServiceBooking>()
                        .eq(ServiceBooking::getUserId, userId)
                        .eq(StringUtils.hasText(status), ServiceBooking::getStatus, status)
                        .orderByDesc(ServiceBooking::getCreatedAt)
        );

        Map<Long, Merchant> merchants = loadMerchants(bookingPage.getRecords().stream().map(ServiceBooking::getMerchantId).toList());
        Map<Long, MerchantService> services = loadMerchantServices(bookingPage.getRecords().stream().map(ServiceBooking::getMerchantServiceId).toList());

        List<ServiceBookingSummaryResponse> list = bookingPage.getRecords().stream()
                .map(booking -> new ServiceBookingSummaryResponse(
                        booking.getId(),
                        MerchantSummaryResponse.from(merchants.get(booking.getMerchantId())),
                        services.get(booking.getMerchantServiceId()).getName(),
                        booking.getBookingTime(),
                        booking.getStatus()
                ))
                .toList();

        return new PageResponse<>(list, bookingPage.getTotal(), page, pageSize);
    }

    @Transactional
    public CancelServiceBookingResponse cancelBooking(Long bookingId) {
        Long userId = SecurityUtils.getCurrentUser().id();
        ServiceBooking booking = serviceBookingMapper.selectById(bookingId);
        if (booking == null || !booking.getUserId().equals(userId)) {
            throw new BusinessException(ResultCode.RESOURCE_NOT_FOUND, "预约记录不存在");
        }
        if ("COMPLETED".equals(booking.getStatus()) || "CANCELLED".equals(booking.getStatus())) {
            throw new BusinessException(ResultCode.INVALID_OPERATION, "当前预约状态不支持取消");
        }
        booking.setStatus("CANCELLED");
        serviceBookingMapper.updateById(booking);
        return new CancelServiceBookingResponse(booking.getId(), booking.getStatus());
    }

    private BigDecimal recalculateMerchantScore(Long merchantId) {
        List<MerchantReview> reviews = merchantReviewMapper.selectList(new LambdaQueryWrapper<MerchantReview>()
                .eq(MerchantReview::getMerchantId, merchantId));
        if (reviews.isEmpty()) {
            return BigDecimal.ZERO.setScale(1, RoundingMode.HALF_UP);
        }
        BigDecimal total = reviews.stream()
                .map(review -> BigDecimal.valueOf(review.getScore()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return total.divide(BigDecimal.valueOf(reviews.size()), 1, RoundingMode.HALF_UP);
    }

    private Map<Long, Merchant> loadMerchants(List<Long> merchantIds) {
        List<Long> distinctIds = merchantIds.stream().filter(Objects::nonNull).distinct().toList();
        if (distinctIds.isEmpty()) {
            return Collections.emptyMap();
        }
        return merchantMapper.selectByIds(distinctIds).stream()
                .collect(Collectors.toMap(Merchant::getId, Function.identity()));
    }

    private Map<Long, MerchantService> loadMerchantServices(List<Long> serviceIds) {
        List<Long> distinctIds = serviceIds.stream().filter(Objects::nonNull).distinct().toList();
        if (distinctIds.isEmpty()) {
            return Collections.emptyMap();
        }
        return merchantServiceMapper.selectByIds(distinctIds).stream()
                .collect(Collectors.toMap(MerchantService::getId, Function.identity()));
    }

    private Map<Long, User> loadUsers(List<Long> userIds) {
        List<Long> distinctIds = userIds.stream().filter(Objects::nonNull).distinct().toList();
        if (distinctIds.isEmpty()) {
            return Collections.emptyMap();
        }
        return userMapper.selectByIds(distinctIds).stream()
                .collect(Collectors.toMap(User::getId, Function.identity()));
    }
}

