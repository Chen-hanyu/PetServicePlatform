<template>
  <section class="posts-hub">
    <div class="page-header">
      <button class="back-btn" @click="goBack">
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
          <path d="M15 19l-7-7 7-7"/>
        </svg>
        返回
      </button>
      <h1 class="page-title">我的动态</h1>
    </div>

    <div class="posts-container">
      <div v-if="loading" class="loading-state">
        <div class="loading-spinner"></div>
        <p class="loading-text">加载中...</p>
      </div>
      <div v-else-if="error" class="error-state">
        <p class="error-text">{{ error }}</p>
        <button class="retry-btn" @click="loadPosts">重试</button>
      </div>
      <div v-else-if="posts.length === 0" class="empty-state">
        <div class="empty-icon">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M19 20H5a2 2 0 01-2-2V6a2 2 0 012-2h10a2 2 0 012 2v1m2 13a2 2 0 01-2-2V7m2 13a2 2 0 002-2V9a2 2 0 00-2-2h-2m-4-3H9M7 16h6M7 8h6v4H7V8z"/>
          </svg>
        </div>
        <p class="empty-text">还没有发布过动态</p>
        <button class="create-btn" @click="goToCommunity">去社区发帖</button>
      </div>

      <div v-else class="posts-list">
        <article v-for="post in posts" :key="post.id" class="post-card" @click="goToPost(post.id)">
          <div class="post-header">
            <div class="post-author">
              <img :src="post.author?.avatar_url || 'https://api.dicebear.com/7.x/avataaars/svg?seed=Felix'" :alt="post.author?.nickname" class="author-avatar" />
              <div class="author-info">
                <span class="author-name">{{ post.author?.nickname || '匿名用户' }}</span>
                <span class="post-time">{{ formatTime(post.published_at) }}</span>
              </div>
            </div>
            <div class="post-actions">
              <button class="action-btn delete" @click.stop="deletePost(post)">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <path d="M3 6h18M19 6v14a2 2 0 01-2 2H7a2 2 0 01-2-2V6m3 0V4a2 2 0 012-2h4a2 2 0 012 2v2"/>
                </svg>
              </button>
            </div>
          </div>

          <div class="post-content">
            <h3 class="post-title">{{ post.title }}</h3>
            <p class="post-text">{{ post.excerpt || post.title }}</p>
            <div v-if="post.cover_url" class="post-images">
              <img :src="post.cover_url" alt="封面" class="post-image" />
            </div>
          </div>

          <div class="post-stats">
            <div class="stat-item">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M14 9V5a3 3 0 00-3-3l-4 9v11h11.28a2 2 0 002-1.7l1.38-9a2 2 0 00-2-2.3zM7 22H4a2 2 0 01-2-2v-7a2 2 0 012-2h3"/>
              </svg>
              <span>{{ post.like_count }} 点赞</span>
            </div>
            <div class="stat-item">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M21 15a2 2 0 01-2 2H7l-4 4V5a2 2 0 012-2h14a2 2 0 012 2z"/>
              </svg>
              <span>{{ post.comment_count }} 评论</span>
            </div>
            <div class="stat-item">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <circle cx="18" cy="5" r="3"/>
                <circle cx="6" cy="12" r="3"/>
                <circle cx="18" cy="19" r="3"/>
                <path d="M8.59 13.51l6.83 3.98M15.41 6.51l-6.82 3.98"/>
              </svg>
              <span>{{ post.favorite_count }} 收藏</span>
            </div>
          </div>
        </article>
      </div>
    </div>
  </section>
</template>

<script setup lang="ts">
import { ref, onMounted } from "vue";
import { useRouter } from "vue-router";
import { fetchPosts } from "@/api/modules/community";
import { toErrorMessage } from "@/api/http";
import type { PostSummary } from "@/types/community";

const router = useRouter();

const loading = ref(false);
const error = ref("");
const posts = ref<PostSummary[]>([]);

const loadPosts = async () => {
  loading.value = true;
  error.value = "";
  try {
    const data = await fetchPosts({ page: 1, page_size: 20 });
    posts.value = data.list ?? [];
  } catch (e) {
    error.value = toErrorMessage(e);
    posts.value = [];
  } finally {
    loading.value = false;
  }
};

const formatTime = (time?: string) => {
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

const goToCommunity = () => {
  router.push("/community");
};

const goToPost = (postId: number) => {
  router.push(`/community/post/${postId}`);
};

const deletePost = async (post: PostSummary) => {
  if (confirm("确定要删除这条动态吗？")) {
    // 调用删除 API（如果后端提供）
    const index = posts.value.findIndex(p => p.id === post.id);
    if (index > -1) {
      posts.value.splice(index, 1);
    }
  }
};

onMounted(loadPosts);
</script>

<style scoped lang="scss">
.posts-hub {
  min-height: calc(100vh - 80px);
  padding: 24px 0;
}

.page-header {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-bottom: 24px;
  padding: 0 24px;

  .back-btn {
    display: flex;
    align-items: center;
    gap: 4px;
    padding: 8px 12px;
    border: none;
    background: var(--surface);
    color: var(--muted);
    font-size: 14px;
    border-radius: var(--radius-md);
    cursor: pointer;
    transition: all 0.2s;

    svg {
      width: 16px;
      height: 16px;
    }

    &:hover {
      background: var(--surface-muted);
      color: var(--text-heading);
    }
  }

  .page-title {
    font-size: 24px;
    font-weight: 600;
    color: var(--text-heading);
    margin: 0;
  }
}

.posts-container {
  max-width: 1000px;
  margin: 0 auto;
  padding: 0 32px;
}

.loading-state {
  text-align: center;
  padding: 60px 24px;
  background: var(--surface);
  border-radius: var(--radius-xl);
  box-shadow: var(--shadow);

  .loading-spinner {
    width: 40px;
    height: 40px;
    margin: 0 auto 16px;
    border: 3px solid var(--surface-muted);
    border-top-color: var(--primary);
    border-radius: 50%;
    animation: spin 0.8s linear infinite;
  }

  .loading-text {
    font-size: 16px;
    color: var(--muted);
    margin: 0;
  }
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

.error-state {
  text-align: center;
  padding: 60px 24px;
  background: var(--surface);
  border-radius: var(--radius-xl);
  box-shadow: var(--shadow);

  .error-text {
    font-size: 16px;
    color: #E97A7A;
    margin: 0 0 24px;
  }

  .retry-btn {
    padding: 12px 24px;
    border: 1px solid var(--primary);
    background: none;
    color: var(--primary);
    font-size: 14px;
    font-weight: 500;
    border-radius: var(--radius-md);
    cursor: pointer;
    transition: all 0.2s;

    &:hover {
      background: var(--primary);
      color: #fff;
    }
  }
}

.empty-state {
  text-align: center;
  padding: 60px 24px;
  background: var(--surface);
  border-radius: var(--radius-xl);
  box-shadow: var(--shadow);

  .empty-icon {
    width: 80px;
    height: 80px;
    margin: 0 auto 16px;
    background: var(--surface-muted);
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;

    svg {
      width: 40px;
      height: 40px;
      color: var(--muted-soft);
    }
  }

  .empty-text {
    font-size: 16px;
    color: var(--muted);
    margin: 0 0 24px;
  }

  .create-btn {
    padding: 12px 24px;
    border: none;
    background: var(--primary);
    color: #fff;
    font-size: 14px;
    font-weight: 500;
    border-radius: var(--radius-md);
    cursor: pointer;
    transition: all 0.2s;

    &:hover {
      opacity: 0.9;
      transform: translateY(-2px);
    }
  }
}

.posts-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.post-card {
  background: var(--surface);
  border-radius: var(--radius-xl);
  padding: 20px;
  box-shadow: var(--shadow);

  .post-header {
    display: flex;
    justify-content: space-between;
    align-items: flex-start;
    margin-bottom: 16px;
  }

  .post-author {
    display: flex;
    align-items: center;
    gap: 12px;

    .author-avatar {
      width: 44px;
      height: 44px;
      border-radius: 50%;
      object-fit: cover;
      background: var(--surface-muted);
    }

    .author-info {
      display: flex;
      flex-direction: column;
      gap: 2px;

      .author-name {
        font-size: 16px;
        font-weight: 600;
        color: var(--text-heading);
      }

      .post-time {
        font-size: 13px;
        color: var(--muted-soft);
      }
    }
  }

  .post-actions {
    display: flex;
    gap: 8px;

    .action-btn {
      width: 36px;
      height: 36px;
      border: none;
      background: var(--surface-muted);
      border-radius: 50%;
      cursor: pointer;
      display: flex;
      align-items: center;
      justify-content: center;
      color: var(--muted);
      transition: all 0.2s;

      svg {
        width: 18px;
        height: 18px;
      }

      &:hover {
        background: var(--primary);
        color: #fff;
      }

      &.delete:hover {
        background: #E97A7A;
      }
    }
  }

  .post-content {
    margin-bottom: 16px;

    .post-text {
      font-size: 15px;
      color: var(--text-heading);
      line-height: 1.7;
      margin: 0 0 12px;
    }

    .post-images {
      display: grid;
      grid-template-columns: repeat(auto-fill, minmax(120px, 1fr));
      gap: 8px;

      .post-image {
        width: 100%;
        aspect-ratio: 1;
        object-fit: cover;
        border-radius: var(--radius-md);
        background: var(--surface-muted);
      }
    }
  }

  .post-stats {
    display: flex;
    gap: 24px;
    padding-top: 16px;
    border-top: 1px solid var(--border-warm);

    .stat-item {
      display: flex;
      align-items: center;
      gap: 6px;
      font-size: 14px;
      color: var(--muted);

      svg {
        width: 16px;
        height: 16px;
      }
    }
  }
}

@media (max-width: 768px) {
  .posts-container {
    padding: 0 20px;
  }
}
</style>
