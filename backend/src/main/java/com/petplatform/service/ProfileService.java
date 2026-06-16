package com.petplatform.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.petplatform.dto.profile.ProfileOverviewResponse;
import com.petplatform.dto.user.UserProfileResponse;
import com.petplatform.entity.AdoptionApplication;
import com.petplatform.entity.CommunityPost;
import com.petplatform.entity.Message;
import com.petplatform.entity.Pet;
import com.petplatform.entity.PostFavorite;
import com.petplatform.entity.ServiceBooking;
import com.petplatform.entity.ShopOrder;
import com.petplatform.entity.User;
import com.petplatform.mapper.AdoptionApplicationMapper;
import com.petplatform.mapper.CommunityPostMapper;
import com.petplatform.mapper.MessageMapper;
import com.petplatform.mapper.PetMapper;
import com.petplatform.mapper.PostFavoriteMapper;
import com.petplatform.mapper.ServiceBookingMapper;
import com.petplatform.mapper.ShopOrderMapper;
import com.petplatform.security.SecurityUtils;
import org.springframework.stereotype.Service;

@Service
public class ProfileService {

    private final UserService userService;
    private final PetMapper petMapper;
    private final CommunityPostMapper communityPostMapper;
    private final PostFavoriteMapper postFavoriteMapper;
    private final ShopOrderMapper shopOrderMapper;
    private final ServiceBookingMapper serviceBookingMapper;
    private final AdoptionApplicationMapper adoptionApplicationMapper;
    private final MessageMapper messageMapper;

    public ProfileService(
            UserService userService,
            PetMapper petMapper,
            CommunityPostMapper communityPostMapper,
            PostFavoriteMapper postFavoriteMapper,
            ShopOrderMapper shopOrderMapper,
            ServiceBookingMapper serviceBookingMapper,
            AdoptionApplicationMapper adoptionApplicationMapper,
            MessageMapper messageMapper
    ) {
        this.userService = userService;
        this.petMapper = petMapper;
        this.communityPostMapper = communityPostMapper;
        this.postFavoriteMapper = postFavoriteMapper;
        this.shopOrderMapper = shopOrderMapper;
        this.serviceBookingMapper = serviceBookingMapper;
        this.adoptionApplicationMapper = adoptionApplicationMapper;
        this.messageMapper = messageMapper;
    }

    public UserProfileResponse getCurrentUserProfile() {
        User user = userService.getByIdOrThrow(SecurityUtils.getCurrentUser().id());
        return userService.toUserProfile(user);
    }

    public ProfileOverviewResponse getCurrentUserOverview() {
        Long userId = SecurityUtils.getCurrentUser().id();
        User user = userService.getByIdOrThrow(userId);
        long likeCount = communityPostMapper.selectList(new LambdaQueryWrapper<CommunityPost>()
                        .eq(CommunityPost::getUserId, userId))
                .stream()
                .mapToLong(post -> post.getLikeCount() == null ? 0L : post.getLikeCount())
                .sum();
        return new ProfileOverviewResponse(
                userService.toUserProfile(user),
                petMapper.selectCount(new LambdaQueryWrapper<Pet>().eq(Pet::getUserId, userId)),
                communityPostMapper.selectCount(new LambdaQueryWrapper<CommunityPost>().eq(CommunityPost::getUserId, userId)),
                likeCount,
                postFavoriteMapper.selectCount(new LambdaQueryWrapper<PostFavorite>().eq(PostFavorite::getUserId, userId)),
                shopOrderMapper.selectCount(new LambdaQueryWrapper<ShopOrder>().eq(ShopOrder::getUserId, userId)),
                serviceBookingMapper.selectCount(new LambdaQueryWrapper<ServiceBooking>().eq(ServiceBooking::getUserId, userId)),
                adoptionApplicationMapper.selectCount(new LambdaQueryWrapper<AdoptionApplication>().eq(AdoptionApplication::getUserId, userId)),
                messageMapper.selectCount(new LambdaQueryWrapper<Message>()
                        .eq(Message::getUserId, userId)
                        .eq(Message::getReadFlag, false))
        );
    }
}
