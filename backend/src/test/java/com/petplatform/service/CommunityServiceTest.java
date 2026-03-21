package com.petplatform.service;

import com.petplatform.dto.community.CreateCommentRequest;
import com.petplatform.dto.community.CreateCommentResponse;
import com.petplatform.dto.community.ToggleLikeResponse;
import com.petplatform.entity.CommunityPost;
import com.petplatform.entity.PostComment;
import com.petplatform.entity.PostLike;
import com.petplatform.mapper.CommunityPostMapper;
import com.petplatform.mapper.PostCommentMapper;
import com.petplatform.mapper.PostFavoriteMapper;
import com.petplatform.mapper.PostLikeMapper;
import com.petplatform.mapper.PostTagMapper;
import com.petplatform.mapper.TagMapper;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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

    private void mockCurrentUser(Long userId) {
        CurrentUser currentUser = new CurrentUser(userId, "USER", "13800000000");
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(currentUser, null, currentUser.authorities())
        );
    }

    private CommunityPost approvedPost(Long id) {
        CommunityPost post = new CommunityPost();
        post.setId(id);
        post.setStatus("APPROVED");
        post.setLikeCount(0);
        post.setFavoriteCount(0);
        post.setCommentCount(0);
        return post;
    }
}
