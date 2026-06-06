<template>
  <div class="post-detail-page">
    <!-- 顶部导航 -->
    <header class="detail-header">
      <button class="back-btn" @click="goBack">
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5">
          <path d="M15 18l-6-6 6-6"/>
        </svg>
      </button>
      <div class="header-actions">
        <button class="action-btn" @click="handleShare">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M4 12v8a2 2 0 002 2h12a2 2 0 002-2v-8M16 6l-4-4-4 4M12 2v13"/>
          </svg>
        </button>
        <button class="action-btn" :class="{ active: isCollected }" @click="toggleCollect">
          <svg viewBox="0 0 24 24" :fill="isCollected ? 'currentColor' : 'none'" stroke="currentColor" stroke-width="2">
            <path d="M19 21l-7-5-7 5V5a2 2 0 012-2h10a2 2 0 012 2z"/>
          </svg>
        </button>
      </div>
    </header>

    <!-- 加载状态 -->
    <div v-if="loading" class="loading-container">
      <div class="loading-shimmer"></div>
    </div>

    <!-- 主内容 -->
    <main v-else-if="post" class="detail-content">
      <!-- 封面图 -->
      <div v-if="postImages.length > 0" class="cover-section">
        <div class="cover-wrapper" :class="{ 'multi-images': postImages.length > 1 }">
          <img 
            v-if="postImages.length === 1" 
            :src="postImages[0]" 
            class="cover-img single" 
            alt="封面"
          />
          <template v-else>
            <div class="carousel" ref="carouselRef">
              <div 
                class="carousel-track" 
                :style="{ transform: `translateX(-${currentImageIndex * 100}%)` }"
              >
                <img 
                  v-for="(img, index) in postImages" 
                  :key="index"
                  :src="img" 
                  class="carousel-img" 
                  alt="图片"
                />
              </div>
            </div>
            <div class="carousel-indicators">
              <span 
                v-for="(_, index) in postImages" 
                :key="index"
                class="indicator"
                :class="{ active: index === currentImageIndex }"
              ></span>
            </div>
            <div class="image-count">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <rect x="3" y="3" width="18" height="18" rx="2"/>
                <circle cx="8.5" cy="8.5" r="1.5"/>
                <path d="M21 15l-5-5L5 21"/>
              </svg>
              {{ postImages.length }}
            </div>
          </template>
        </div>
      </div>

      <!-- 文章内容区 -->
      <div class="article-section">
        <!-- 分类标签 -->
        <div class="category-badge" :style="categoryStyle">
          <span>{{ post.category }}</span>
        </div>

        <!-- 标题 -->
        <h1 class="post-title">{{ post.title }}</h1>

        <!-- 作者信息卡片 -->
        <div class="author-card">
          <div class="author-main">
            <img 
              :src="post.author?.avatar_url || 'https://api.dicebear.com/7.x/avataaars/svg?seed=Felix'" 
              class="author-avatar" 
            />
            <div class="author-info">
              <span class="author-name">{{ post.author?.nickname || '匿名用户' }}</span>
              <span class="publish-time">{{ formatTime(post.published_at) }} · {{ postViews }}次浏览</span>
            </div>
          </div>
        </div>

        <!-- 正文内容 -->
        <div class="post-body">
          <p>{{ post.content || post.excerpt }}</p>
        </div>

        <!-- 话题标签 -->
        <div v-if="post.tags && post.tags.length > 0" class="tags-cloud">
          <span v-for="tag in post.tags" :key="tag" class="tag-chip"># {{ tag }}</span>
        </div>

        <!-- 互动数据 -->
        <div class="interaction-stats">
          <div class="stat-item">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M14 9V5a3 3 0 00-3-3l-4 9v11h11.28a2 2 0 002-1.7l1.38-9a2 2 0 00-2-2.3H14z"/>
            </svg>
            <span>{{ likeCount }}</span>
          </div>
          <div class="stat-item">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M21 11.5a8.38 8.38 0 01-.9 3.8 8.5 8.5 0 01-7.6 4.7 8.38 8.38 0 01-3.8-.9L3 21l1.9-5.7a8.38 8.38 0 01-.9-3.8 8.5 8.5 0 014.7-7.6 8.38 8.38 0 013.8-.9h.5a8.48 8.48 0 018 8v.5z"/>
            </svg>
            <span>{{ commentCount }}</span>
          </div>
          <div class="stat-item">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M19 21l-7-5-7 5V5a2 2 0 012-2h10a2 2 0 012 2z"/>
            </svg>
            <span>{{ post.favorite_count || 0 }}</span>
          </div>
        </div>
      </div>

      <!-- 推荐内容 -->
      <div class="recommend-section">
        <h3 class="section-title">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M23 7l-7 5 7 5V7z"/>
            <rect x="1" y="5" width="15" height="14" rx="2" ry="2"/>
          </svg>
          相关推荐
        </h3>
        <div class="recommend-list">
          <div v-for="item in recommendPosts" :key="item.id" class="recommend-item" @click="goToPost(item.id)">
            <img :src="item.cover_url" class="recommend-img" />
            <div class="recommend-info">
              <span class="recommend-title">{{ item.title }}</span>
              <span class="recommend-meta">{{ item.author?.nickname }} · {{ item.like_count }}赞</span>
            </div>
          </div>
        </div>
      </div>

      <!-- 评论区 -->
      <div id="comments" class="comments-section">
        <h3 class="section-title">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M21 11.5a8.38 8.38 0 01-.9 3.8 8.5 8.5 0 01-7.6 4.7 8.38 8.38 0 01-3.8-.9L3 21l1.9-5.7a8.38 8.38 0 01-.9-3.8 8.5 8.5 0 014.7-7.6 8.38 8.38 0 013.8-.9h.5a8.48 8.48 0 018 8v.5z"/>
          </svg>
          评论 ({{ commentCount }})
        </h3>

        <!-- 精彩评论 -->
        <div v-if="featuredComments.length > 0" class="featured-comments">
          <div v-for="comment in featuredComments" :key="comment.id" class="comment-card featured">
            <img :src="comment.user.avatar_url" class="comment-avatar" />
            <div class="comment-content">
              <div class="comment-header">
                <span class="comment-author">{{ comment.user.nickname }}</span>
                <span class="comment-badge" v-if="comment.isAuthor">作者</span>
              </div>
              <p class="comment-text">{{ comment.content }}</p>
              <div class="comment-footer">
                <span class="comment-time">{{ formatTime(comment.created_at) }}</span>
                <div class="comment-actions">
                  <button class="action-btn-text" :class="{ liked: comment.isLiked }" @click="toggleCommentLike(comment)">
                    <svg viewBox="0 0 24 24" :fill="comment.isLiked ? 'currentColor' : 'none'" stroke="currentColor" stroke-width="2">
                      <path d="M14 9V5a3 3 0 00-3-3l-4 9v11h11.28a2 2 0 002-1.7l1.38-9a2 2 0 00-2-2.3H14z"/>
                    </svg>
                    {{ comment.like_count || 0 }}
                  </button>
                  <button class="action-btn-text">
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

        <!-- 普通评论 -->
        <div class="comment-list">
          <div v-for="comment in normalComments" :key="comment.id" class="comment-card">
            <img :src="comment.user.avatar_url" class="comment-avatar" />
            <div class="comment-content">
              <div class="comment-header">
                <span class="comment-author">{{ comment.user.nickname }}</span>
                <span class="comment-badge" v-if="comment.isAuthor">作者</span>
              </div>
              <p class="comment-text">{{ comment.content }}</p>
              <div class="comment-footer">
                <span class="comment-time">{{ formatTime(comment.created_at) }}</span>
                <div class="comment-actions">
                  <button class="action-btn-text" :class="{ liked: comment.isLiked }" @click="toggleCommentLike(comment)">
                    <svg viewBox="0 0 24 24" :fill="comment.isLiked ? 'currentColor' : 'none'" stroke="currentColor" stroke-width="2">
                      <path d="M14 9V5a3 3 0 00-3-3l-4 9v11h11.28a2 2 0 002-1.7l1.38-9a2 2 0 00-2-2.3H14z"/>
                    </svg>
                    {{ comment.like_count || 0 }}
                  </button>
                  <button class="action-btn-text">
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

      <!-- 底部占位 -->
      <div class="bottom-placeholder"></div>
    </main>

    <!-- 底部固定互动栏 -->
    <footer class="fixed-action-bar" v-if="!loading && post">
      <div class="action-bar-inner">
        <div class="input-area" @click="focusComment">
          <img :src="'https://api.dicebear.com/7.x/avataaars/svg?seed=Felix'" class="user-avatar-small" />
          <span class="placeholder-text">说点什么...</span>
        </div>
        <div class="action-icons">
          <button class="icon-btn" :class="{ active: isLiked }" @click="handleToggleLike">
            <svg viewBox="0 0 24 24" :fill="isLiked ? 'currentColor' : 'none'" stroke="currentColor" stroke-width="2">
              <path d="M14 9V5a3 3 0 00-3-3l-4 9v11h11.28a2 2 0 002-1.7l1.38-9a2 2 0 00-2-2.3H14z"/>
            </svg>
            <span>{{ likeCount }}</span>
          </button>
          <button class="icon-btn" @click="scrollToComments">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M21 11.5a8.38 8.38 0 01-.9 3.8 8.5 8.5 0 01-7.6 4.7 8.38 8.38 0 01-3.8-.9L3 21l1.9-5.7a8.38 8.38 0 01-.9-3.8 8.5 8.5 0 014.7-7.6 8.38 8.38 0 013.8-.9h.5a8.48 8.48 0 018 8v.5z"/>
            </svg>
            <span>{{ commentCount }}</span>
          </button>
          <button class="icon-btn" :class="{ active: isCollected }" @click="toggleCollect">
            <svg viewBox="0 0 24 24" :fill="isCollected ? 'currentColor' : 'none'" stroke="currentColor" stroke-width="2">
              <path d="M19 21l-7-5-7 5V5a2 2 0 012-2h10a2 2 0 012 2z"/>
            </svg>
            <span>{{ post.favorite_count || 0 }}</span>
          </button>
        </div>
      </div>
    </footer>

    <!-- 评论输入弹窗 -->
    <Teleport to="body">
      <transition name="slide-up">
        <div v-if="commentPopupVisible" class="comment-popup">
          <div class="popup-header">
            <button class="cancel-btn" @click="closeCommentPopup">取消</button>
            <span class="popup-title">评论</span>
            <button class="send-btn" :disabled="!commentText.trim()" @click="submitComment">发布</button>
          </div>
          <div class="popup-content">
            <textarea 
              ref="commentInputRef"
              v-model="commentText" 
              placeholder="分享你的想法..." 
              class="popup-input"
              rows="4"
            ></textarea>
          </div>
        </div>
      </transition>
      <div v-if="commentPopupVisible" class="popup-overlay" @click="closeCommentPopup"></div>
    </Teleport>

    <!-- 分享弹窗 -->
    <Teleport to="body">
      <transition name="fade">
        <div v-if="sharePopupVisible" class="share-popup">
          <div class="share-header">
            <span>分享到</span>
            <button @click="sharePopupVisible = false">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M18 6L6 18M6 6l12 12"/>
              </svg>
            </button>
          </div>
          <div class="share-platforms">
            <button class="share-platform" @click="shareTo('wechat')">
              <div class="platform-icon wechat">
                <svg viewBox="0 0 24 24" fill="currentColor">
                  <path d="M8.5 11a1.5 1.5 0 100-3 1.5 1.5 0 000 3zm5 0a1.5 1.5 0 100-3 1.5 1.5 0 000 3z"/>
                  <path d="M12 2C6.477 2 2 6.145 2 11.243c0 2.936 1.444 5.544 3.7 7.254L4.788 21.5l3.605-1.053a11.2 11.2 0 003.607.553C11.766 21 12 11.243 12 11.243s-.234-9.819-4.5-9.819V2z"/>
                </svg>
              </div>
              <span>微信</span>
            </button>
            <button class="share-platform" @click="shareTo('weibo')">
              <div class="platform-icon weibo">
                <svg viewBox="0 0 24 24" fill="currentColor">
                  <path d="M10.098 20.323c-3.977.391-7.414-1.406-7.672-4.02-.259-2.609 2.759-5.047 6.74-5.441 3.979-.394 7.413 1.404 7.671 4.018.259 2.6-2.759 5.049-6.739 5.443zM9.05 17.219c-.384.616-1.208.884-1.829.602-.612-.279-.793-.991-.406-1.593.379-.595 1.176-.861 1.793-.601.622.263.82.972.442 1.592zm1.27-1.627c-.141.237-.449.353-.689.253-.236-.09-.313-.361-.177-.586.138-.227.436-.346.672-.24.239.09.315.36.194.573zm.176-2.719c-1.893-.493-4.033.45-4.857 2.118-.836 1.704-.026 3.591 1.886 4.21 1.983.64 4.318-.341 5.132-2.179.8-1.793-.201-3.642-2.161-4.149zm7.627-1.949c-.346-.105-.579-.18-.405-.649.381-1.017.424-1.893.009-2.521-.771-1.166-2.727-1.084-5.003-.034 0 0-.916.396-2.096-.12-1.181-.518-1.542-1.353-1.745-1.729-.203-.376-.237-.518-.629-.582-.392-.063-.476.105-.544.178-.067.074-.177.164-.18.37-.004.207.003.465.003.74-.001.276.002.59.002.965-.003.691-.009 1.482.003 2.286.011.804.035 1.621.125 2.373.091.753.243 1.413.536 1.858.293.445.736.741 1.33.861.594.12 1.337-.021 2.201-.46 0 0 .752-.374 1.677-.134 1.014.263 1.709.876 2.277 1.687.567.812.905 1.85 1.032 3.01.128 1.161.057 2.435-.241 3.646-.298 1.211-.806 2.262-1.566 2.992-1.519 1.459-3.495 1.66-4.732.896-.586-.361-.965-.866-1.237-1.463-.272-.596-.385-1.273-.423-1.938-.077-1.33.156-2.536.672-3.442.517-.905 1.29-1.41 2.286-1.479.997-.07 1.983.35 2.836 1.219.854.869 1.494 2.186 1.722 3.764.228 1.577.083 3.347-.562 4.87-.646 1.522-1.752 2.718-3.229 3.293-1.476.575-3.166.459-4.616-.378-1.449-.838-2.459-2.277-2.875-3.956-.416-1.679-.26-3.543.515-5.113.173-.351.035-.762-.312-.938-.347-.176-.766-.035-.938.312-.907 1.839-1.089 4.008-.594 5.943.495 1.935 1.664 3.637 3.362 4.624 1.698.986 3.676 1.113 5.393.385 1.717-.729 3.026-2.161 3.765-3.89.739-1.729.912-3.699.595-5.434-.317-1.735-1.091-3.248-2.139-4.242-1.048-.994-2.294-1.538-3.523-1.523-1.228.015-2.338.633-3.011 1.66-.673 1.027-.932 2.416-.856 3.905.076 1.489.403 3.021.873 4.309.47 1.287 1.071 2.363 1.895 2.994.825.63 1.881.737 2.873.359.992-.379 1.718-1.152 2.091-2.106.373-.955.449-2.055.373-3.106-.076-1.052-.302-2.052-.66-2.898-.359-.846-.851-1.55-1.447-2.027-.596-.478-1.278-.704-1.978-.63-.7.073-1.425.453-2.027 1.073-1.014 1.047-1.624 2.642-1.747 4.337-.123 1.694.215 3.45 1.001 4.79.786 1.339 1.961 2.263 3.294 2.526 1.333.264 2.682-.152 3.716-1.199 1.034-1.048 1.567-2.528 1.487-4.053-.08-1.525-.712-2.975-1.812-3.954z"/>
                </svg>
              </div>
              <span>微博</span>
            </button>
            <button class="share-platform" @click="shareTo('qq')">
              <div class="platform-icon qq">
                <svg viewBox="0 0 24 24" fill="currentColor">
                  <path d="M12 2C6.477 2 2 5.806 2 10.425c0 2.373 1.19 4.465 3.016 5.796-.154-.713-.273-1.426-.273-2.042 0-.616.119-1.329.273-2.042.615.41 1.232.82 1.847 1.026.82.41 1.54.82 2.155 1.026.82.41 1.54.205 1.54.205s-.41 1.026-.615 1.436c-.41.82-.82 1.435-1.232 2.255-.41 1.026-.41 2.052-.41 2.052.41-.41 1.025-.615 1.435-1.025.205-.41.41-.82.82-1.23.615-.615 1.23-.82 1.54-.82s.41.205.615.41c.41.205.82.41 1.23.615h.41c.205-.205.41-.41.615-.615.205-.41.41-.82.41-1.23.615.205 1.23.41 1.847.41 4.615 0 8.385-2.867 8.385-6.426C22 5.806 17.523 2 12 2z"/>
                </svg>
              </div>
              <span>QQ</span>
            </button>
            <button class="share-platform" @click="shareTo('copy')">
              <div class="platform-icon link">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <path d="M10 13a5 5 0 007.54.54l3-3a5 5 0 00-7.07-7.07l-1.72 1.71"/>
                  <path d="M14 11a5 5 0 00-7.54-.54l-3 3a5 5 0 007.07 7.07l1.71-1.71"/>
                </svg>
              </div>
              <span>复制链接</span>
            </button>
          </div>
        </div>
      </transition>
      <div v-if="sharePopupVisible" class="popup-overlay" @click="sharePopupVisible = false"></div>
    </Teleport>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, nextTick } from "vue";
import { useRoute, useRouter } from "vue-router";
import { fetchPostComments, fetchPostDetail, fetchPosts, toggleLike, createComment } from "@/api/modules/community";
import type { PostComment, PostDetail, PostSummary } from "@/types/community";

type CommentVm = PostComment & {
  user: NonNullable<PostComment["author"]>;
  like_count: number;
  isLiked: boolean;
  isAuthor: boolean;
};

const route = useRoute();
const router = useRouter();

const loading = ref(true);
const post = ref<PostDetail | null>(null);
const isLiked = ref(false);
const isCollected = ref(false);
const likeCount = ref(0);
const commentCount = ref(0);
const postViews = ref(0);
const commentText = ref("");
const currentImageIndex = ref(0);
const commentPopupVisible = ref(false);
const sharePopupVisible = ref(false);
const commentInputRef = ref<HTMLTextAreaElement | null>(null);
const commentsRef = ref<HTMLElement | null>(null);

const comments = ref<CommentVm[]>([]);

const recommendPosts = ref<PostSummary[]>([]);

const featuredComments = computed(() => comments.value.filter(c => c.like_count > 50).slice(0, 2));
const normalComments = computed(() => comments.value.filter(c => c.like_count <= 50));

const postImages = computed(() => {
  if (!post.value) return [];
  if (post.value.cover_url) return [post.value.cover_url];
  if (post.value.images) return post.value.images;
  return [];
});

const categoryStyle = computed(() => {
  const colors: Record<string, { bg: string; text: string }> = {
    '推荐': { bg: 'rgba(255, 155, 122, 0.1)', text: '#ff9b7a' },
    '晒宠': { bg: 'rgba(147, 112, 219, 0.1)', text: '#9370db' },
    '问答': { bg: 'rgba(64, 224, 208, 0.1)', text: '#40e0d0' },
    '种草': { bg: 'rgba(255, 105, 180, 0.1)', text: '#ff69b4' },
    '日常': { bg: 'rgba(100, 149, 237, 0.1)', text: '#6495ed' },
    '知识': { bg: 'rgba(60, 179, 113, 0.1)', text: '#3cb371' },
    '视频': { bg: 'rgba(255, 99, 71, 0.1)', text: '#ff6347' },
    '好物': { bg: 'rgba(255, 215, 0, 0.1)', text: '#ffd700' }
  };
  const style = colors[post.value?.category] || { bg: 'rgba(255, 155, 122, 0.1)', text: '#ff9b7a' };
  return {
    backgroundColor: style.bg,
    color: style.text
  };
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

const goToPost = (id: number) => {
  router.push(`/community/post/${id}`);
};

const handleToggleLike = async () => {
  if (!post.value) return;
  try {
    await toggleLike(post.value.id);
    isLiked.value = !isLiked.value;
    likeCount.value += isLiked.value ? 1 : -1;
  } catch {
    // 静默失败
  }
};

const toggleCollect = () => {
  isCollected.value = !isCollected.value;
};

const toggleCommentLike = (comment: any) => {
  comment.isLiked = !comment.isLiked;
  comment.like_count += comment.isLiked ? 1 : -1;
};

const scrollToComments = () => {
  document.querySelector('.comments-section')?.scrollIntoView({ behavior: 'smooth' });
};

const focusComment = () => {
  commentPopupVisible.value = true;
  nextTick(() => {
    commentInputRef.value?.focus();
  });
};

const closeCommentPopup = () => {
  commentPopupVisible.value = false;
  commentText.value = "";
};

const submitComment = async () => {
  if (!commentText.value.trim() || !post.value) return;
  
  try {
    await createComment(post.value.id, commentText.value);
    // 重新加载评论
    const commentPage = await fetchPostComments(post.value.id);
    comments.value = (commentPage.list || []).map((c: PostComment) => ({
      ...c,
      user: c.author || { id: 0, nickname: "匿名用户", avatar_url: "" },
      like_count: 0,
      isLiked: false,
      isAuthor: c.author?.id === post.value?.author?.id
    }));
    commentCount.value = comments.value.length;
    closeCommentPopup();
  } catch {
    // 静默失败
  }
};

const handleShare = () => {
  sharePopupVisible.value = true;
};

const shareTo = (platform: string) => {
  console.log("分享到:", platform);
  sharePopupVisible.value = false;
};

onMounted(() => {
  setTimeout(async () => {
    const postId = Number(route.params.id);
    let foundPost: PostDetail | null = null;
    try {
      foundPost = await fetchPostDetail(postId);
      const commentPage = await fetchPostComments(postId);
      comments.value = (commentPage.list || []).map((c: PostComment) => ({
        ...c,
        user: c.author || { id: 0, nickname: "匿名用户", avatar_url: "" },
        like_count: 0,
        isLiked: false,
        isAuthor: c.author?.id === foundPost?.author?.id
      }));
    } catch {
      comments.value = [];
    }
    if (foundPost) {
      post.value = foundPost;
      likeCount.value = foundPost.like_count || 0;
      commentCount.value = comments.value.length;
      postViews.value = (foundPost as any).view_count || 0;
    } else {
      post.value = null;
      likeCount.value = 0;
      commentCount.value = 0;
      postViews.value = 0;
    }

    try {
      const recommendPage = await fetchPosts({ page: 1, page_size: 5 });
      recommendPosts.value = (recommendPage.list || []).filter((p) => p.id !== postId).slice(0, 4);
    } catch {
      recommendPosts.value = [];
    }
    loading.value = false;

    // 如果 URL 包含 #comments，则滚动到评论区
    if (window.location.hash === '#comments') {
      nextTick(() => {
        document.getElementById('comments')?.scrollIntoView({ behavior: 'smooth' });
      });
    }
  }, 500);
});
</script>

<style scoped lang="scss">
.post-detail-page {
  min-height: 100vh;
  background: linear-gradient(180deg, var(--surface-muted) 0%, var(--bg) 100%);
  padding-bottom: 80px;
}

// 顶部导航
.detail-header {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 100;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 20px;
  background: var(--surface);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  border-bottom: 1px solid var(--border-warm);

  .back-btn {
    width: 40px;
    height: 40px;
    display: flex;
    align-items: center;
    justify-content: center;
    background: var(--surface-muted);
    border: 1px solid var(--border-warm);
    border-radius: 50%;
    cursor: pointer;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
    transition: all 0.2s ease;

    svg {
      width: 20px;
      height: 20px;
      color: var(--text-heading);
    }

    &:hover {
      transform: scale(1.05);
    }
  }

  .header-actions {
    display: flex;
    gap: 8px;
  }

  .action-btn {
    width: 40px;
    height: 40px;
    display: flex;
    align-items: center;
    justify-content: center;
    background: var(--surface-muted);
    border: 1px solid var(--border-warm);
    border-radius: 50%;
    cursor: pointer;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
    transition: all 0.2s ease;

    svg {
      width: 20px;
      height: 20px;
      color: var(--text);
    }

    &:hover {
      transform: scale(1.05);
      svg {
        color: var(--primary);
      }
    }

    &.active svg {
      color: var(--primary);
    }
  }
}

// 加载状态
.loading-container {
  padding-top: 60px;

  .loading-shimmer {
    height: 300px;
    background: linear-gradient(90deg, #f0f0f0 25%, #e0e0e0 50%, #f0f0f0 75%);
    background-size: 200% 100%;
    animation: shimmer 1.5s infinite;
  }
}

@keyframes shimmer {
  0% { background-position: 200% 0; }
  100% { background-position: -200% 0; }
}

// 主内容
.detail-content {
  padding-top: 64px;
}

// 封面图
.cover-section {
  .cover-wrapper {
    position: relative;
    width: 100%;

    .cover-img.single {
      width: 100%;
      max-height: 450px;
      object-fit: cover;
    }

    .carousel {
      position: relative;
      width: 100%;
      height: 350px;
      overflow: hidden;

      .carousel-track {
        display: flex;
        transition: transform 0.3s ease;
        height: 100%;
      }

      .carousel-img {
        width: 100%;
        height: 100%;
        object-fit: cover;
        flex-shrink: 0;
      }
    }

    .carousel-indicators {
      position: absolute;
      bottom: 16px;
      left: 50%;
      transform: translateX(-50%);
      display: flex;
      gap: 6px;

      .indicator {
        width: 6px;
        height: 6px;
        border-radius: 50%;
        background: var(--surface-muted);
        transition: all 0.2s ease;

        &.active {
          width: 18px;
          border-radius: 3px;
          background: var(--surface);
        }
      }
    }

    .image-count {
      position: absolute;
      bottom: 16px;
      right: 16px;
      display: flex;
      align-items: center;
      gap: 4px;
      padding: 6px 12px;
      background: rgba(0, 0, 0, 0.5);
      border-radius: 20px;
      color: #fff;
      font-size: 12px;

      svg {
        width: 14px;
        height: 14px;
      }
    }
  }
}

// 文章内容区
.article-section {
  padding: 24px 20px;
  background: var(--surface);
  border-radius: 24px 24px 0 0;
  margin-top: -20px;
  position: relative;
  box-shadow: 0 -4px 20px rgba(0, 0, 0, 0.05);
  border: 1px solid var(--border-warm);
  border-bottom: none;

  .category-badge {
    display: inline-flex;
    align-items: center;
    padding: 6px 14px;
    border-radius: 20px;
    font-size: 13px;
    font-weight: 600;
    margin-bottom: 16px;
  }

  .post-title {
    font-size: 24px;
    font-weight: 700;
    color: var(--text-heading);
    line-height: 1.4;
    margin: 0 0 20px;
  }

  .author-card {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 16px;
    background: var(--bg);
    border-radius: 16px;
    margin-bottom: 20px;

    .author-main {
      display: flex;
      align-items: center;
      gap: 12px;

      .author-avatar {
        width: 48px;
        height: 48px;
        border-radius: 50%;
        border: 2px solid #fff;
        box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
      }

      .author-info {
        display: flex;
        flex-direction: column;
        gap: 2px;

        .author-name {
          font-size: 15px;
          font-weight: 600;
          color: var(--text-heading);
        }

        .publish-time {
          font-size: 12px;
          color: var(--muted);
        }
      }
    }

  }

  .post-body {
    margin-bottom: 20px;

    p {
      font-size: 15px;
      color: var(--text);
      line-height: 1.9;
      margin: 0;
    }
  }

  .tags-cloud {
    display: flex;
    flex-wrap: wrap;
    gap: 10px;
    margin-bottom: 20px;

    .tag-chip {
      padding: 8px 16px;
      background: rgba(255, 155, 122, 0.1);
      color: var(--primary);
      border-radius: 20px;
      font-size: 13px;
      font-weight: 500;
      transition: all 0.2s ease;

      &:hover {
        background: rgba(255, 155, 122, 0.2);
      }
    }
  }

  .interaction-stats {
    display: flex;
    gap: 24px;
    padding: 16px 0;
    border-top: 1px solid var(--border-warm);

    .stat-item {
      display: flex;
      align-items: center;
      gap: 6px;
      color: var(--muted);
      font-size: 14px;

      svg {
        width: 18px;
        height: 18px;
      }
    }
  }
}

// 推荐内容
.recommend-section {
  padding: 20px;
  background: var(--bg);

  .section-title {
    display: flex;
    align-items: center;
    gap: 8px;
    font-size: 16px;
    font-weight: 600;
    color: var(--text-heading);
    margin: 0 0 16px;

    svg {
      width: 18px;
      height: 18px;
      color: var(--primary);
    }
  }

  .recommend-list {
    display: flex;
    gap: 12px;
    overflow-x: auto;
    padding-bottom: 8px;
    margin: 0 -20px;
    padding-left: 20px;
    padding-right: 20px;

    &::-webkit-scrollbar {
      display: none;
    }

    .recommend-item {
      flex-shrink: 0;
      width: 140px;
      background: var(--surface);
      border-radius: 12px;
      overflow: hidden;
      cursor: pointer;
      transition: transform 0.2s ease;
      box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
      border: 1px solid var(--border-warm);

      &:hover {
        transform: translateY(-4px);
      }

      .recommend-img {
        width: 100%;
        height: 90px;
        object-fit: cover;
      }

      .recommend-info {
        padding: 10px;

        .recommend-title {
          display: block;
          font-size: 13px;
          font-weight: 500;
          color: var(--text-heading);
          line-height: 1.4;
          overflow: hidden;
          text-overflow: ellipsis;
          white-space: nowrap;
          margin-bottom: 4px;
        }

        .recommend-meta {
          font-size: 11px;
          color: var(--muted);
        }
      }
    }
  }
}

// 评论区
.comments-section {
  padding: 20px;
  background: var(--surface);
  border-top: 1px solid var(--border-warm);

  .section-title {
    display: flex;
    align-items: center;
    gap: 8px;
    font-size: 16px;
    font-weight: 600;
    color: var(--text-heading);
    margin: 0 0 20px;

    svg {
      width: 18px;
      height: 18px;
      color: var(--primary);
    }
  }

  .featured-comments {
    margin-bottom: 24px;
    padding-bottom: 24px;
    border-bottom: 1px solid var(--border-warm);
  }

  .comment-card {
    display: flex;
    gap: 12px;
    margin-bottom: 20px;

    .comment-avatar {
      width: 40px;
      height: 40px;
      border-radius: 50%;
      flex-shrink: 0;
    }

    .comment-content {
      flex: 1;
      min-width: 0;

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

        .comment-badge {
          padding: 2px 8px;
          background: linear-gradient(135deg, #ff9b7a 0%, #ff6b6b 100%);
          color: #fff;
          border-radius: 10px;
          font-size: 10px;
          font-weight: 600;
        }
      }

      .comment-text {
        font-size: 14px;
        color: var(--text);
        line-height: 1.6;
        margin: 0 0 10px;
      }

      .comment-footer {
        display: flex;
        justify-content: space-between;
        align-items: center;

        .comment-time {
          font-size: 12px;
          color: var(--muted);
        }

        .comment-actions {
          display: flex;
          gap: 16px;

          .action-btn-text {
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

            &.liked {
              color: var(--primary);
            }
          }
        }
      }
    }
  }
}

.bottom-placeholder {
  height: 20px;
}

// 底部固定操作栏
.fixed-action-bar {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  z-index: 100;
  background: var(--surface);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  border-top: 1px solid var(--border-warm);
  padding: 12px 20px;
  padding-bottom: calc(12px + env(safe-area-inset-bottom));

  .action-bar-inner {
    display: flex;
    align-items: center;
    gap: 16px;
    max-width: 600px;
    margin: 0 auto;

    .input-area {
      flex: 1;
      display: flex;
      align-items: center;
      gap: 10px;
      padding: 10px 16px;
      background: var(--bg);
      border-radius: 24px;
      cursor: pointer;

      .user-avatar-small {
        width: 28px;
        height: 28px;
        border-radius: 50%;
      }

      .placeholder-text {
        font-size: 14px;
        color: var(--muted);
      }
    }

    .action-icons {
      display: flex;
      gap: 16px;

      .icon-btn {
        display: flex;
        flex-direction: column;
        align-items: center;
        gap: 2px;
        padding: 0;
        background: none;
        border: none;
        color: var(--muted);
        font-size: 11px;
        cursor: pointer;
        transition: color 0.2s ease;

        svg {
          width: 22px;
          height: 22px;
        }

        &:hover {
          color: var(--primary);
        }

        &.active {
          color: var(--primary);
        }
      }
    }
  }
}

// 评论弹窗
.comment-popup {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  z-index: 200;
  background: var(--surface);
  border-radius: 24px 24px 0 0;
  padding: 16px;
  padding-bottom: calc(16px + env(safe-area-inset-bottom));
  border-top: 1px solid var(--border-warm);

  .popup-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 16px;

    .cancel-btn {
      padding: 0;
      background: none;
      border: none;
      font-size: 15px;
      color: var(--muted);
      cursor: pointer;
    }

    .popup-title {
      font-size: 16px;
      font-weight: 600;
      color: var(--text-heading);
    }

    .send-btn {
      padding: 8px 20px;
      background: linear-gradient(135deg, #ff9b7a 0%, #ff6b6b 100%);
      color: #fff;
      border: none;
      border-radius: 20px;
      font-size: 14px;
      font-weight: 600;
      cursor: pointer;

      &:disabled {
        background: var(--surface-muted);
        cursor: not-allowed;
      }
    }
  }

  .popup-input {
    width: 100%;
    padding: 16px;
    background: var(--bg);
    border: none;
    border-radius: 16px;
    font-size: 15px;
    line-height: 1.6;
    resize: none;
    outline: none;

    &::placeholder {
      color: var(--muted);
    }
  }
}

.popup-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  z-index: 150;
  background: rgba(0, 0, 0, 0.4);
}

// 分享弹窗
.share-popup {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  z-index: 200;
  background: var(--surface);
  border-radius: 24px 24px 0 0;
  padding: 24px;
  padding-bottom: calc(24px + env(safe-area-inset-bottom));
  border-top: 1px solid var(--border-warm);

  .share-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 24px;

    span {
      font-size: 16px;
      font-weight: 600;
      color: var(--text-heading);
    }

    button {
      width: 32px;
      height: 32px;
      display: flex;
      align-items: center;
      justify-content: center;
      background: var(--bg);
      border: none;
      border-radius: 50%;
      cursor: pointer;

      svg {
        width: 18px;
        height: 18px;
        color: var(--muted);
      }
    }
  }

  .share-platforms {
    display: flex;
    justify-content: space-around;

    .share-platform {
      display: flex;
      flex-direction: column;
      align-items: center;
      gap: 8px;
      background: none;
      border: none;
      cursor: pointer;

      .platform-icon {
        width: 56px;
        height: 56px;
        display: flex;
        align-items: center;
        justify-content: center;
        border-radius: 50%;
        margin-bottom: 4px;

        svg {
          width: 28px;
          height: 28px;
        }

        &.wechat {
          background: #07c160;
          color: #fff;
        }

        &.weibo {
          background: #e6162d;
          color: #fff;
        }

        &.qq {
          background: #1296db;
          color: #fff;
        }

        &.link {
          background: var(--bg);
          color: var(--text);
        }
      }

      span {
        font-size: 12px;
        color: var(--text);
      }
    }
  }
}

// 动画
.slide-up-enter-active,
.slide-up-leave-active {
  transition: transform 0.3s ease;
}

.slide-up-enter-from,
.slide-up-leave-to {
  transform: translateY(100%);
}

.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

// 响应式
@media (max-width: 768px) {
  .article-section {
    .post-title {
      font-size: 20px;
    }

    .author-card {
      flex-direction: column;
      align-items: flex-start;
      gap: 12px;
    }
  }

  .recommend-section {
    .recommend-list {
      .recommend-item {
        width: 120px;

        .recommend-img {
          height: 75px;
        }
      }
    }
  }
}
</style>
