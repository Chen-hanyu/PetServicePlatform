<template>
  <div class="community-page">
    <!-- 顶部搜索区域 -->
    <div class="top-bar">
      <div class="top-bar-inner">
        <div class="logo">
          <span class="logo-icon">🐾</span>
          <span class="logo-text">宠物社区</span>
        </div>

        <div class="search-box">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <circle cx="11" cy="11" r="8"/>
            <path d="M21 21l-4.35-4.35"/>
          </svg>
          <input type="text" placeholder="搜索话题、用户..." v-model="searchQuery" />
        </div>

        <button class="btn-publish" @click="goToCreate">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M12 5v14M5 12h14"/>
          </svg>
          发布
        </button>
      </div>
    </div>

    <!-- 内容区域 -->
    <div class="content-wrapper">
      <!-- 主内容区 -->
      <main class="main-content">
        <!-- 分类标签 -->
        <section class="category-section">
          <div class="category-tabs">
            <button 
              v-for="cat in categories" 
              :key="cat" 
              :class="['category-tab', { active: activeCategory === cat }]"
              @click="activeCategory = cat"
            >
              {{ cat }}
            </button>
          </div>
        </section>

        <!-- 内容三栏网格 -->
        <section class="content-section">
          <DataState :loading="loading" :error="error" :empty="posts.length === 0" empty-text="暂无内容，快来发布第一篇吧">
            <div class="content-grid">
              <article v-for="post in posts" :key="post.id" class="content-card" @click="openDetail(post.id)">
                <div class="card-image">
                  <img :src="post.cover || 'https://images.unsplash.com/photo-1450778869180-41d0601e046e?auto=format&fit=crop&w=400&q=80'" :alt="post.title" />
                </div>
                <div class="card-body">
                  <h4 class="card-title">{{ post.title }}</h4>
                  <div class="card-footer">
                    <span class="author">
                      <img :src="post.author?.avatar_url || 'https://api.dicebear.com/7.x/avataaars/svg?seed=Felix'" />
                      {{ post.author?.nickname || '匿名用户' }}
                    </span>
                    <span class="stats">
                      <span>❤️ {{ post.like_count || 0 }}</span>
                      <span>💬 {{ post.comment_count || 0 }}</span>
                    </span>
                  </div>
                </div>
              </article>
            </div>
          </DataState>

          <!-- 页码切换 -->
          <div class="pagination">
            <button class="page-btn" :class="{ active: currentPage === 1 }" @click="currentPage = 1">1</button>
            <button class="page-btn" :class="{ active: currentPage === 2 }" @click="currentPage = 2">2</button>
            <button class="page-btn" :class="{ active: currentPage === 3 }" @click="currentPage = 3">3</button>
            <span class="page-ellipsis">...</span>
            <button class="page-btn" @click="currentPage++">下一页</button>
          </div>
        </section>
      </main>

      <!-- 右侧边栏 -->
      <aside class="right-sidebar">
        <!-- 个人信息卡片 -->
        <div class="sidebar-card user-card">
          <div class="user-profile">
            <img src="https://api.dicebear.com/7.x/avataaars/svg?seed=Felix" class="user-avatar" />
            <div class="user-info">
              <span class="user-name">宠友123456</span>
              <span class="user-level">Lv.5 萌新达人</span>
            </div>
          </div>
          <div class="user-stats">
            <div class="stat-item">
              <span class="stat-num">128</span>
              <span class="stat-label">关注</span>
            </div>
            <div class="stat-item">
              <span class="stat-num">256</span>
              <span class="stat-label">粉丝</span>
            </div>
            <div class="stat-item">
              <span class="stat-num">1.2k</span>
              <span class="stat-label">获赞</span>
            </div>
          </div>
        </div>

        <!-- 热门话题 -->
        <div class="sidebar-card">
          <h4 class="card-title-bar">🔥 热门话题</h4>
          <ul class="topic-list">
            <li class="topic-item">
              <span class="rank">1</span>
              <span class="topic-name">#新手养猫攻略</span>
            </li>
            <li class="topic-item">
              <span class="rank">2</span>
              <span class="topic-name">#狗狗行为训练</span>
            </li>
            <li class="topic-item">
              <span class="rank">3</span>
              <span class="topic-name">#自制宠物零食</span>
            </li>
            <li class="topic-item">
              <span class="rank">4</span>
              <span class="topic-name">#春季驱虫</span>
            </li>
            <li class="topic-item">
              <span class="rank">5</span>
              <span class="topic-name">#宠物摄影大赛</span>
            </li>
          </ul>
        </div>

        <!-- 特别关注 -->
        <div class="sidebar-card">
          <h4 class="card-title-bar">💜 特别关注</h4>
          <div class="user-list">
            <div class="user-item">
              <img src="https://api.dicebear.com/7.x/avataaars/svg?seed=1" class="user-avatar" />
              <div class="user-info">
                <span class="user-name">喵星人</span>
                <span class="user-desc">分享养猫心得</span>
              </div>
              <button class="btn-follow">+ 关注</button>
            </div>
            <div class="user-item">
              <img src="https://api.dicebear.com/7.x/avataaars/svg?seed=2" class="user-avatar" />
              <div class="user-info">
                <span class="user-name">汪星人</span>
                <span class="user-desc">狗狗训练师</span>
              </div>
              <button class="btn-follow">+ 关注</button>
            </div>
            <div class="user-item">
              <img src="https://api.dicebear.com/7.x/avataaars/svg?seed=3" class="user-avatar" />
              <div class="user-info">
                <span class="user-name">兔兔酱</span>
                <span class="user-desc">养兔爱好者</span>
              </div>
              <button class="btn-follow">+ 关注</button>
            </div>
            <div class="user-item">
              <img src="https://api.dicebear.com/7.x/avataaars/svg?seed=3" class="user-avatar" />
              <div class="user-info">
                <span class="user-name">兔兔酱</span>
                <span class="user-desc">养兔爱好者</span>
              </div>
              <button class="btn-follow">+ 关注</button>
            </div>
          </div>
        </div>
      </aside>
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref } from "vue";
import { useRouter } from "vue-router";
import DataState from "@/components/DataState.vue";
import { fetchPosts, toggleLike } from "@/services/modules/community";
import { mockPosts } from "@/mocks/community";
import type { PostSummary } from "@/types/community";

const loading = ref(false);
const error = ref("");
const searchQuery = ref("");
const activeCategory = ref("推荐");
const currentPage = ref(1);
const posts = ref<any[]>([]);
const router = useRouter();

const categories = ["推荐", "晒宠", "问答", "种草", "日常", "知识", "视频", "好物"];

const loadPosts = async () => {
  loading.value = true;
  error.value = "";
  try {
    const data = await fetchPosts({ tab: activeCategory.value, page: currentPage.value, page_size: 20 });
    posts.value = data.list || [];
  } catch (e) {
    console.warn("Failed to fetch posts, using mock data", e);
    posts.value = mockPosts as any;
  } finally {
    loading.value = false;
  }
};

const openDetail = (postId: number) => {
  router.push(`/community/post/${postId}`);
};

const goToCreate = () => {
  router.push("/community/create");
};

onMounted(loadPosts);
</script>

<style scoped lang="scss">
.community-page {
  min-height: 100vh;
  background: var(--bg);
}

// 顶部搜索区域
.top-bar {
  background: #fff;
  border-bottom: 1px solid var(--border-warm);
}

.top-bar-inner {
  display: flex;
  justify-content: space-between;
  align-items: center;
  max-width: 100%;
  margin: 0 auto;
  padding: 16px 40px;
}

.logo {
  display: flex;
  align-items: center;
  gap: 10px;
  min-width: 180px;

  .logo-icon {
    font-size: 28px;
  }

  .logo-text {
    font-size: 20px;
    font-weight: 700;
    color: var(--text-heading);
  }
}

.search-box {
  flex: 1;
  max-width: 500px;
  display: flex;
  align-items: center;
  gap: 10px;
  background: var(--surface);
  border: 1px solid var(--border-warm);
  border-radius: 24px;
  padding: 10px 18px;
  margin: 0 40px;
  transition: all 0.2s ease;

  &:focus-within {
    border-color: var(--primary);
    box-shadow: 0 0 0 3px rgba(255, 155, 122, 0.15);
  }

  svg {
    width: 18px;
    height: 18px;
    color: var(--muted);
    flex-shrink: 0;
  }

  input {
    flex: 1;
    border: none;
    background: transparent;
    font-size: 14px;
    outline: none;

    &::placeholder {
      color: var(--muted-soft);
    }
  }
}

.btn-publish {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 10px 24px;
  background: var(--hero-gradient);
  color: #fff;
  border: none;
  border-radius: 20px;
  font-size: 14px;
  font-weight: 700;
  cursor: pointer;
  transition: all 0.2s ease;
  box-shadow: 0 4px 12px rgba(255, 155, 122, 0.3);
  min-width: 100px;
  justify-content: center;

  svg {
    width: 16px;
    height: 16px;
  }

  &:hover {
    transform: translateY(-2px);
    box-shadow: 0 6px 16px rgba(255, 155, 122, 0.4);
  }
}

// 内容区域
.content-wrapper {
  display: flex;
  padding: 24px 40px 40px;
}

// 主内容区
.main-content {
  flex: 1;
}

// 分类标签
.category-section {
  margin-bottom: 24px;
}

.category-tabs {
  display: flex;
  gap: 4px;
  background: var(--surface);
  padding: 6px;
  border-radius: 14px;
  box-shadow: 0 2px 8px rgba(34, 60, 52, 0.04);
}

.category-tab {
  flex: 1;
  padding: 12px 16px;
  background: transparent;
  border: none;
  border-radius: 10px;
  font-size: 14px;
  font-weight: 600;
  color: var(--muted);
  cursor: pointer;
  transition: all 0.2s ease;

  &:hover {
    background: var(--chip-bg);
    color: var(--text-heading);
  }

  &.active {
    background: var(--hero-gradient);
    color: #fff;
    box-shadow: 0 4px 12px rgba(255, 155, 122, 0.3);
  }
}

// 内容三栏网格
.content-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 16px;
}

.content-card {
  background: var(--surface);
  border-radius: 16px;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 4px 12px rgba(34, 60, 52, 0.06);

  &:hover {
    transform: translateY(-4px);
    box-shadow: 0 12px 30px rgba(34, 60, 52, 0.12);

    .card-image img {
      transform: scale(1.05);
    }
  }
}

.card-image {
  overflow: hidden;

  img {
    width: 100%;
    display: block;
    transition: transform 0.4s ease;
  }
}

.card-body {
  padding: 14px;
}

.card-title {
  font-size: 14px;
  font-weight: 700;
  color: var(--text-heading);
  margin: 0 0 12px;
  line-height: 1.4;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.card-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;

  .author {
    display: flex;
    align-items: center;
    gap: 6px;
    font-size: 12px;
    color: var(--muted);

    img {
      width: 20px;
      height: 20px;
      border-radius: 50%;
    }
  }

  .stats {
    display: flex;
    gap: 8px;
    font-size: 11px;
    color: var(--muted);
  }
}

// 页码切换
.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 8px;
  margin-top: 32px;
  padding: 20px 0;
}

.page-btn {
  min-width: 40px;
  height: 40px;
  padding: 0 12px;
  background: var(--surface);
  border: 1px solid var(--border-warm);
  border-radius: 10px;
  font-size: 14px;
  font-weight: 600;
  color: var(--muted);
  cursor: pointer;
  transition: all 0.2s ease;

  &:hover {
    border-color: var(--primary);
    color: var(--primary);
  }

  &.active {
    background: var(--primary);
    border-color: var(--primary);
    color: #fff;
  }
}

.page-ellipsis {
  color: var(--muted);
  font-size: 14px;
}

// 右侧边栏
.right-sidebar {
  width: 280px;
  flex-shrink: 0;
  margin-left: 20px;
}

.sidebar-card {
  background: var(--surface);
  border-radius: 16px;
  padding: 18px;
  margin-bottom: 16px;
  box-shadow: 0 4px 12px rgba(34, 60, 52, 0.06);
}

// 用户卡片
.user-card {
  .user-profile {
    display: flex;
    align-items: center;
    gap: 12px;
    margin-bottom: 16px;

    .user-avatar {
      width: 48px;
      height: 48px;
      border-radius: 50%;
      border: 2px solid var(--border-warm);
    }

    .user-info {
      display: flex;
      flex-direction: column;

      .user-name {
        font-size: 15px;
        font-weight: 700;
        color: var(--text-heading);
      }

      .user-level {
        font-size: 12px;
        color: var(--primary);
        font-weight: 600;
      }
    }
  }

  .user-stats {
    display: flex;
    justify-content: space-around;
    padding-top: 14px;
    border-top: 1px solid var(--border-warm);

    .stat-item {
      display: flex;
      flex-direction: column;
      align-items: center;
      gap: 2px;

      .stat-num {
        font-size: 16px;
        font-weight: 700;
        color: var(--text-heading);
      }

      .stat-label {
        font-size: 11px;
        color: var(--muted);
      }
    }
  }
}

.card-title-bar {
  font-size: 15px;
  font-weight: 700;
  color: var(--text-heading);
  margin: 0 0 14px;
  padding-bottom: 10px;
  border-bottom: 2px solid var(--primary);
  display: inline-block;
}

.topic-list {
  list-style: none;
  padding: 0;
  margin: 0;
}

.topic-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 10px 0;
  border-bottom: 1px solid var(--border-warm);
  cursor: pointer;
  transition: all 0.2s ease;

  &:last-child {
    border-bottom: none;
  }

  &:hover .topic-name {
    color: var(--primary);
  }

  .rank {
    width: 20px;
    height: 20px;
    background: rgba(255, 155, 122, 0.15);
    color: var(--primary);
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 11px;
    font-weight: 700;
  }

  .topic-name {
    font-size: 13px;
    color: var(--text);
    transition: color 0.2s ease;
  }
}

.user-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.user-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 6px;
  border-radius: 10px;
  transition: background 0.2s ease;

  &:hover {
    background: var(--surface-muted);
  }

  .user-avatar {
    width: 36px;
    height: 36px;
    border-radius: 50%;
    border: 2px solid var(--border-warm);
  }

  .user-info {
    flex: 1;
    display: flex;
    flex-direction: column;

    .user-name {
      font-size: 13px;
      font-weight: 700;
      color: var(--text-heading);
    }

    .user-desc {
      font-size: 11px;
      color: var(--muted);
    }
  }

  .btn-follow {
    padding: 5px 10px;
    background: var(--primary);
    color: #fff;
    border: none;
    border-radius: 12px;
    font-size: 11px;
    font-weight: 600;
    cursor: pointer;
    transition: all 0.2s ease;

    &:hover {
      opacity: 0.9;
      transform: scale(1.05);
    }
  }
}

// 响应式
@media (max-width: 1200px) {
  .right-sidebar {
    display: none;
  }

  .main-content {
    max-width: 100%;
  }
}

@media (max-width: 768px) {
  .top-bar-inner {
    padding: 12px 16px;
    flex-wrap: wrap;
    gap: 12px;
  }

  .logo {
    .logo-text {
      display: none;
    }
  }

  .search-box {
    order: 3;
    flex: 1 1 100%;
    margin: 0;
    max-width: none;
  }

  .btn-publish {
    padding: 10px 16px;
  }

  .content-wrapper {
    padding: 16px;
  }

  .category-tabs {
    overflow-x: auto;
    
    &::-webkit-scrollbar {
      display: none;
    }
  }

  .content-grid {
    grid-template-columns: repeat(2, 1fr);
    gap: 12px;
  }
}

@media (max-width: 480px) {
  .content-grid {
    grid-template-columns: 1fr;
  }
}
</style>
