<template>
  <section class="home-page">
    <!-- Hero Section -->
    <section class="hero-section">
      <div class="hero-content">
        <div class="hero-text">
          <h1>寻找你和爱宠的快乐时光</h1>
          <p>一站式宠物综合服务平台</p>
          <div class="hero-actions">
            <RouterLink to="/adoption" class="btn-hero-primary">领养萌宠</RouterLink>
            <RouterLink to="/community" class="btn-hero-secondary">进入社区</RouterLink>
          </div>
        </div>
        <div class="hero-image">
          <img src="https://images.unsplash.com/photo-1450778869180-41d0601e046e?auto=format&fit=crop&w=600&q=80" alt="Pet" />
        </div>
      </div>
    </section>

    <!-- Quick Entries -->
    <section class="section">
      <div class="entry-grid">
        <RouterLink v-for="entry in entries" :key="entry.code" :to="entry.path" class="entry-card">
          <div class="entry-icon" :class="entry.code">
            <img v-if="entry.code === 'community'" src="https://img.icons8.com/ios-filled/50/7ECFBC/chat.png" alt="Community" />
            <img v-else-if="entry.code === 'adoption'" src="https://img.icons8.com/ios-filled/50/FFD66B/dog.png" alt="Adoption" />
            <img v-else-if="entry.code === 'services'" src="https://img.icons8.com/ios-filled/50/7ECFBC/wrench.png" alt="Services" />
            <img v-else-if="entry.code === 'shop'" src="https://img.icons8.com/ios-filled/50/7ECFBC/shopping-bag.png" alt="Shop" />
          </div>
          <strong>{{ entry.title }}</strong>
        </RouterLink>
      </div>
    </section>

    <!-- Pet Cards -->
    <section class="section">
      <div class="section-header">
        <h2 class="section-title">
          <span class="title-indicator"></span>
          萌宠展示
        </h2>
        <div class="pet-filter-tabs">
          <button
            v-for="t in petFilterTypes"
            :key="t.value"
            :class="['pet-filter-tab', { active: activePetFilter === t.value }]"
            @click="activePetFilter = t.value"
          >
            {{ t.label }}
          </button>
        </div>
      </div>
      <div class="pet-card-grid">
        <div
          v-for="pet in filteredPetCards"
          :key="pet.title"
          class="pet-card-item"
          @click="router.push('/adoption')"
        >
          <div class="pet-card-image">
            <img :src="pet.image_url" :alt="pet.title" />
          </div>
          <div class="pet-card-body">
            <h4>{{ pet.title }}</h4>
            <p>{{ pet.subtitle }}</p>
          </div>
        </div>
      </div>
    </section>

    <!-- Recommended Posts -->
    <section class="section">
      <div class="section-header">
        <h2 class="section-title">
          <span class="title-indicator"></span>
          热门社区
        </h2>
        <RouterLink to="/community" class="view-all">查看全部 →</RouterLink>
      </div>
      <div class="post-grid">
        <article
          v-for="post in recommendedPosts"
          :key="post.id"
          class="post-card"
        >
          <RouterLink :to="`/community/post/${post.id}`" class="post-link">
            <div class="post-image">
              <img :src="post.cover_url" :alt="post.title" />
            </div>
            <div class="post-content">
              <h3>{{ post.title }}</h3>
              <div class="post-meta">
                <div class="author">
                  <img :src="post.author?.avatar_url" alt="Avatar" />
                  <span>{{ post.author?.nickname }}</span>
                </div>
                <div class="post-stats">
                  <span
                    class="stat-btn like-btn"
                    :class="{ liked: post.isLiked }"
                    @click.stop="togglePostLike(post)"
                  >
                    <svg viewBox="0 0 24 24" :fill="post.isLiked ? 'currentColor' : 'none'" stroke="currentColor" stroke-width="2">
                      <path d="M14 9V5a3 3 0 00-3-3l-4 9v11h11.28a2 2 0 002-1.7l1.38-9a2 2 0 00-2-2.3H14z"/>
                    </svg>
                    {{ post.like_count }}
                  </span>
                  <span
                    class="stat-btn"
                    @click.stop="goToPostComments(post.id)"
                  >
                    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                      <path d="M21 11.5a8.38 8.38 0 01-.9 3.8 8.5 8.5 0 01-7.6 4.7 8.38 8.38 0 01-3.8-.9L3 21l1.9-5.7a8.38 8.38 0 01-.9-3.8 8.5 8.5 0 014.7-7.6 8.38 8.38 0 013.8-.9h.5a8.48 8.48 0 018 8v.5z"/>
                    </svg>
                    {{ post.comment_count }}
                  </span>
                </div>
              </div>
            </div>
          </RouterLink>
        </article>
      </div>
    </section>

    <!-- Recommended Services -->
    <section class="section">
      <div class="section-header">
        <h2 class="section-title">
          <span class="title-indicator"></span>
          推荐服务
        </h2>
        <RouterLink to="/services" class="view-all">查看全部 →</RouterLink>
      </div>
      <div class="service-grid">
        <RouterLink
          v-for="service in recommendedServices"
          :key="service.id"
          :to="`/services/merchant/${service.id}`"
          class="service-card"
        >
          <div class="service-image">
            <img :src="service.cover_url || '/static/images/merchant-grooming.svg'" :alt="service.name" />
            <div class="service-rating">
              <span class="star">⭐</span> {{ service.score || service.rating }}
            </div>
          </div>
          <div class="service-info">
            <h3>{{ service.name }}</h3>
            <p class="service-location">📍 {{ service.district || service.location }}</p>
          </div>

        </RouterLink>
      </div>
    </section>

    <!-- Recommended Products -->
    <section class="section">
      <div class="section-header">
        <h2 class="section-title">
          <span class="title-indicator"></span>
          热门商品
        </h2>
        <RouterLink to="/shop" class="view-all">查看全部 →</RouterLink>
      </div>
      <div class="product-grid">
        <RouterLink
          v-for="product in recommendedProducts"
          :key="product.id"
          :to="`/shop/product/${product.id}`"
          class="product-card"
        >
          <div class="product-image">
            <img :src="product.image_url" :alt="product.name" />
          </div>
          <div class="product-info">
            <h3>{{ product.name }}</h3>
            <div class="product-bottom">
              <span class="price">¥{{ product.price }}</span>
            </div>
          </div>
        </RouterLink>
      </div>
    </section>
  </section>
</template>

<script setup lang="ts">
import { onMounted, ref, computed } from "vue";
import { useRouter } from "vue-router";
import { fetchHomeData } from "@/api/modules/home";
import { toggleLike } from "@/api/modules/community";
import type { HomeBanner, HomeQuickEntry, HomeTip, PetCard } from "@/types/home";
import { toErrorMessage } from "@/api/http";

const router = useRouter();
const loading = ref(false);
const error = ref("");
const entries = ref<HomeQuickEntry[]>([]);
const banners = ref<HomeBanner[]>([]);
const tips = ref<HomeTip[]>([]);
const recommendedPosts = ref<any[]>([]);
const recommendedServices = ref<any[]>([]);
const recommendedProducts = ref<any[]>([]);
const petCards = ref<PetCard[]>([]);

// 宠物类型筛选
const activePetFilter = ref("all");
const petFilterTypes = [
  { label: "全部", value: "all" },
  { label: "猫咪", value: "cat" },
  { label: "狗狗", value: "dog" },
  { label: "其他", value: "other" }
];
const filteredPetCards = computed(() => {
  if (activePetFilter.value === "all") return petCards.value;
  return petCards.value.filter(p => p.title?.includes(activePetFilter.value === "cat" ? "猫" : activePetFilter.value === "dog" ? "狗" : ""));
});

const togglePostLike = async (post: any) => {
  try {
    await toggleLike(post.id);
    post.isLiked = !post.isLiked;
    post.like_count += post.isLiked ? 1 : -1;
  } catch {
    // ignore
  }
};

const goToPostComments = (postId: number) => {
  router.push(`/community/post/${postId}#comments`);
};

onMounted(async () => {
  loading.value = true;
  error.value = "";
  try {
    const data = await fetchHomeData();
    entries.value = data.quick_entries || [];
    banners.value = data.banners || [];
    tips.value = data.tips || [];
    recommendedPosts.value = data.recommended_posts || [];
    recommendedServices.value = data.recommended_services || [];
    recommendedProducts.value = data.recommended_products || [];
    petCards.value = data.pet_cards || [];
  } catch (e) {
    error.value = toErrorMessage(e);
    entries.value = [];
    banners.value = [];
    tips.value = [];
    recommendedPosts.value = [];
    recommendedServices.value = [];
    recommendedProducts.value = [];
    petCards.value = [];
  } finally {
    loading.value = false;
  }
});
</script>

<style scoped lang="scss">
.home-page {
  display: flex;
  flex-direction: column;
  gap: 32px;
  max-width: 1200px;
  margin: 0 auto;
  padding-bottom: 100px;
}

.hero-section {
  position: relative;
}

.hero-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: var(--surface);
  border-radius: 20px;
  overflow: hidden;
  box-shadow: 0 8px 24px rgba(34, 60, 52, 0.08);
  min-height: 360px;
}

.hero-text {
  padding: 48px;
  flex: 1;
  
  h1 {
    font-size: 36px;
    font-weight: 800;
    color: var(--text-heading);
    margin: 0 0 16px;
    line-height: 1.3;
  }
  
  p {
    font-size: 18px;
    color: var(--muted);
    margin: 0 0 32px;
  }
}

.hero-actions {
  display: flex;
  gap: 16px;
}

.btn-hero-primary {
  padding: 14px 28px;
  background: var(--primary);
  color: #fff;
  border-radius: 12px;
  font-weight: 700;
  font-size: 16px;
  transition: all 0.3s ease;
  box-shadow: 0 4px 12px rgba(255, 155, 122, 0.3);
  
  &:hover {
    transform: translateY(-2px);
    box-shadow: 0 8px 20px rgba(255, 155, 122, 0.4);
  }
}

.btn-hero-secondary {
  padding: 14px 28px;
  background: var(--chip-bg);
  color: var(--text-heading);
  border-radius: 12px;
  font-weight: 700;
  font-size: 16px;
  border: 1px solid var(--border-warm);
  transition: all 0.3s ease;
  
  &:hover {
    background: var(--primary);
    color: #fff;
    border-color: var(--primary);
  }
}

.hero-image {
  width: 45%;
  height: 100%;
  min-height: 360px;
  position: relative;
  
  img {
    width: 100%;
    height: 100%;
    object-fit: cover;
  }
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.section-title {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 22px;
  font-weight: 700;
  color: var(--text-heading);
  
  .title-indicator {
    width: 6px;
    height: 24px;
    background: var(--primary);
    border-radius: 3px;
  }
}

.view-all {
  font-size: 14px;
  color: var(--primary);
  font-weight: 600;
  
  &:hover {
    text-decoration: underline;
  }
}

.entry-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
}

.entry-card {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 16px;
  padding: 28px 16px;
  background: var(--surface);
  border-radius: 16px;
  box-shadow: 0 4px 16px rgba(34, 60, 52, 0.06);
  transition: all 0.3s ease;
  cursor: pointer;
  
  &:hover {
    transform: translateY(-6px);
    box-shadow: 0 12px 30px rgba(34, 60, 52, 0.12);
    
    .entry-icon {
      background: var(--primary);
      
      img {
        filter: brightness(0) invert(1);
      }
    }
  }
}

.entry-icon {
  width: 64px;
  height: 64px;
  border-radius: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(255, 155, 122, 0.15);
  transition: all 0.3s ease;
  
  img {
    width: 32px;
    height: 32px;
  }
  
  &.community { background: rgba(255, 155, 122, 0.15); }
  &.adoption { background: rgba(255, 214, 107, 0.15); }
  &.services { background: rgba(255, 155, 122, 0.15); }
  &.shop { background: rgba(255, 155, 122, 0.15); }
}

// 宠物卡片区域
.pet-filter-tabs {
  display: flex;
  gap: 8px;
}

.pet-filter-tab {
  padding: 6px 16px;
  border: 1px solid var(--border-warm);
  border-radius: 20px;
  background: var(--surface);
  color: var(--muted);
  font-size: 13px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s ease;

  &:hover {
    border-color: var(--primary);
    color: var(--primary);
  }

  &.active {
    background: var(--primary);
    color: #fff;
    border-color: var(--primary);
  }
}

.pet-card-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
}

.pet-card-item {
  border-radius: 16px;
  overflow: hidden;
  background: var(--surface);
  box-shadow: 0 4px 16px rgba(34, 60, 52, 0.06);
  cursor: pointer;
  transition: all 0.3s ease;

  &:hover {
    transform: translateY(-4px);
    box-shadow: 0 12px 30px rgba(34, 60, 52, 0.12);
  }
}

.pet-card-image {
  height: 160px;
  overflow: hidden;

  img {
    width: 100%;
    height: 100%;
    object-fit: cover;
    transition: transform 0.5s ease;
  }
}

.pet-card-item:hover .pet-card-image img {
  transform: scale(1.05);
}

.pet-card-body {
  padding: 12px 16px;

  h4 {
    margin: 0 0 4px;
    font-size: 15px;
    font-weight: 700;
    color: var(--text-heading);
  }

  p {
    margin: 0;
    font-size: 12px;
    color: var(--muted);
  }
}

.post-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 24px;
}

.post-card {
  border-radius: 16px;
  overflow: hidden;
  background: var(--surface);
  box-shadow: 0 4px 16px rgba(34, 60, 52, 0.06);
  transition: all 0.3s ease;
  cursor: pointer;
  text-decoration: none;
  color: inherit;
  display: block;
  
  &:hover {
    transform: translateY(-6px);
    box-shadow: 0 12px 30px rgba(34, 60, 52, 0.12);
    
    .post-image img {
      transform: scale(1.05);
    }
  }
}

.post-image {
  height: 180px;
  overflow: hidden;
  
  img {
    width: 100%;
    height: 100%;
    object-fit: cover;
    transition: transform 0.5s ease;
  }
}

.post-content {
  padding: 16px;
  
  h3 {
    margin: 0 0 12px;
    font-size: 16px;
    font-weight: 700;
    line-height: 1.4;
    color: var(--text-heading);
    display: -webkit-box;
    -webkit-line-clamp: 2;
    -webkit-box-orient: vertical;
    overflow: hidden;
  }
}

.post-meta {
  display: flex;
  align-items: center;
  justify-content: space-between;
  font-size: 13px;
  color: var(--muted);
}

.post-stats {
  display: flex;
  gap: 12px;
}

.stat-btn {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 14px;
  color: var(--muted);
  cursor: pointer;
  transition: color 0.2s ease;
  user-select: none;

  svg {
    width: 16px;
    height: 16px;
  }

  &:hover {
    color: var(--primary);
  }

  &.liked {
    color: #ff4d4f;
  }
}

.like-btn.liked svg {
  fill: #ff4d4f;
}

.author {
  display: flex;
  align-items: center;
  gap: 8px;
  
  img {
    width: 24px;
    height: 24px;
    border-radius: 50%;
  }
}

.stats {
  display: flex;
  gap: 12px;
}

.service-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20px;
}

.service-card {
  display: flex;
  gap: 16px;
  padding: 16px;
  background: var(--surface);
  border-radius: 16px;
  box-shadow: 0 4px 16px rgba(34, 60, 52, 0.06);
  transition: all 0.3s ease;
  cursor: pointer;
  text-decoration: none;
  color: inherit;
  
  &:hover {
    transform: translateY(-4px);
    box-shadow: 0 12px 30px rgba(34, 60, 52, 0.12);
  }
}

.service-image {
  width: 120px;
  height: 120px;
  border-radius: 12px;
  overflow: hidden;
  position: relative;
  flex-shrink: 0;
  
  img {
    width: 100%;
    height: 100%;
    object-fit: cover;
  }
}

.service-rating {
  position: absolute;
  top: 8px;
  right: 8px;
  background: rgba(255, 255, 255, 0.95);
  padding: 4px 8px;
  border-radius: 8px;
  font-size: 13px;
  font-weight: 700;
  color: var(--rating);
  display: flex;
  align-items: center;
  gap: 4px;
}

.service-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: center;
  
  h3 {
    margin: 0 0 8px;
    font-size: 18px;
    font-weight: 700;
    color: var(--text-heading);
  }
}

.service-location {
  margin: 0 0 12px;
  font-size: 14px;
  color: var(--muted);
}

.service-tags {
  display: flex;
  gap: 8px;
  
  .tag {
    padding: 4px 10px;
    background: rgba(255, 155, 122, 0.15);
    color: var(--primary);
    border-radius: 20px;
    font-size: 12px;
  }
}

.product-grid {
  display: grid;
  grid-template-columns: repeat(5, 1fr);
  gap: 16px;
}

.product-card {
  border-radius: 16px;
  overflow: hidden;
  background: var(--surface);
  box-shadow: 0 4px 16px rgba(34, 60, 52, 0.06);
  transition: all 0.3s ease;
  cursor: pointer;
  text-decoration: none;
  color: inherit;
  
  &:hover {
    transform: translateY(-6px);
    box-shadow: 0 12px 30px rgba(34, 60, 52, 0.12);
    
    .product-image img {
      transform: scale(1.05);
    }
  }
}

.product-image {
  height: 180px;
  position: relative;
  overflow: hidden;
  background: var(--surface-muted);
  
  img {
    width: 100%;
    height: 100%;
    object-fit: cover;
    transition: transform 0.5s ease;
  }
}

.product-badge {
  position: absolute;
  top: 12px;
  left: 12px;
  background: var(--accent);
  color: var(--text-heading);
  padding: 4px 12px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 700;
}

.product-info {
  padding: 16px;
  
  h3 {
    margin: 0 0 12px;
    font-size: 15px;
    font-weight: 700;
    color: var(--text-heading);
    line-height: 1.4;
    height: 42px;
    overflow: hidden;
    display: -webkit-box;
    -webkit-line-clamp: 2;
    -webkit-box-orient: vertical;
  }
}

.product-bottom {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.price {
  font-size: 20px;
  font-weight: 700;
  color: var(--primary);
}

.sales {
  font-size: 12px;
  color: var(--muted);
}

@media (max-width: 1024px) {
  .entry-grid {
    grid-template-columns: repeat(2, 1fr);
  }
  
  .pet-card-grid {
    grid-template-columns: repeat(2, 1fr);
  }

  .post-grid {
    grid-template-columns: repeat(2, 1fr);
  }

  .service-grid {
    grid-template-columns: repeat(2, 1fr);
  }
  
  .product-grid {
    grid-template-columns: repeat(3, 1fr);
  }
}

@media (max-width: 768px) {
  .hero-content {
    flex-direction: column-reverse;
    min-height: auto;
  }
  
  .hero-image {
    width: 100%;
    height: 240px;
    min-height: 240px;
  }
  
  .hero-text {
    padding: 24px;
    
    h1 {
      font-size: 26px;
    }
  }
  
  .entry-grid {
    grid-template-columns: repeat(2, 1fr);
    gap: 12px;
  }
  
  .entry-card {
    padding: 20px 12px;
    gap: 12px;
  }
  
  .post-grid, .service-grid, .product-grid {
    grid-template-columns: 1fr;
  }
  
  .product-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}
</style>
