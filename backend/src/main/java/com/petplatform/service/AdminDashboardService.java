package com.petplatform.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.petplatform.dto.admin.DashboardOverviewResponse;
import com.petplatform.entity.AdoptionApplication;
import com.petplatform.entity.CommunityPost;
import com.petplatform.entity.User;
import com.petplatform.mapper.AdoptionApplicationMapper;
import com.petplatform.mapper.CommunityPostMapper;
import com.petplatform.mapper.ServiceBookingMapper;
import com.petplatform.mapper.ShopOrderMapper;
import com.petplatform.mapper.UserMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminDashboardService {

    private final UserMapper userMapper;
    private final CommunityPostMapper communityPostMapper;
    private final ShopOrderMapper shopOrderMapper;
    private final ServiceBookingMapper serviceBookingMapper;
    private final AdoptionApplicationMapper adoptionApplicationMapper;

    public AdminDashboardService(
            UserMapper userMapper,
            CommunityPostMapper communityPostMapper,
            ShopOrderMapper shopOrderMapper,
            ServiceBookingMapper serviceBookingMapper,
            AdoptionApplicationMapper adoptionApplicationMapper
    ) {
        this.userMapper = userMapper;
        this.communityPostMapper = communityPostMapper;
        this.shopOrderMapper = shopOrderMapper;
        this.serviceBookingMapper = serviceBookingMapper;
        this.adoptionApplicationMapper = adoptionApplicationMapper;
    }

    public DashboardOverviewResponse getOverview() {
        return new DashboardOverviewResponse(
                userMapper.selectCount(new LambdaQueryWrapper<User>().eq(User::getRole, "USER")),
                communityPostMapper.selectCount(null),
                shopOrderMapper.selectCount(null),
                serviceBookingMapper.selectCount(null),
                communityPostMapper.selectCount(new LambdaQueryWrapper<CommunityPost>().eq(CommunityPost::getStatus, "PENDING")),
                adoptionApplicationMapper.selectCount(new LambdaQueryWrapper<AdoptionApplication>().eq(AdoptionApplication::getStatus, "PENDING")),
                List.of(),
                List.of()
        );
    }
}
