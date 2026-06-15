package com.petplatform.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.petplatform.dto.admin.DashboardOverviewResponse;
import com.petplatform.entity.AdoptionApplication;
import com.petplatform.entity.CommunityPost;
import com.petplatform.entity.ServiceBooking;
import com.petplatform.entity.ShopOrder;
import com.petplatform.entity.User;
import com.petplatform.mapper.AdoptionApplicationMapper;
import com.petplatform.mapper.CommunityPostMapper;
import com.petplatform.mapper.ServiceBookingMapper;
import com.petplatform.mapper.ShopOrderMapper;
import com.petplatform.mapper.UserMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

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
        List<DashboardOverviewResponse.TrendPoint> orderTrend = buildOrderTrend();
        List<DashboardOverviewResponse.TrendPoint> bookingTrend = buildBookingTrend();
        return new DashboardOverviewResponse(
                userMapper.selectCount(new LambdaQueryWrapper<User>().eq(User::getRole, "USER")),
                communityPostMapper.selectCount(null),
                shopOrderMapper.selectCount(null),
                serviceBookingMapper.selectCount(null),
                communityPostMapper.selectCount(new LambdaQueryWrapper<CommunityPost>().eq(CommunityPost::getStatus, "PENDING")),
                adoptionApplicationMapper.selectCount(new LambdaQueryWrapper<AdoptionApplication>().eq(AdoptionApplication::getStatus, "PENDING")),
                serviceBookingMapper.selectCount(new LambdaQueryWrapper<ServiceBooking>().eq(ServiceBooking::getStatus, "PENDING")),
                orderTrend,
                bookingTrend
        );
    }

    private List<DashboardOverviewResponse.TrendPoint> buildOrderTrend() {
        LocalDate startDate = LocalDate.now().minusDays(6);
        List<ShopOrder> orders = shopOrderMapper.selectList(new LambdaQueryWrapper<ShopOrder>()
                .ge(ShopOrder::getCreatedAt, startDate.atStartOfDay()));
        Map<LocalDate, Long> countByDate = orders.stream()
                .filter(order -> order.getCreatedAt() != null)
                .map(order -> order.getCreatedAt().toLocalDate())
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
        return buildTrendPoints(startDate, countByDate);
    }

    private List<DashboardOverviewResponse.TrendPoint> buildBookingTrend() {
        LocalDate startDate = LocalDate.now().minusDays(6);
        List<ServiceBooking> bookings = serviceBookingMapper.selectList(new LambdaQueryWrapper<ServiceBooking>()
                .ge(ServiceBooking::getCreatedAt, startDate.atStartOfDay()));
        Map<LocalDate, Long> countByDate = bookings.stream()
                .filter(booking -> booking.getCreatedAt() != null)
                .map(booking -> booking.getCreatedAt().toLocalDate())
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
        return buildTrendPoints(startDate, countByDate);
    }

    private List<DashboardOverviewResponse.TrendPoint> buildTrendPoints(LocalDate startDate, Map<LocalDate, Long> countByDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd");
        return startDate.datesUntil(LocalDate.now().plusDays(1))
                .map(date -> new DashboardOverviewResponse.TrendPoint(formatter.format(date), countByDate.getOrDefault(date, 0L)))
                .toList();
    }
}
