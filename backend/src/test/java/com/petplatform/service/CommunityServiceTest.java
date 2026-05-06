package com.petplatform.service;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.petplatform.common.ResultCode;
import com.petplatform.common.exception.BusinessException;
import com.petplatform.dto.community.CreateCommentRequest;
import com.petplatform.dto.community.CreateCommentResponse;
import com.petplatform.dto.community.CreatePostRequest;
import com.petplatform.dto.community.PostDetailResponse;
import com.petplatform.dto.community.PostSummaryResponse;
import com.petplatform.dto.community.ToggleFavoriteResponse;
import com.petplatform.dto.community.ToggleLikeResponse;
import com.petplatform.entity.CommunityPost;
import com.petplatform.entity.PostComment;
import com.petplatform.entity.PostFavorite;
import com.petplatform.entity.PostLike;
import com.petplatform.entity.PostTag;
import com.petplatform.entity.Tag;
import com.petplatform.entity.User;
import com.petplatform.mapper.CommunityPostMapper;
import com.petplatform.mapper.PostCommentMapper;
import com.petplatform.mapper.PostFavoriteMapper;
import com.petplatform.mapper.PostLikeMapper;
import com.petplatform.mapper.PostTagMapper;
import com.petplatform.mapper.TagMapper;
import com.petplatform.mapper.UserMapper;
import com.petplatform.security.CurrentUser;

@ExtendWith(MockitoExtension.class)
class CommunityServiceTest {

    @Mock
    private CommunityPostMapper communityPostMapper;

    @Mock
    private PostCommentMapper postCommentMapper;

    @Mock
    private PostLikeMapper postLikeMapper;

    @Mock
    private PostFavoriteMapper postFavoriteMapper;

    @Mock
    private PostTagMapper postTagMapper;

    @Mock
    private TagMapper tagMapper;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private CommunityService communityService;

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("帖子列表应返回作者、标签和正文摘要")
    void shouldReturnPostPageWithAuthorAndTags() {
        CommunityPost post = approvedPost(1L);
        post.setUserId(20L);
        post.setTitle("Spring Care");
        post.setCategory("CARE");
        post.setCoverUrl("/uploads/post.png");
        post.setContent("a".repeat(100));
        post.setPublishedAt(LocalDateTime.of(2026, 4, 1, 10, 0));
        Page<CommunityPost> page = new Page<>(1, 10);
        page.setRecords(List.of(post));
        page.setTotal(1);
        when(communityPostMapper.selectPage(any(), any())).thenReturn(page);
        when(userMapper.selectByIds(any())).thenReturn(List.of(user(20L)));
        when(postTagMapper.selectList(any())).thenReturn(List.of(postTag(1L, 7L)));
        when(tagMapper.selectByIds(any())).thenReturn(List.of(tag(7L, "care", "ACTIVE")));

        List<PostSummaryResponse> list = communityService.getPostPage("recommended", "CARE", null, 1, 10).list();

        assertThat(list).hasSize(1);
        assertThat(list.get(0).author().nickname()).isEqualTo("Alice");
        assertThat(list.get(0).tags()).containsExactly("care");
        assertThat(list.get(0).excerpt()).hasSize(80);
    }

    @Test
    @DisplayName("按不存在标签筛选帖子时应返回空分页")
    void shouldReturnEmptyPostPageWhenTagNotFound() {
        when(tagMapper.selectOne(any())).thenReturn(null);

        assertThat(communityService.getPostPage(null, null, "missing", 1, 10).list()).isEmpty();
    }

    @Test
    @DisplayName("按标签筛选但无匹配帖子时应返回空分页")
    void shouldReturnEmptyPostPageWhenTagHasNoPosts() {
        Tag tag = new Tag();
        tag.setId(1L);
        tag.setName("care");
        tag.setStatus("ACTIVE");
        when(tagMapper.selectOne(any())).thenReturn(tag);
        when(postTagMapper.selectList(any())).thenReturn(List.of());

        assertThat(communityService.getPostPage(null, null, "care", 1, 10).list()).isEmpty();
    }

    @Test
    @DisplayName("帖子详情应返回点赞收藏状态和图片")
    void shouldReturnPostDetailWithInteractionFlags() {
        mockCurrentUser(20L);
        CommunityPost post = approvedPost(1L);
        post.setUserId(21L);
        post.setTitle("Detail");
        post.setContent("content");
        post.setCoverUrl("/uploads/detail.png");
        post.setPublishedAt(LocalDateTime.of(2026, 4, 1, 10, 0));
        when(communityPostMapper.selectById(1L)).thenReturn(post);
        when(userMapper.selectByIds(any())).thenReturn(List.of(user(21L)));
        when(postTagMapper.selectList(any())).thenReturn(List.of(postTag(1L, 7L)));
        when(tagMapper.selectByIds(any())).thenReturn(List.of(tag(7L, "care", "ACTIVE")));
        when(postLikeMapper.selectCount(any())).thenReturn(1L);
        when(postFavoriteMapper.selectCount(any())).thenReturn(1L);

        PostDetailResponse response = communityService.getPostDetail(1L);

        assertThat(response.images()).containsExactly("/uploads/detail.png");
        assertThat(response.tags()).containsExactly("care");
        assertThat(response.isLiked()).isTrue();
        assertThat(response.isFavorited()).isTrue();
    }

    @Test
    @DisplayName("获取不存在的帖子详情时应抛出异常")
    void shouldThrowWhenPostNotFound() {
        when(communityPostMapper.selectById(999L)).thenReturn(null);

        assertThatThrownBy(() -> communityService.getPostDetail(999L))
                .isInstanceOf(BusinessException.class)
                .satisfies(exception -> assertThat(((BusinessException) exception).getCode())
                        .isEqualTo(ResultCode.RESOURCE_NOT_FOUND.getCode()));
    }

    @Test
    @DisplayName("发布帖子时应保存草稿审核状态并写入有效标签")
    void shouldCreatePostWithActiveTags() {
        mockCurrentUser(20L);
        doAnswer(invocation -> {
            CommunityPost post = invocation.getArgument(0);
            post.setId(99L);
            return 1;
        }).when(communityPostMapper).insert(any(CommunityPost.class));
        when(tagMapper.selectByIds(List.of(7L, 8L))).thenReturn(List.of(
                tag(7L, "care", "ACTIVE"),
                tag(8L, "food", "ACTIVE")
        ));

        var response = communityService.createPost(new CreatePostRequest(
                " Title ",
                " Content ",
                "CARE",
                List.of("", "/uploads/cover.png"),
                List.of(7L, 8L)
        ));

        assertThat(response.id()).isEqualTo(99L);
        assertThat(response.status()).isEqualTo("PENDING");
        verify(postTagMapper, times(2)).insert(any(PostTag.class));
    }

    @Test
    @DisplayName("发布帖子包含不可用标签时应抛出参数异常")
    void shouldRejectCreatePostWhenTagInactive() {
        mockCurrentUser(20L);
        doAnswer(invocation -> {
            CommunityPost post = invocation.getArgument(0);
            post.setId(99L);
            return 1;
        }).when(communityPostMapper).insert(any(CommunityPost.class));
        when(tagMapper.selectByIds(List.of(7L))).thenReturn(List.of(tag(7L, "care", "DISABLED")));

        assertThatThrownBy(() -> communityService.createPost(new CreatePostRequest(
                "Title",
                "Content",
                "CARE",
                List.of(),
                List.of(7L)
        )))
                .isInstanceOf(BusinessException.class)
                .satisfies(exception -> assertThat(((BusinessException) exception).getCode())
                        .isEqualTo(ResultCode.PARAM_ERROR.getCode()));
    }

    @Test
    @DisplayName("评论列表应返回评论作者信息")
    void shouldReturnCommentPage() {
        CommunityPost post = approvedPost(1L);
        when(communityPostMapper.selectById(1L)).thenReturn(post);
        PostComment comment = new PostComment();
        comment.setId(5L);
        comment.setPostId(1L);
        comment.setUserId(20L);
        comment.setContent("nice");
        comment.setCreatedAt(LocalDateTime.of(2026, 4, 1, 10, 0));
        Page<PostComment> page = new Page<>(1, 10);
        page.setRecords(List.of(comment));
        page.setTotal(1);
        when(postCommentMapper.selectPage(any(), any())).thenReturn(page);
        when(userMapper.selectByIds(any())).thenReturn(List.of(user(20L)));

        assertThat(communityService.getCommentPage(1L, 1, 10).list())
                .extracting("content")
                .containsExactly("nice");
    }

    @Test
    @DisplayName("获取不存在帖子的评论列表时应抛出异常")
    void shouldThrowWhenGetCommentPageForNonExistentPost() {
        when(communityPostMapper.selectById(999L)).thenReturn(null);

        assertThatThrownBy(() -> communityService.getCommentPage(999L, 1, 10))
                .isInstanceOf(BusinessException.class)
                .satisfies(exception -> assertThat(((BusinessException) exception).getCode())
                        .isEqualTo(ResultCode.RESOURCE_NOT_FOUND.getCode()));
    }

    @Test
    @DisplayName("发表评论后应新增评论并更新帖子评论数")
    void shouldCreateCommentAndIncreaseCommentCount() {
        mockCurrentUser(20L);
        CommunityPost post = approvedPost(1L);
        post.setCommentCount(2);
        when(communityPostMapper.selectById(1L)).thenReturn(post);
        doAnswer(invocation -> {
            PostComment comment = invocation.getArgument(0);
            comment.setId(88L);
            return 1;
        }).when(postCommentMapper).insert(any(PostComment.class));

        CreateCommentResponse response = communityService.createComment(1L, new CreateCommentRequest("  写得很实用  "));

        ArgumentCaptor<PostComment> commentCaptor = ArgumentCaptor.forClass(PostComment.class);
        verify(postCommentMapper).insert(commentCaptor.capture());
        assertThat(commentCaptor.getValue().getUserId()).isEqualTo(20L);
        assertThat(commentCaptor.getValue().getContent()).isEqualTo("写得很实用");
        assertThat(commentCaptor.getValue().getStatus()).isEqualTo("NORMAL");
        assertThat(response.id()).isEqualTo(88L);
        assertThat(post.getCommentCount()).isEqualTo(3);
        verify(communityPostMapper).updateById(post);
    }

    @Test
    @DisplayName("对不存在帖子发表评论时应抛出异常")
    void shouldThrowWhenCreateCommentForNonExistentPost() {
        mockCurrentUser(20L);
        when(communityPostMapper.selectById(999L)).thenReturn(null);

        assertThatThrownBy(() -> communityService.createComment(999L, new CreateCommentRequest("nice")))
                .isInstanceOf(BusinessException.class)
                .satisfies(exception -> assertThat(((BusinessException) exception).getCode())
                        .isEqualTo(ResultCode.RESOURCE_NOT_FOUND.getCode()));
    }

    @Test
    @DisplayName("首次点赞应插入点赞记录并增加点赞数")
    void shouldCreateLikeWhenNotLikedBefore() {
        mockCurrentUser(20L);
        CommunityPost post = approvedPost(1L);
        post.setLikeCount(5);
        when(communityPostMapper.selectById(1L)).thenReturn(post);
        when(postLikeMapper.selectOne(any())).thenReturn(null);

        ToggleLikeResponse response = communityService.toggleLike(1L);

        ArgumentCaptor<PostLike> likeCaptor = ArgumentCaptor.forClass(PostLike.class);
        verify(postLikeMapper).insert(likeCaptor.capture());
        assertThat(likeCaptor.getValue().getPostId()).isEqualTo(1L);
        assertThat(likeCaptor.getValue().getUserId()).isEqualTo(20L);
        assertThat(response.liked()).isTrue();
        assertThat(response.likeCount()).isEqualTo(6);
        assertThat(post.getLikeCount()).isEqualTo(6);
        verify(communityPostMapper).updateById(post);
    }

    @Test
    @DisplayName("再次点赞应删除点赞记录并扣减点赞数")
    void shouldRemoveLikeWhenAlreadyLiked() {
        mockCurrentUser(20L);
        CommunityPost post = approvedPost(1L);
        post.setLikeCount(5);
        PostLike existing = new PostLike();
        existing.setId(33L);
        when(communityPostMapper.selectById(1L)).thenReturn(post);
        when(postLikeMapper.selectOne(any())).thenReturn(existing);

        ToggleLikeResponse response = communityService.toggleLike(1L);

        verify(postLikeMapper).deleteById(33L);
        assertThat(response.liked()).isFalse();
        assertThat(response.likeCount()).isEqualTo(4);
    }

    @Test
    @DisplayName("对不存在帖子点赞时应抛出异常")
    void shouldThrowWhenToggleLikeForNonExistentPost() {
        mockCurrentUser(20L);
        when(communityPostMapper.selectById(999L)).thenReturn(null);

        assertThatThrownBy(() -> communityService.toggleLike(999L))
                .isInstanceOf(BusinessException.class)
                .satisfies(exception -> assertThat(((BusinessException) exception).getCode())
                        .isEqualTo(ResultCode.RESOURCE_NOT_FOUND.getCode()));
    }

    @Test
    @DisplayName("收藏和取消收藏应更新收藏数")
    void shouldToggleFavorite() {
        mockCurrentUser(20L);
        CommunityPost post = approvedPost(1L);
        post.setFavoriteCount(2);
        when(communityPostMapper.selectById(1L)).thenReturn(post);
        when(postFavoriteMapper.selectOne(any())).thenReturn(null);

        ToggleFavoriteResponse created = communityService.toggleFavorite(1L);

        assertThat(created.favorited()).isTrue();
        assertThat(created.favoriteCount()).isEqualTo(3);

        PostFavorite existing = new PostFavorite();
        existing.setId(44L);
        when(postFavoriteMapper.selectOne(any())).thenReturn(existing);

        ToggleFavoriteResponse removed = communityService.toggleFavorite(1L);

        verify(postFavoriteMapper).deleteById(44L);
        assertThat(removed.favorited()).isFalse();
        assertThat(removed.favoriteCount()).isEqualTo(2);
    }

    @Test
    @DisplayName("对不存在帖子收藏时应抛出异常")
    void shouldThrowWhenToggleFavoriteForNonExistentPost() {
        mockCurrentUser(20L);
        when(communityPostMapper.selectById(999L)).thenReturn(null);

        assertThatThrownBy(() -> communityService.toggleFavorite(999L))
                .isInstanceOf(BusinessException.class)
                .satisfies(exception -> assertThat(((BusinessException) exception).getCode())
                        .isEqualTo(ResultCode.RESOURCE_NOT_FOUND.getCode()));
    }

    @Test
    @DisplayName("我的收藏应按收藏顺序返回已审核帖子")
    void shouldReturnFavoritePostsInFavoriteOrder() {
        mockCurrentUser(20L);
        PostFavorite favorite = new PostFavorite();
        favorite.setId(1L);
        favorite.setPostId(10L);
        favorite.setUserId(20L);
        Page<PostFavorite> page = new Page<>(1, 10);
        page.setRecords(List.of(favorite));
        page.setTotal(1);
        CommunityPost post = approvedPost(10L);
        post.setUserId(21L);
        when(postFavoriteMapper.selectPage(any(), any())).thenReturn(page);
        when(communityPostMapper.selectList(any())).thenReturn(List.of(post));
        when(userMapper.selectByIds(any())).thenReturn(List.of(user(21L)));
        when(postTagMapper.selectList(any())).thenReturn(List.of(postTag(10L, 7L)));
        when(tagMapper.selectByIds(any())).thenReturn(List.of(tag(7L, "care", "ACTIVE")));

        assertThat(communityService.getFavoritePosts(1, 10).list())
                .extracting("id")
                .containsExactly(10L);
    }

    @Test
    @DisplayName("无收藏时应返回空分页")
    void shouldReturnEmptyFavoritePostsWhenNoFavorites() {
        mockCurrentUser(20L);
        Page<PostFavorite> page = new Page<>(1, 10);
        page.setRecords(List.of());
        page.setTotal(0);
        when(postFavoriteMapper.selectPage(any(), any())).thenReturn(page);

        assertThat(communityService.getFavoritePosts(1, 10).list()).isEmpty();
    }

    @Test
    @DisplayName("移除收藏时应删除记录并扣减帖子收藏数")
    void shouldRemoveFavoriteAndDecreaseCount() {
        mockCurrentUser(20L);
        CommunityPost post = approvedPost(1L);
        post.setFavoriteCount(1);
        when(communityPostMapper.selectById(1L)).thenReturn(post);

        communityService.removeFavorite(1L);

        verify(postFavoriteMapper).delete(any());
        assertThat(post.getFavoriteCount()).isZero();
        verify(communityPostMapper).updateById(post);
    }

    @Test
    @DisplayName("移除收藏时帖子不存在应抛出异常")
    void shouldThrowWhenRemoveFavoriteForNonExistentPost() {
        mockCurrentUser(20L);
        when(communityPostMapper.selectById(999L)).thenReturn(null);

        assertThatThrownBy(() -> communityService.removeFavorite(999L))
                .isInstanceOf(BusinessException.class)
                .satisfies(exception -> assertThat(((BusinessException) exception).getCode())
                        .isEqualTo(ResultCode.RESOURCE_NOT_FOUND.getCode()));
    }

    private void mockCurrentUser(Long userId) {
        CurrentUser currentUser = new CurrentUser(userId, "USER", "13800000000");
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(currentUser, null, currentUser.authorities())
        );
    }

    private CommunityPost approvedPost(Long id) {
        CommunityPost post = new CommunityPost();
        post.setId(id);
        post.setUserId(20L);
        post.setTitle("Title");
        post.setContent("Content");
        post.setCategory("CARE");
        post.setStatus("APPROVED");
        post.setLikeCount(0);
        post.setFavoriteCount(0);
        post.setCommentCount(0);
        return post;
    }

    private User user(Long id) {
        User user = new User();
        user.setId(id);
        user.setNickname(id == 20L ? "Alice" : "Bob");
        user.setAvatarUrl("/uploads/avatar.png");
        return user;
    }

    private Tag tag(Long id, String name, String status) {
        Tag tag = new Tag();
        tag.setId(id);
        tag.setName(name);
        tag.setType("community");
        tag.setStatus(status);
        return tag;
    }

    private PostTag postTag(Long postId, Long tagId) {
        PostTag postTag = new PostTag();
        postTag.setPostId(postId);
        postTag.setTagId(tagId);
        return postTag;
    }
}
