package com.petplatform.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.petplatform.common.PageResponse;
import com.petplatform.common.ResultCode;
import com.petplatform.common.exception.BusinessException;
import com.petplatform.dto.admin.AdminUserDetailResponse;
import com.petplatform.dto.admin.UpdateUserStatusRequest;
import com.petplatform.dto.admin.UpdateUserStatusResponse;
import com.petplatform.dto.user.UserProfileResponse;
import com.petplatform.entity.AdoptionApplication;
import com.petplatform.entity.CommunityPost;
import com.petplatform.entity.Pet;
import com.petplatform.entity.ShopOrder;
import com.petplatform.entity.User;
import com.petplatform.mapper.AdoptionApplicationMapper;
import com.petplatform.mapper.CommunityPostMapper;
import com.petplatform.mapper.PetMapper;
import com.petplatform.mapper.ShopOrderMapper;
import com.petplatform.mapper.UserMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class AdminUserService {

    private final UserMapper userMapper;
    private final PetMapper petMapper;
    private final CommunityPostMapper communityPostMapper;
    private final ShopOrderMapper shopOrderMapper;
    private final AdoptionApplicationMapper adoptionApplicationMapper;

    public AdminUserService(
            UserMapper userMapper,
            PetMapper petMapper,
            CommunityPostMapper communityPostMapper,
            ShopOrderMapper shopOrderMapper,
            AdoptionApplicationMapper adoptionApplicationMapper
    ) {
        this.userMapper = userMapper;
        this.petMapper = petMapper;
        this.communityPostMapper = communityPostMapper;
        this.shopOrderMapper = shopOrderMapper;
        this.adoptionApplicationMapper = adoptionApplicationMapper;
    }

    public PageResponse<UserProfileResponse> getUserPage(String keyword, String status, int page, int pageSize) {
        Page<User> pager = new Page<>(page, pageSize);
        IPage<User> userPage = userMapper.selectPage(
                pager,
                new LambdaQueryWrapper<User>()
                        .eq(User::getRole, "USER")
                        .eq(StringUtils.hasText(status), User::getStatus, status)
                        .and(StringUtils.hasText(keyword), wrapper -> wrapper
                                .like(User::getPhone, keyword)
                                .or()
                                .like(User::getNickname, keyword))
                        .orderByDesc(User::getCreatedAt)
        );
        List<UserProfileResponse> list = userPage.getRecords().stream()
                .map(UserProfileResponse::from)
                .toList();
        return new PageResponse<>(list, userPage.getTotal(), page, pageSize);
    }

    public AdminUserDetailResponse getUserDetail(Long userId) {
        User user = getUserOrThrow(userId);
        return new AdminUserDetailResponse(
                UserProfileResponse.from(user),
                petMapper.selectCount(new LambdaQueryWrapper<Pet>().eq(Pet::getUserId, userId)),
                communityPostMapper.selectCount(new LambdaQueryWrapper<CommunityPost>().eq(CommunityPost::getUserId, userId)),
                shopOrderMapper.selectCount(new LambdaQueryWrapper<ShopOrder>().eq(ShopOrder::getUserId, userId)),
                adoptionApplicationMapper.selectCount(new LambdaQueryWrapper<AdoptionApplication>().eq(AdoptionApplication::getUserId, userId))
        );
    }

    @Transactional
    public UpdateUserStatusResponse updateUserStatus(Long userId, UpdateUserStatusRequest request) {
        User user = getUserOrThrow(userId);
        if (!"ACTIVE".equals(request.status()) && !"DISABLED".equals(request.status())) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "用户状态仅支持 ACTIVE 或 DISABLED");
        }
        user.setStatus(request.status());
        userMapper.updateById(user);
        return new UpdateUserStatusResponse(user.getId(), user.getStatus(), request.remark());
    }

    private User getUserOrThrow(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null || !"USER".equals(user.getRole())) {
            throw new BusinessException(ResultCode.RESOURCE_NOT_FOUND, "用户不存在");
        }
        return user;
    }
}
