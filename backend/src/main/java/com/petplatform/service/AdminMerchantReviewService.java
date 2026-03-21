package com.petplatform.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.petplatform.common.PageResponse;
import com.petplatform.common.ResultCode;
import com.petplatform.common.exception.BusinessException;
import com.petplatform.dto.admin.AdminMerchantReviewResponse;
import com.petplatform.entity.Merchant;
import com.petplatform.entity.MerchantReview;
import com.petplatform.entity.User;
import com.petplatform.mapper.MerchantMapper;
import com.petplatform.mapper.MerchantReviewMapper;
import com.petplatform.mapper.UserMapper;
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
public class AdminMerchantReviewService {

    private final MerchantReviewMapper merchantReviewMapper;
    private final MerchantMapper merchantMapper;
    private final UserMapper userMapper;

    public AdminMerchantReviewService(
            MerchantReviewMapper merchantReviewMapper,
            MerchantMapper merchantMapper,
            UserMapper userMapper
    ) {
        this.merchantReviewMapper = merchantReviewMapper;
        this.merchantMapper = merchantMapper;
        this.userMapper = userMapper;
    }

    public PageResponse<AdminMerchantReviewResponse> getReviewPage(Long merchantId, String keyword, int page, int pageSize) {
        Page<MerchantReview> pager = new Page<>(page, pageSize);
        LambdaQueryWrapper<MerchantReview> queryWrapper = new LambdaQueryWrapper<MerchantReview>()
                .eq(merchantId != null, MerchantReview::getMerchantId, merchantId)
                .and(StringUtils.hasText(keyword), wrapper -> wrapper
                        .like(MerchantReview::getContent, keyword)
                        .or()
                        .inSql(MerchantReview::getUserId,
                                "select id from users where nickname like '%" + escapeSql(keyword) + "%' or phone like '%" + escapeSql(keyword) + "%'")
                        .or()
                        .inSql(MerchantReview::getMerchantId,
                                "select id from merchants where name like '%" + escapeSql(keyword) + "%'"))
                .orderByDesc(MerchantReview::getCreatedAt);

        IPage<MerchantReview> reviewPage = merchantReviewMapper.selectPage(pager, queryWrapper);
        Map<Long, Merchant> merchants = loadMerchants(reviewPage.getRecords().stream().map(MerchantReview::getMerchantId).toList());
        Map<Long, User> users = loadUsers(reviewPage.getRecords().stream().map(MerchantReview::getUserId).toList());

        List<AdminMerchantReviewResponse> list = reviewPage.getRecords().stream()
                .map(review -> {
                    Merchant merchant = merchants.get(review.getMerchantId());
                    User user = users.get(review.getUserId());
                    return new AdminMerchantReviewResponse(
                            review.getId(),
                            review.getMerchantId(),
                            merchant == null ? null : merchant.getName(),
                            review.getScore() == null ? 0 : review.getScore(),
                            review.getContent(),
                            new AdminMerchantReviewResponse.Author(
                                    user == null ? null : user.getId(),
                                    user == null ? null : user.getNickname(),
                                    user == null ? null : user.getPhone()
                            ),
                            review.getCreatedAt()
                    );
                })
                .toList();

        return new PageResponse<>(list, reviewPage.getTotal(), page, pageSize);
    }

    @Transactional
    public void deleteReview(Long reviewId) {
        MerchantReview review = merchantReviewMapper.selectById(reviewId);
        if (review == null) {
            throw new BusinessException(ResultCode.RESOURCE_NOT_FOUND, "商家评价不存在");
        }
        merchantReviewMapper.deleteById(reviewId);

        Merchant merchant = merchantMapper.selectById(review.getMerchantId());
        if (merchant != null) {
            merchant.setScore(recalculateMerchantScore(review.getMerchantId()));
            merchantMapper.updateById(merchant);
        }
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
        return merchantMapper.selectBatchIds(distinctIds).stream()
                .collect(Collectors.toMap(Merchant::getId, Function.identity()));
    }

    private Map<Long, User> loadUsers(List<Long> userIds) {
        List<Long> distinctIds = userIds.stream().filter(Objects::nonNull).distinct().toList();
        if (distinctIds.isEmpty()) {
            return Collections.emptyMap();
        }
        return userMapper.selectBatchIds(distinctIds).stream()
                .collect(Collectors.toMap(User::getId, Function.identity()));
    }

    private String escapeSql(String value) {
        return value.replace("\\", "\\\\").replace("'", "''");
    }
}
