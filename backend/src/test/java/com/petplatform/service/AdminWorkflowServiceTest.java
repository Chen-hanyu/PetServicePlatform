package com.petplatform.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.petplatform.common.ResultCode;
import com.petplatform.common.exception.BusinessException;
import com.petplatform.dto.admin.PostReviewRequest;
import com.petplatform.dto.admin.UpdateOrderRequest;
import com.petplatform.dto.admin.UpdateServiceBookingRequest;
import com.petplatform.dto.admin.UpdateUserStatusRequest;
import com.petplatform.dto.adoption.CreateAdoptionApplicationRequest;
import com.petplatform.entity.AdoptionApplication;
import com.petplatform.entity.AdoptionPet;
import com.petplatform.entity.CommunityPost;
import com.petplatform.entity.Merchant;
import com.petplatform.entity.MerchantReview;
import com.petplatform.entity.MerchantService;
import com.petplatform.entity.PostComment;
import com.petplatform.entity.PostTag;
import com.petplatform.entity.ServiceBooking;
import com.petplatform.entity.ShopOrder;
import com.petplatform.entity.Tag;
import com.petplatform.entity.User;
import com.petplatform.mapper.AdoptionApplicationMapper;
import com.petplatform.mapper.AdoptionPetMapper;
import com.petplatform.mapper.CommunityPostMapper;
import com.petplatform.mapper.MerchantMapper;
import com.petplatform.mapper.MerchantReviewMapper;
import com.petplatform.mapper.MerchantServiceMapper;
import com.petplatform.mapper.PetMapper;
import com.petplatform.mapper.PostCommentMapper;
import com.petplatform.mapper.PostTagMapper;
import com.petplatform.mapper.ServiceBookingMapper;
import com.petplatform.mapper.ShopOrderItemMapper;
import com.petplatform.mapper.ShopOrderMapper;
import com.petplatform.mapper.TagMapper;
import com.petplatform.mapper.UserMapper;
import com.petplatform.security.CurrentUser;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
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
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdminWorkflowServiceTest {

    @Mock
    private MerchantReviewMapper merchantReviewMapper;
    @Mock
    private MerchantMapper merchantMapper;
    @Mock
    private UserMapper userMapper;
    @Mock
    private PostCommentMapper postCommentMapper;
    @Mock
    private CommunityPostMapper communityPostMapper;
    @Mock
    private ShopOrderMapper shopOrderMapper;
    @Mock
    private ShopOrderItemMapper shopOrderItemMapper;
    @Mock
    private PostTagMapper postTagMapper;
    @Mock
    private TagMapper tagMapper;
    @Mock
    private ServiceBookingMapper serviceBookingMapper;
    @Mock
    private MerchantServiceMapper merchantServiceMapper;
    @Mock
    private AdoptionPetMapper adoptionPetMapper;
    @Mock
    private AdoptionApplicationMapper adoptionApplicationMapper;
    @Mock
    private PetMapper petMapper;

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("管理端评价列表应组装商家和用户信息，删除评价后应重算商家评分")
    void shouldListAndDeleteMerchantReviews() {
        AdminMerchantReviewService service = new AdminMerchantReviewService(merchantReviewMapper, merchantMapper, userMapper);
        MerchantReview review = merchantReview(10L, 3L, 2L, 5);
        Page<MerchantReview> page = pageOf(review);
        Merchant merchant = merchant(3L);
        User author = user(2L);
        when(merchantReviewMapper.selectPage(any(), any())).thenReturn(page);
        when(merchantMapper.selectByIds(List.of(3L))).thenReturn(List.of(merchant));
        when(userMapper.selectByIds(List.of(2L))).thenReturn(List.of(author));

        var response = service.getReviewPage(3L, "clean", 1, 10);

        assertThat(response.total()).isEqualTo(1);
        assertThat(response.list().get(0).merchantName()).isEqualTo("Happy Pets");
        assertThat(response.list().get(0).author().nickname()).isEqualTo("Alice");

        when(merchantReviewMapper.selectById(10L)).thenReturn(review);
        when(merchantMapper.selectById(3L)).thenReturn(merchant);
        when(merchantReviewMapper.selectList(any())).thenReturn(List.of(
                merchantReview(11L, 3L, 4L, 5),
                merchantReview(12L, 3L, 5L, 3)
        ));

        service.deleteReview(10L);

        assertThat(merchant.getScore()).isEqualByComparingTo(new BigDecimal("4.0"));
        verify(merchantReviewMapper).deleteById(10L);
        verify(merchantMapper).updateById(merchant);
    }

    @Test
    @DisplayName("管理端评论列表应组装帖子和用户信息，删除正常评论时应减少帖子评论数")
    void shouldListAndDeleteComments() {
        AdminCommentService service = new AdminCommentService(postCommentMapper, communityPostMapper, userMapper);
        PostComment comment = comment(6L, 8L, 2L, "NORMAL");
        CommunityPost post = post(8L, 2L, "APPROVED");
        post.setCommentCount(2);
        when(postCommentMapper.selectPage(any(), any())).thenReturn(pageOf(comment));
        when(communityPostMapper.selectByIds(List.of(8L))).thenReturn(List.of(post));
        when(userMapper.selectByIds(List.of(2L))).thenReturn(List.of(user(2L)));

        var response = service.getCommentPage("nice", 1, 10);

        assertThat(response.list().get(0).postTitle()).isEqualTo("养宠经验");
        assertThat(response.list().get(0).author().phone()).isEqualTo("13800000002");

        when(postCommentMapper.selectById(6L)).thenReturn(comment);
        when(communityPostMapper.selectById(8L)).thenReturn(post);

        service.deleteComment(6L);

        assertThat(comment.getStatus()).isEqualTo("DELETED");
        assertThat(post.getCommentCount()).isEqualTo(1);
        verify(postCommentMapper).updateById(comment);
        verify(communityPostMapper).updateById(post);
    }

    @Test
    @DisplayName("管理端订单列表应组装用户信息，订单状态更新应拒绝非法流转")
    void shouldListAndUpdateOrders() {
        AdminOrderService service = new AdminOrderService(shopOrderMapper, shopOrderItemMapper, userMapper);
        ShopOrder order = order(20L, "PAID");
        when(shopOrderMapper.selectPage(any(), any())).thenReturn(pageOf(order));
        when(userMapper.selectByIds(List.of(2L))).thenReturn(List.of(user(2L)));
        when(shopOrderItemMapper.selectList(any())).thenReturn(List.of());

        var page = service.getOrderPage("PAID", "NO", 1, 10);

        assertThat(page.list().get(0).orderNo()).isEqualTo("NO20240424001");
        assertThat(page.list().get(0).user().nickname()).isEqualTo("Alice");

        when(shopOrderMapper.selectById(20L)).thenReturn(order);

        var response = service.updateOrder(20L, new UpdateOrderRequest("SHIPPED", "已发货"));

        assertThat(response.status()).isEqualTo("SHIPPED");
        assertThat(order.getRemark()).isEqualTo("已发货");
        verify(shopOrderMapper).updateById(order);

        order.setStatus("COMPLETED");
        assertThatThrownBy(() -> service.updateOrder(20L, new UpdateOrderRequest("CANCELLED", "撤销")))
                .isInstanceOf(BusinessException.class)
                .satisfies(exception -> assertThat(((BusinessException) exception).getCode())
                        .isEqualTo(ResultCode.INVALID_OPERATION.getCode()));
    }

    @Test
    @DisplayName("管理端社区列表应返回作者和标签，审核待审帖子通过后应设置发布时间")
    void shouldListAndReviewCommunityPosts() {
        AdminCommunityService service = new AdminCommunityService(communityPostMapper, postTagMapper, tagMapper, userMapper);
        CommunityPost post = post(8L, 2L, "PENDING");
        when(communityPostMapper.selectPage(any(), any())).thenReturn(pageOf(post));
        when(userMapper.selectByIds(List.of(2L))).thenReturn(List.of(user(2L)));
        when(postTagMapper.selectList(any())).thenReturn(List.of(postTag(8L, 4L)));
        when(tagMapper.selectByIds(List.of(4L))).thenReturn(List.of(tag(4L)));

        var page = service.getPostPage("PENDING", "经验", "Alice", 1, 10);

        assertThat(page.list().get(0).author().nickname()).isEqualTo("Alice");
        assertThat(page.list().get(0).tags()).containsExactly("护理");

        when(communityPostMapper.selectById(8L)).thenReturn(post);

        var response = service.reviewPost(8L, new PostReviewRequest("APPROVED", "内容真实"));

        assertThat(response.status()).isEqualTo("APPROVED");
        assertThat(post.getPublishedAt()).isNotNull();
        verify(communityPostMapper).updateById(post);
    }

    @Test
    @DisplayName("管理端预约列表应组装用户商家服务信息，并支持确认预约")
    void shouldListAndUpdateServiceBookings() {
        AdminServiceBookingService service = new AdminServiceBookingService(
                serviceBookingMapper, merchantMapper, merchantServiceMapper, userMapper
        );
        ServiceBooking booking = booking(30L, "PENDING");
        when(serviceBookingMapper.selectPage(any(), any())).thenReturn(pageOf(booking));
        when(merchantMapper.selectByIds(List.of(3L))).thenReturn(List.of(merchant(3L)));
        when(merchantServiceMapper.selectByIds(List.of(7L))).thenReturn(List.of(merchantService(7L)));
        when(userMapper.selectByIds(List.of(2L))).thenReturn(List.of(user(2L)));

        var page = service.getBookingPage("PENDING", 3L, 1, 10);

        assertThat(page.list().get(0).merchant().name()).isEqualTo("Happy Pets");
        assertThat(page.list().get(0).serviceName()).isEqualTo("洗护套餐");

        when(serviceBookingMapper.selectById(30L)).thenReturn(booking);

        var response = service.updateBooking(30L, new UpdateServiceBookingRequest("CONFIRMED", "已确认"));

        assertThat(response.status()).isEqualTo("CONFIRMED");
        assertThat(booking.getRemark()).isEqualTo("已确认");
        verify(serviceBookingMapper).updateById(booking);
    }

    @Test
    @DisplayName("领养服务应支持列表详情、申请创建和我的申请查询")
    void shouldHandleAdoptionUserFlow() {
        mockUser(2L);
        AdoptionService service = new AdoptionService(adoptionPetMapper, adoptionApplicationMapper);
        AdoptionPet pet = adoptionPet(9L, "ONLINE");
        AdoptionApplication application = adoptionApplication(15L, 9L, 2L, "PENDING");
        when(adoptionPetMapper.selectPage(any(), any())).thenReturn(pageOf(pet));
        when(adoptionPetMapper.selectById(9L)).thenReturn(pet);
        when(adoptionApplicationMapper.selectCount(any())).thenReturn(0L);
        doAnswer(invocation -> {
            AdoptionApplication inserted = invocation.getArgument(0);
            inserted.setId(16L);
            return 1;
        }).when(adoptionApplicationMapper).insert(ArgumentMatchers.any(AdoptionApplication.class));

        var pets = service.getPetPage("CAT", "上海", "FEMALE", 1, 10);
        var detail = service.getPetDetail(9L);
        var created = service.createApplication(new CreateAdoptionApplicationRequest(
                9L, "13800000002", "  养过猫  ", "  家里空间稳定  "
        ));

        assertThat(pets.list().get(0).name()).isEqualTo("团团");
        assertThat(detail.personality()).isEqualTo("亲人");
        assertThat(created.id()).isEqualTo(16L);
        assertThat(created.status()).isEqualTo("PENDING");

        when(adoptionApplicationMapper.selectPage(any(), any())).thenReturn(pageOf(application));
        when(adoptionPetMapper.selectByIds(List.of(9L))).thenReturn(List.of(pet));

        var myApplications = service.getMyApplications("PENDING", 1, 10);

        assertThat(myApplications.list().get(0).pet().name()).isEqualTo("团团");
        verify(adoptionApplicationMapper).insert(ArgumentMatchers.<AdoptionApplication>argThat(inserted ->
                inserted.getUserId().equals(2L)
                        && inserted.getExperienceDesc().equals("养过猫")
                        && inserted.getLivingConditionDesc().equals("家里空间稳定")
        ));
    }

    @Test
    @DisplayName("管理端用户服务应支持分页、详情统计和状态更新")
    void shouldListDetailAndUpdateUsers() {
        AdminUserService service = new AdminUserService(
                userMapper, petMapper, communityPostMapper, shopOrderMapper, adoptionApplicationMapper
        );
        User user = user(2L);
        when(userMapper.selectPage(any(), any())).thenReturn(pageOf(user));

        var users = service.getUserPage("Alice", "ACTIVE", 1, 10);

        assertThat(users.list().get(0).nickname()).isEqualTo("Alice");

        when(userMapper.selectById(2L)).thenReturn(user);
        when(petMapper.selectCount(any())).thenReturn(2L);
        when(communityPostMapper.selectCount(any())).thenReturn(3L);
        when(shopOrderMapper.selectCount(any())).thenReturn(4L);
        when(adoptionApplicationMapper.selectCount(any())).thenReturn(5L);

        var detail = service.getUserDetail(2L);

        assertThat(detail.petCount()).isEqualTo(2);
        assertThat(detail.postCount()).isEqualTo(3);
        assertThat(detail.orderCount()).isEqualTo(4);
        assertThat(detail.adoptionApplicationCount()).isEqualTo(5);

        var updated = service.updateUserStatus(2L, new UpdateUserStatusRequest("DISABLED", "违规"));

        assertThat(updated.status()).isEqualTo("DISABLED");
        assertThat(user.getStatus()).isEqualTo("DISABLED");
        verify(userMapper).updateById(user);

        assertThatThrownBy(() -> service.updateUserStatus(2L, new UpdateUserStatusRequest("LOCKED", "非法")))
                .isInstanceOf(BusinessException.class)
                .satisfies(exception -> assertThat(((BusinessException) exception).getCode())
                        .isEqualTo(ResultCode.PARAM_ERROR.getCode()));
    }

    @Test
    @DisplayName("删除已删除评论不应重复更新帖子统计")
    void shouldSkipAlreadyDeletedComment() {
        AdminCommentService service = new AdminCommentService(postCommentMapper, communityPostMapper, userMapper);
        PostComment comment = comment(6L, 8L, 2L, "DELETED");
        when(postCommentMapper.selectById(6L)).thenReturn(comment);

        service.deleteComment(6L);

        verify(postCommentMapper, never()).updateById(ArgumentMatchers.any(PostComment.class));
        verify(communityPostMapper, never()).selectById(any());
    }

    private <T> Page<T> pageOf(T record) {
        Page<T> page = new Page<>(1, 10);
        page.setRecords(List.of(record));
        page.setTotal(1);
        return page;
    }

    private User user(Long id) {
        User user = new User();
        user.setId(id);
        user.setRole("USER");
        user.setPhone("1380000000" + id);
        user.setNickname("Alice");
        user.setAvatarUrl("/avatar.png");
        user.setGender("FEMALE");
        user.setBio("爱宠物");
        user.setStatus("ACTIVE");
        user.setCreatedAt(LocalDateTime.now());
        return user;
    }

    private Merchant merchant(Long id) {
        Merchant merchant = new Merchant();
        merchant.setId(id);
        merchant.setName("Happy Pets");
        merchant.setDistrict("浦东");
        merchant.setAddress("宠物路 1 号");
        merchant.setBusinessHours("09:00-18:00");
        merchant.setScore(new BigDecimal("4.8"));
        merchant.setStatus("APPROVED");
        return merchant;
    }

    private MerchantReview merchantReview(Long id, Long merchantId, Long userId, int score) {
        MerchantReview review = new MerchantReview();
        review.setId(id);
        review.setMerchantId(merchantId);
        review.setUserId(userId);
        review.setScore(score);
        review.setContent("服务很细心");
        review.setCreatedAt(LocalDateTime.now());
        return review;
    }

    private PostComment comment(Long id, Long postId, Long userId, String status) {
        PostComment comment = new PostComment();
        comment.setId(id);
        comment.setPostId(postId);
        comment.setUserId(userId);
        comment.setContent("很有帮助");
        comment.setStatus(status);
        comment.setCreatedAt(LocalDateTime.now());
        return comment;
    }

    private CommunityPost post(Long id, Long userId, String status) {
        CommunityPost post = new CommunityPost();
        post.setId(id);
        post.setUserId(userId);
        post.setCategory("经验");
        post.setTitle("养宠经验");
        post.setContent("这是一篇关于宠物护理的经验分享");
        post.setCoverUrl("/cover.jpg");
        post.setStatus(status);
        post.setLikeCount(1);
        post.setFavoriteCount(2);
        post.setCommentCount(0);
        post.setCreatedAt(LocalDateTime.now());
        return post;
    }

    private PostTag postTag(Long postId, Long tagId) {
        PostTag postTag = new PostTag();
        postTag.setPostId(postId);
        postTag.setTagId(tagId);
        return postTag;
    }

    private Tag tag(Long id) {
        Tag tag = new Tag();
        tag.setId(id);
        tag.setName("护理");
        tag.setStatus("ACTIVE");
        return tag;
    }

    private ShopOrder order(Long id, String status) {
        ShopOrder order = new ShopOrder();
        order.setId(id);
        order.setUserId(2L);
        order.setOrderNo("NO20240424001");
        order.setTotalAmount(new BigDecimal("99.00"));
        order.setPayAmount(new BigDecimal("89.00"));
        order.setStatus(status);
        order.setReceiverName("Alice");
        order.setReceiverPhone("13800000002");
        order.setReceiverAddress("上海市浦东新区");
        order.setCreatedAt(LocalDateTime.now());
        return order;
    }

    private ServiceBooking booking(Long id, String status) {
        ServiceBooking booking = new ServiceBooking();
        booking.setId(id);
        booking.setUserId(2L);
        booking.setMerchantId(3L);
        booking.setMerchantServiceId(7L);
        booking.setBookingTime(LocalDateTime.now().plusDays(1));
        booking.setContactName("Alice");
        booking.setContactPhone("13800000002");
        booking.setStatus(status);
        booking.setCreatedAt(LocalDateTime.now());
        return booking;
    }

    private MerchantService merchantService(Long id) {
        MerchantService merchantService = new MerchantService();
        merchantService.setId(id);
        merchantService.setMerchantId(3L);
        merchantService.setName("洗护套餐");
        merchantService.setPrice(new BigDecimal("128.00"));
        merchantService.setDurationMinutes(90);
        merchantService.setStatus("ACTIVE");
        return merchantService;
    }

    private AdoptionPet adoptionPet(Long id, String status) {
        AdoptionPet pet = new AdoptionPet();
        pet.setId(id);
        pet.setName("团团");
        pet.setType("CAT");
        pet.setBreed("田园猫");
        pet.setGender("FEMALE");
        pet.setAgeDesc("1 岁");
        pet.setCity("上海");
        pet.setHealthStatus("已驱虫");
        pet.setPersonality("亲人");
        pet.setAdoptionRequirements("有稳定住所");
        pet.setStory("救助后恢复良好");
        pet.setStatus(status);
        pet.setCoverUrl("/tuantuan.jpg");
        pet.setCreatedAt(LocalDateTime.now());
        return pet;
    }

    private AdoptionApplication adoptionApplication(Long id, Long petId, Long userId, String status) {
        AdoptionApplication application = new AdoptionApplication();
        application.setId(id);
        application.setPetId(petId);
        application.setUserId(userId);
        application.setContactPhone("13800000002");
        application.setExperienceDesc("养过猫");
        application.setLivingConditionDesc("家里空间稳定");
        application.setStatus(status);
        application.setCreatedAt(LocalDateTime.now());
        return application;
    }

    private void mockUser(Long userId) {
        CurrentUser currentUser = new CurrentUser(userId, "USER", "13800000002");
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(currentUser, null, currentUser.authorities())
        );
    }
}
