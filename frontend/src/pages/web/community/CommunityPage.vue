<template>
  <section class="community-page">
    <div class="card page-hero">
      <h1>宠物社区</h1>
      <p>分享养宠经验，交流养宠心得</p>
    </div>

    <div class="tabs-row">
      <button 
        v-for="t in tabs" 
        :key="t.value" 
        :class="['tab-btn', { active: tab === t.value }]"
        @click="switchTab(t.value)"
      >
        {{ t.label }}
      </button>
    </div>

    <div class="content-grid">
      <main class="feed-column">
        <div class="create-post-card card">
          <div class="user-input">
            <img src="https://api.dicebear.com/7.x/avataaars/svg?seed=Felix" alt="Avatar" class="avatar-small" />
            <button class="input-trigger" @click="showCreateModal = true">分享你的养宠故事...</button>
          </div>
          <div class="quick-actions">
            <button class="action-btn">📷 相册</button>
            <button class="action-btn">📷 视频</button>
          </div>
        </div>

        <DataState :loading="loading" :error="error" :empty="posts.length === 0" empty-text="暂无帖子，快来发布第一篇吧">
          <div class="post-list">
            <article v-for="post in posts" :key="post.id" class="post-card">
              <div class="post-header">
                <img :src="post.author?.avatar_url || 'https://api.dicebear.com/7.x/avataaars/svg?seed=Felix'" class="avatar" />
                <div class="post-info">
                  <div class="author-name">{{ post.author?.nickname }}</div>
                  <div class="post-time">{{ post.published_at }}</div>
                </div>
                <span class="category-tag">{{ post.category }}</span>
              </div>
              
              <div class="post-content" @click="openDetail(post.id)">
                <h3>{{ post.title }}</h3>
                <p>{{ post.excerpt }}</p>
                <div v-if="post.cover_url" class="post-image">
                  <img :src="post.cover_url" />
                </div>
              </div>

              <div class="post-footer">
                <div class="tags" v-if="post.tags?.length">
                  <span v-for="tag in post.tags" :key="tag" class="tag">#{{ tag }}</span>
                </div>
                <div class="interaction-bar">
                  <button :class="['interact-btn', { active: post.is_liked }]" @click.stop="toggleLike(post)">
                    <span class="icon">👍</span> {{ post.like_count }}
                  </button>
                  <button class="interact-btn" @click.stop="openDetail(post.id)">
                    <span class="icon">💬</span> {{ post.comment_count }}
                  </button>
                  <button class="interact-btn">
                    <span class="icon">⭐</span> {{ post.favorite_count }}
                  </button>
                </div>
              </div>
            </article>
          </div>
        </DataState>
      </main>

      <aside class="sidebar">
        <div class="card hot-topics">
          <h3>热门话题</h3>
          <ul>
            <li><span class="rank">1</span> #新手养猫攻略</li>
            <li><span class="rank">2</span> #狗狗行为训练</li>
            <li><span class="rank">3</span> #自制宠物零食</li>
            <li><span class="rank">4</span> #春季驱虫</li>
            <li><span class="rank">5</span> #宠物摄影大赛</li>
          </ul>
        </div>
        
        <div class="card recommended-users">
          <h3>推荐关注</h3>
          <div class="user-list">
            <div class="user-item">
              <img src="https://api.dicebear.com/7.x/avataaars/svg?seed=1" class="avatar-mini" />
              <div class="user-info">
                <span class="name">喵星人</span>
                <span class="desc">分享养猫心得</span>
              </div>
              <button class="btn-follow">关注</button>
            </div>
            <div class="user-item">
              <img src="https://api.dicebear.com/7.x/avataaars/svg?seed=2" class="avatar-mini" />
              <div class="user-info">
                <span class="name">汪星人</span>
                <span class="desc">狗狗训练师</span>
              </div>
              <button class="btn-follow">关注</button>
            </div>
          </div>
        </div>
      </aside>
    </div>
  </section>
</template>

<script setup lang="ts">
import { onMounted, ref } from "vue";
import DataState from "@/components/DataState.vue";
import { createPost, fetchPosts, toggleLike } from "@/services/modules/community";
import { mockPosts } from "@/mocks/community";
import { toErrorMessage } from "@/services/http";
import type { PostSummary } from "@/types/community";

const loading = ref(false);
const error = ref("");
const tab = ref("recommended");
const posts = ref<PostSummary[]>([]);
const showCreateModal = ref(false);

const tabs = [
  { label: "推荐", value: "recommended" },
  { label: "最新", value: "latest" },
  { label: "知识", value: "knowledge" },
  { label: "日常", value: "daily" }
];

const loadPosts = async () => {
  loading.value = true;
  error.value = "";
  try {
    const data = await fetchPosts({ tab: tab.value, page: 1, page_size: 20 });
    posts.value = data.list || [];
  } catch (e) {
    console.warn("Failed to fetch posts, using mock data", e);
    posts.value = mockPosts as any;
  } finally {
    loading.value = false;
  }
};

const switchTab = (value: string) => {
  tab.value = value;
  loadPosts();
};

const openDetail = (postId: number) => {
  console.log("Open detail", postId);
  // In a real app, navigate to detail page or open modal
};

const toggleLike = async (post: any) => {
  try {
    await toggleLike(post.id);
    post.is_liked = !post.is_liked;
    post.like_count += post.is_liked ? 1 : -1;
  } catch (e) {
    // Mock toggle
    post.is_liked = !post.is_liked;
    post.like_count += post.is_liked ? 1 : -1;
  }
};

onMounted(loadPosts);
</script>

<style scoped lang="scss">
.community-page {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.tabs-row {
  display: flex;
  gap: 12px;
  border-bottom: 1px solid #f0dccb;
  padding-bottom: 12px;
}

.tab-btn {
  background: none;
  border: 1px solid transparent;
  padding: 8px 16px;
  font-size: 15px;
  color: var(--muted);
  cursor: pointer;
  border-radius: 20px;
  transition: all 0.2s;
  font-weight: 600;
  
  &:hover {
    background: var(--chip-bg);
    color: var(--text-heading-soft);
    border-color: var(--chip-border);
  }
  
  &.active {
    background: linear-gradient(135deg, var(--primary) 0%, var(--primary-strong) 100%);
    color: #fff;
    border-color: var(--primary-strong);
    box-shadow: 0 6px 14px rgba(241, 124, 83, 0.22);
  }
}

.content-grid {
  display: grid;
  grid-template-columns: 1fr 300px;
  gap: 24px;
}

.create-post-card {
  padding: 16px;
  margin-bottom: 20px;
}

.user-input {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 12px;
}

.avatar-small {
  width: 40px;
  height: 40px;
  border-radius: 50%;
}

.input-trigger {
  flex: 1;
  background: var(--surface-muted);
  border: none;
  border-radius: 20px;
  padding: 10px 16px;
  text-align: left;
  color: var(--muted-soft);
  cursor: pointer;
  font-size: 14px;
  
  &:hover {
    background: var(--surface-muted-hover);
  }
}

.quick-actions {
  display: flex;
  gap: 16px;
  padding-left: 52px;
}

.action-btn {
  background: none;
  border: none;
  color: #7d7068;
  font-size: 14px;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 4px;
}

.post-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.post-card {
  background: #fff;
  border-radius: 16px;
  padding: 20px;
  border: 1px solid #f0dccb;
  transition: all 0.2s;
  
  &:hover {
    box-shadow: 0 8px 24px rgba(128, 84, 52, 0.08);
  }
}

.post-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 16px;
}

.avatar {
  width: 44px;
  height: 44px;
  border-radius: 50%;
}

.post-info {
  flex: 1;
}

.author-name {
  font-weight: 600;
  color: #2f2a26;
}

.post-time {
  font-size: 12px;
  color: var(--on-white-text);
}

.category-tag {
  padding: 4px 10px;
  background: #fff1e5;
  color: #8a4f33;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 600;
}

.post-content {
  cursor: pointer;
  
  h3 {
    margin: 0 0 8px;
    font-size: 18px;
    color: #2f2a26;
  }
  
  p {
    margin: 0 0 12px;
    color: #7d7068;
    font-size: 14px;
    line-height: 1.5;
    display: -webkit-box;
    -webkit-line-clamp: 2;
    -webkit-box-orient: vertical;
    overflow: hidden;
  }
}

.post-image {
  border-radius: 12px;
  overflow: hidden;
  margin-bottom: 12px;
  max-height: 300px;
  
  img {
    width: 100%;
    height: 100%;
    object-fit: cover;
  }
}

.post-footer {
  margin-top: 12px;
}

.tags {
  display: flex;
  gap: 8px;
  margin-bottom: 12px;
  
  .tag {
    font-size: 13px;
    color: #ff9d7a;
  }
}

.interaction-bar {
  display: flex;
  gap: 24px;
  border-top: 1px solid #f5f0eb;
  padding-top: 12px;
}

.interact-btn {
  background: none;
  border: none;
  color: #7d7068;
  font-size: 14px;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 4px 8px;
  border-radius: 8px;
  transition: all 0.2s;
  
  &:hover {
    background: #fff8f5;
  }
  
  &.active {
    color: #ff9d7a;
  }
  
  .icon {
    font-size: 16px;
  }
}

.sidebar {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.hot-topics, .recommended-users {
  padding: 20px;
  
  h3 {
    margin: 0 0 16px;
    font-size: 16px;
    color: #2f2a26;
    border-left: 4px solid #ff9d7a;
    padding-left: 12px;
  }
  
  ul {
    list-style: none;
    padding: 0;
    margin: 0;
  }
  
  li {
    padding: 10px 0;
    border-bottom: 1px solid #f5f0eb;
    color: #7d7068;
    font-size: 14px;
    
    &:last-child {
      border-bottom: none;
    }
    
    .rank {
      display: inline-block;
      width: 20px;
      height: 20px;
      background: #ffecd2;
      color: #8d4d30;
      border-radius: 50%;
      text-align: center;
      line-height: 20px;
      font-size: 12px;
      font-weight: 700;
      margin-right: 8px;
    }
  }
}

.user-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.user-item {
  display: flex;
  align-items: center;
  gap: 10px;
}

.avatar-mini {
  width: 36px;
  height: 36px;
  border-radius: 50%;
}

.user-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  
  .name {
    font-size: 14px;
    font-weight: 600;
    color: #2f2a26;
  }
  
  .desc {
    font-size: 12px;
    color: var(--on-white-text);
  }
}

.btn-follow {
  padding: 4px 12px;
  background: #fff1e5;
  color: #8a4f33;
  border: 1px solid #ffd5b8;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 600;
  cursor: pointer;
  
  &:hover {
    background: #ffe9d7;
  }
}

@media (max-width: 1024px) {
  .content-grid {
    grid-template-columns: 1fr;
  }
  
  .sidebar {
    display: none;
  }
}
</style>