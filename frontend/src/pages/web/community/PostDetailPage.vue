<template>
  <div class="post-detail-page">
    <!-- 顶部导航 -->
    <div class="top-bar">
      <div class="top-bar-inner">
        <button class="btn-back" @click="goBack">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M19 12H5M12 19l-7-7 7-7"/>
          </svg>
        </button>
        <h1 class="page-title">动态详情</h1>
        <div class="action-btns">
          <button class="btn-action" title="收藏">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M19 21l-7-5-7 5V5a2 2 0 012-2h10a2 2 0 012 2z"/>
            </svg>
          </button>
          <button class="btn-action" title="分享">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <circle cx="18" cy="5" r="3"/>
              <circle cx="6" cy="12" r="3"/>
              <circle cx="18" cy="19" r="3"/>
              <line x1="8.59" y1="13.51" x2="15.42" y2="17.49"/>
              <line x1="15.41" y1="6.51" x2="8.59" y2="10.49"/>
            </svg>
          </button>
        </div>
      </div>
    </div>

    <!-- 加载状态 -->
    <div v-if="loading" class="loading-state">
      <div class="spinner"></div>
      <p>加载中...</p>
    </div>

    <!-- 内容区域 -->
    <div v-else-if="post" class="content-area">
      <!-- 作者信息 -->
      <div class="author-section">
        <img :src="post.author?.avatar_url || 'https://api.dicebear.com/7.x/avataaars/svg?seed=Felix'" class="author-avatar" />
        <div class="author-info">
          <span class="author-name">{{ post.author?.nickname || '匿名用户' }}</span>
          <span class="publish-time">{{ formatTime(post.published_at) }}</span>
        </div>
        <button class="btn-follow">+ 关注</button>
      </div>

      <!-- 分类标签 -->
      <div class="category-tag">
        <span class="tag">{{ post.category }}</span>
      </div>

      <!-- 标题 -->
      <h2 class="post-title">{{ post.title }}</h2>

      <!-- 图片画廊 -->
      <div v-if="postImages.length > 0" class="image-gallery">
        <div 
          v-for="(img, index) in postImages" 
          :key="index" 
          class="gallery-item"
          @click="previewImage(index)"
        >
          <img :src="img" :alt="`图片 ${index + 1}`" />
        </div>
      </div>

      <!-- 正文内容 -->
      <div class="post-content">
        <p>{{ post.content || post.excerpt }}</p>
      </div>

      <!-- 话题标签 -->
      <div v-if="post.tags && post.tags.length > 0" class="tags-section">
        <span v-for="tag in post.tags" :key="tag" class="tag-item">#{{ tag }}</span>
      </div>

      <!-- 互动栏 -->
      <div class="interaction-bar">
        <button class="interaction-btn" :class="{ active: isLiked }" @click="toggleLike">
          <svg viewBox="0 0 24 24" :fill="isLiked ? 'currentColor' : 'none'" stroke="currentColor" stroke-width="2">
            <path d="M14 9V5a3 3 0 00-3-3l-4 9v11h11.28a2 2 0 002-1.7l1.38-9a2 2 0 00-2-2.3H14z"/>
            <path d="M7 22H4a2 2 0 01-2-2v-7a2 2 0 012-2h3"/>
          </svg>
          <span>{{ likeCount }}</span>
        </button>
        <button class="interaction-btn" @click="scrollToComments">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M21 11.5a8.38 8.38 0 01-.9 3.8 8.5 8.5 0 01-7.6 4.7 8.38 8.38 0 01-3.8-.9L3 21l1.9-5.7a8.38 8.38 0 01-.9-3.8 8.5 8.5 0 014.7-7.6 8.38 8.38 0 013.8-.9h.5a8.48 8.48 0 018 8v.5z"/>
          </svg>
          <span>{{ commentCount }}</span>
        </button>
        <button class="interaction-btn">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M19 21l-7-5-7 5V5a2 2 0 012-2h10a2 2 0 012 2z"/>
          </svg>
          <span>{{ post.favorite_count || 0 }}</span>
        </button>
      </div>

      <!-- 评论区域 -->
      <div class="comments-section" ref="commentsRef">
        <h3 class="section-title">评论 ({{ commentCount }})</h3>
        
        <!-- 评论输入 -->
        <div class="comment-input-area">
          <img src="https://api.dicebear.com/7.x/avataaars/svg?seed=Felix" class="user-avatar" />
          <div class="input-wrapper">
            <textarea 
              v-model="commentText" 
              placeholder="发表你的看法..." 
              class="comment-input"
              rows="1"
            ></textarea>
            <button class="btn-send" :disabled="!commentText.trim()" @click="submitComment">
              发送
            </button>
          </div>
        </div>

        <!-- 评论列表 -->
        <div class="comment-list">
          <div v-for="comment in comments" :key="comment.id" class="comment-item">
            <img :src="comment.user.avatar_url" class="comment-avatar" />
            <div class="comment-body">
              <div class="comment-header">
                <span class="comment-author">{{ comment.user.nickname }}</span>
                <span class="comment-time">{{ formatTime(comment.created_at) }}</span>
              </div>
              <p class="comment-text">{{ comment.content }}</p>
              <div class="comment-actions">
                <button class="action-item" @click="toggleCommentLike(comment)">
                  <svg viewBox="0 0 24 24" :fill="comment.isLiked ? 'currentColor' : 'none'" stroke="currentColor" stroke-width="2">
                    <path d="M14 9V5a3 3 0 00-3-3l-4 9v11h11.28a2 2 0 002-1.7l1.38-9a2 2 0 00-2-2.3H14z"/>
                  </svg>
                  {{ comment.like_count || 0 }}
                </button>
                <button class="action-item">
                  <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <path d="M21 11.5a8.38 8.38 0 01-.9 3.8 8.5 8.5 0 01-7.6 4.7 8.38 8.38 0 01-3.8-.9L3 21l1.9-5.7a8.38 8.38 0 01-.9-3.8 8.5 8.5 0 014.7-7.6 8.38 8.38 0 013.8-.9h.5a8.48 8.48 0 018 8v.5z"/>
                  </svg>
                  回复
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 图片预览 -->
    <Teleport to="body">
      <div v-if="previewVisible" class="image-preview-modal" @click="closePreview">
        <button class="close-btn" @click="closePreview">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M18 6L6 18M6 6l12 12"/>
          </svg>
        </button>
        <img :src="postImages[previewIndex]" alt="预览" class="preview-image" />
        <div class="preview-nav">
          <button @click.stop="prevImage" :disabled="previewIndex === 0">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M15 18l-6-6 6-6"/>
            </svg>
          </button>
          <span>{{ previewIndex + 1 }} / {{ postImages.length }}</span>
          <button @click.stop="nextImage" :disabled="previewIndex === postImages.length - 1">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M9 18l6-6-6-6"/>
            </svg>
          </button>
        </div>
      </div>
    </Teleport>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, reactive } from "vue";
import { useRoute, useRouter } from "vue-router";
import { mockPosts } from "@/mocks/community";

const route = useRoute();
const router = useRouter();

const loading = ref(true);
const post = ref<any>(null);
const isLiked = ref(false);
const likeCount = ref(0);
const commentText = ref("");
const quickComment = ref("");
const commentsRef = ref<HTMLElement | null>(null);
const previewVisible = ref(false);
const previewIndex = ref(0);

const comments = ref([
  {
    id: 1,
    user: { nickname: "铲屎官小李", avatar_url: "https://api.dicebear.com/7.x/avataaars/svg?seed=10" },
    content: "写得真好！对我帮助很大，谢谢分享！",
    created_at: "2024-03-20 16:30",
    like_count: 12,
    isLiked: false
  },
  {
    id: 2,
    user: { nickname: "宠物爱好者", avatar_url: "https://api.dicebear.com/7.x/avataaars/svg?seed=11" },
    content: "我家狗狗也是这样，特别有同感",
    created_at: "2024-03-20 15:20",
    like_count: 5,
    isLiked: false
  },
  {
    id: 3,
    user: { nickname: "新手养宠", avatar_url: "https://api.dicebear.com/7.x/avataaars/svg?seed=12" },
    content: "收藏了，准备试试看效果如何",
    created_at: "2024-03-20 14:00",
    like_count: 3,
    isLiked: false
  }
]);

const commentCount = computed(() => comments.value.length);

const postImages = computed(() => {
  if (!post.value) return [];
  if (post.value.cover_url) return [post.value.cover_url];
  if (post.value.images) return post.value.images;
  return [];
});

const formatTime = (time: string) => {
  if (!time) return "";
  const date = new Date(time);
  const now = new Date();
  const diff = now.getTime() - date.getTime();
  
  if (diff < 60000) return "刚刚";
  if (diff < 3600000) return `${Math.floor(diff / 60000)}分钟前`;
  if (diff < 86400000) return `${Math.floor(diff / 3600000)}小时前`;
  if (diff < 604800000) return `${Math.floor(diff / 86400000)}天前`;
  
  return date.toLocaleDateString("zh-CN", { month: "short", day: "numeric" });
};

const goBack = () => {
  router.back();
};

const toggleLike = () => {
  isLiked.value = !isLiked.value;
  likeCount.value += isLiked.value ? 1 : -1;
};

const toggleCommentLike = (comment: any) => {
  comment.isLiked = !comment.isLiked;
  comment.like_count += comment.isLiked ? 1 : -1;
};

const scrollToComments = () => {
  commentsRef.value?.scrollIntoView({ behavior: "smooth" });
};

const submitComment = () => {
  if (!commentText.value.trim()) return;
  
  comments.value.unshift({
    id: Date.now(),
    user: { nickname: "当前用户", avatar_url: "https://api.dicebear.com/7.x/avataaars/svg?seed=Felix" },
    content: commentText.value,
    created_at: new Date().toLocaleString("zh-CN"),
    like_count: 0,
    isLiked: false
  });
  
  commentText.value = "";
};

const submitQuickComment = () => {
  if (!quickComment.value.trim()) return;
  
  comments.value.unshift({
    id: Date.now(),
    user: { nickname: "当前用户", avatar_url: "https://api.dicebear.com/7.x/avataaars/svg?seed=Felix" },
    content: quickComment.value,
    created_at: new Date().toLocaleString("zh-CN"),
    like_count: 0,
    isLiked: false
  });
  
  quickComment.value = "";
};

const previewImage = (index: number) => {
  previewIndex.value = index;
  previewVisible.value = true;
  document.body.style.overflow = "hidden";
};

const closePreview = () => {
  previewVisible.value = false;
  document.body.style.overflow = "";
};

const prevImage = () => {
  if (previewIndex.value > 0) {
    previewIndex.value--;
  }
};

const nextImage = () => {
  if (previewIndex.value < postImages.value.length - 1) {
    previewIndex.value++;
  }
};

onMounted(() => {
  setTimeout(() => {
    const postId = Number(route.params.id);
    const foundPost = mockPosts.find((p: any) => p.id === postId);
    if (foundPost) {
      post.value = foundPost;
      likeCount.value = foundPost.like_count || 0;
    } else {
      post.value = mockPosts[0];
      likeCount.value = post.value.like_count || 0;
    }
    loading.value = false;
  }, 500);
});
</script>

<style scoped lang="scss">
.post-detail-page {
  min-height: 100vh;
  background: var(--bg);
  padding-bottom: 0;
}

// 顶部导航
.top-bar {
  background: #fff;
  border-bottom: 1px solid var(--border-warm);
  position: sticky;
  top: 0;
  z-index: 100;
}

.top-bar-inner {
  display: flex;
  justify-content: space-between;
  align-items: center;
  max-width: 800px;
  margin: 0 auto;
  padding: 14px 20px;
}

.btn-back {
  width: 36px;
  height: 36px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--surface);
  border: none;
  border-radius: 50%;
  cursor: pointer;
  transition: all 0.2s ease;

  svg {
    width: 20px;
    height: 20px;
    color: var(--text-heading);
  }

  &:hover {
    background: var(--surface-muted);
  }
}

.page-title {
  font-size: 17px;
  font-weight: 700;
  color: var(--text-heading);
  margin: 0;
}

.action-btns {
  display: flex;
  gap: 8px;
}

.btn-action {
  width: 36px;
  height: 36px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--surface);
  border: none;
  border-radius: 50%;
  cursor: pointer;
  transition: all 0.2s ease;

  svg {
    width: 18px;
    height: 18px;
    color: var(--muted);
  }

  &:hover {
    background: var(--surface-muted);

    svg {
      color: var(--primary);
    }
  }
}

// 加载状态
.loading-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60px 20px;

  .spinner {
    width: 40px;
    height: 40px;
    border: 3px solid var(--border-warm);
    border-top-color: var(--primary);
    border-radius: 50%;
    animation: spin 1s linear infinite;
  }

  p {
    margin-top: 16px;
    color: var(--muted);
    font-size: 14px;
  }
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

// 内容区域
.content-area {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
}

// 作者信息
.author-section {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 20px;

  .author-avatar {
    width: 48px;
    height: 48px;
    border-radius: 50%;
    border: 2px solid var(--border-warm);
  }

  .author-info {
    flex: 1;
    display: flex;
    flex-direction: column;

    .author-name {
      font-size: 15px;
      font-weight: 700;
      color: var(--text-heading);
    }

    .publish-time {
      font-size: 12px;
      color: var(--muted);
    }
  }

  .btn-follow {
    padding: 6px 16px;
    background: var(--primary);
    color: #fff;
    border: none;
    border-radius: 16px;
    font-size: 13px;
    font-weight: 600;
    cursor: pointer;
    transition: all 0.2s ease;

    &:hover {
      opacity: 0.9;
    }
  }
}

// 分类标签
.category-tag {
  margin-bottom: 12px;

  .tag {
    display: inline-block;
    padding: 4px 12px;
    background: rgba(255, 155, 122, 0.15);
    color: var(--primary);
    border-radius: 12px;
    font-size: 12px;
    font-weight: 600;
  }
}

// 标题
.post-title {
  font-size: 22px;
  font-weight: 700;
  color: var(--text-heading);
  margin: 0 0 16px;
  line-height: 1.4;
}

// 图片画廊
.image-gallery {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 8px;
  margin-bottom: 20px;
  border-radius: 12px;
  overflow: hidden;

  .gallery-item {
    aspect-ratio: 1;
    cursor: pointer;
    overflow: hidden;

    img {
      width: 100%;
      height: 100%;
      object-fit: cover;
      transition: transform 0.3s ease;
    }

    &:hover img {
      transform: scale(1.05);
    }
  }
}

// 正文内容
.post-content {
  margin-bottom: 20px;

  p {
    font-size: 15px;
    color: var(--text);
    line-height: 1.8;
    margin: 0;
  }
}

// 话题标签
.tags-section {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  margin-bottom: 20px;

  .tag-item {
    padding: 6px 14px;
    background: var(--surface);
    color: var(--primary);
    border-radius: 16px;
    font-size: 13px;
    font-weight: 600;
  }
}

// 互动栏
.interaction-bar {
  display: flex;
  gap: 20px;
  padding: 16px 0;
  border-top: 1px solid var(--border-warm);
  border-bottom: 1px solid var(--border-warm);
  margin-bottom: 24px;
}

.interaction-btn {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 8px 16px;
  background: var(--surface);
  border: 1px solid var(--border-warm);
  border-radius: 20px;
  font-size: 14px;
  color: var(--muted);
  cursor: pointer;
  transition: all 0.2s ease;

  svg {
    width: 18px;
    height: 18px;
  }

  &:hover {
    border-color: var(--primary);
    color: var(--primary);
  }

  &.active {
    background: rgba(255, 107, 107, 0.1);
    border-color: #ff6b6b;
    color: #ff6b6b;
  }
}

// 评论区域
.comments-section {
  margin-top: 24px;
}

.section-title {
  font-size: 16px;
  font-weight: 700;
  color: var(--text-heading);
  margin: 0 0 20px;
}

// 评论输入
.comment-input-area {
  display: flex;
  gap: 12px;
  margin-bottom: 24px;

  .user-avatar {
    width: 40px;
    height: 40px;
    border-radius: 50%;
    flex-shrink: 0;
  }

  .input-wrapper {
    flex: 1;
    display: flex;
    gap: 10px;
    align-items: flex-end;
  }

  .comment-input {
    flex: 1;
    padding: 10px 14px;
    background: var(--surface);
    border: 1px solid var(--border-warm);
    border-radius: 20px;
    font-size: 14px;
    outline: none;
    resize: none;
    transition: all 0.2s ease;

    &:focus {
      border-color: var(--primary);
    }
  }

  .btn-send {
    padding: 8px 16px;
    background: var(--primary);
    color: #fff;
    border: none;
    border-radius: 16px;
    font-size: 13px;
    font-weight: 600;
    cursor: pointer;
    transition: all 0.2s ease;

    &:disabled {
      background: #ccc;
      cursor: not-allowed;
    }
  }
}

// 评论列表
.comment-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.comment-item {
  display: flex;
  gap: 12px;

  .comment-avatar {
    width: 36px;
    height: 36px;
    border-radius: 50%;
    flex-shrink: 0;
  }

  .comment-body {
    flex: 1;
  }

  .comment-header {
    display: flex;
    align-items: center;
    gap: 8px;
    margin-bottom: 6px;

    .comment-author {
      font-size: 14px;
      font-weight: 600;
      color: var(--text-heading);
    }

    .comment-time {
      font-size: 12px;
      color: var(--muted);
    }
  }

  .comment-text {
    font-size: 14px;
    color: var(--text);
    line-height: 1.6;
    margin: 0 0 10px;
  }

  .comment-actions {
    display: flex;
    gap: 16px;

    .action-item {
      display: flex;
      align-items: center;
      gap: 4px;
      padding: 0;
      background: none;
      border: none;
      font-size: 12px;
      color: var(--muted);
      cursor: pointer;
      transition: color 0.2s ease;

      svg {
        width: 16px;
        height: 16px;
      }

      &:hover {
        color: var(--primary);
      }
    }
  }
}

// 图片预览
.image-preview-modal {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.9);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;

  .close-btn {
    position: absolute;
    top: 20px;
    right: 20px;
    width: 44px;
    height: 44px;
    display: flex;
    align-items: center;
    justify-content: center;
    background: rgba(255, 255, 255, 0.1);
    border: none;
    border-radius: 50%;
    cursor: pointer;

    svg {
      width: 24px;
      height: 24px;
      color: #fff;
    }
  }

  .preview-image {
    max-width: 90%;
    max-height: 80%;
    object-fit: contain;
  }

  .preview-nav {
    position: absolute;
    bottom: 40px;
    display: flex;
    align-items: center;
    gap: 20px;

    button {
      width: 44px;
      height: 44px;
      display: flex;
      align-items: center;
      justify-content: center;
      background: rgba(255, 255, 255, 0.1);
      border: none;
      border-radius: 50%;
      cursor: pointer;

      svg {
        width: 24px;
        height: 24px;
        color: #fff;
      }

      &:disabled {
        opacity: 0.3;
        cursor: not-allowed;
      }
    }

    span {
      color: #fff;
      font-size: 14px;
    }
  }
}

// 响应式
@media (max-width: 768px) {
  .content-area {
    padding: 16px;
  }

  .image-gallery {
    grid-template-columns: repeat(2, 1fr);
  }

  .interaction-bar {
    gap: 10px;
  }

  .interaction-btn {
    padding: 8px 12px;
    font-size: 13px;
  }
}
</style>
