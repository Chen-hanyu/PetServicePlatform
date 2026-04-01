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
        <article v-for="post in recommendedPosts" :key="post.id" class="post-card">
          <div class="post-image">
            <img :src="post.cover" :alt="post.title" />
          </div>
          <div class="post-content">
            <h3>{{ post.title }}</h3>
            <div class="post-meta">
              <div class="author">
                <img :src="post.author.avatar" alt="Avatar" />
                <span>{{ post.author.name }}</span>
              </div>
              <div class="stats">
                <span>❤️ {{ post.likes }}</span>
                <span>💬 {{ post.comments }}</span>
              </div>
            </div>
          </div>
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
        <article v-for="service in recommendedServices" :key="service.id" class="service-card">
          <div class="service-image">
            <img :src="service.image" :alt="service.name" />
            <div class="service-rating">
              <span class="star">⭐</span> {{ service.rating }}
            </div>
          </div>
          <div class="service-info">
            <h3>{{ service.name }}</h3>
            <p class="service-location">📍 {{ service.location }}</p>
            <div class="service-tags">
              <span v-for="tag in service.tags" :key="tag" class="tag">{{ tag }}</span>
            </div>
          </div>
        </article>
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
        <article v-for="product in recommendedProducts" :key="product.id" class="product-card">
          <div class="product-image">
            <img :src="product.image" :alt="product.name" />
            <div v-if="product.badge" class="product-badge">{{ product.badge }}</div>
          </div>
          <div class="product-info">
            <h3>{{ product.name }}</h3>
            <div class="product-bottom">
              <span class="price">¥{{ product.price }}</span>
              <span class="sales">销量 {{ product.sales }}+</span>
            </div>
          </div>
        </article>
      </div>
    </section>
  </section>
</template>

<script setup lang="ts">
import { onMounted, ref } from "vue";
import DataState from "@/components/DataState.vue";
import { fetchHomeData } from "@/services/modules/home";
import { mockHomeData } from "@/mocks/home";
import type { HomeBanner, HomeQuickEntry, HomeTip } from "@/types/home";
import { toErrorMessage } from "@/services/http";

const loading = ref(false);
const error = ref("");
const entries = ref<HomeQuickEntry[]>([]);
const tips = ref<HomeTip[]>([]);
const recommendedPosts = ref<any[]>([]);
const recommendedServices = ref<any[]>([]);
const recommendedProducts = ref<any[]>([]);

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
  } catch (e) {
    console.warn("Failed to fetch home data, using mock data", e);
    entries.value = mockHomeData.quick_entries;
    tips.value = mockHomeData.tips;
    recommendedPosts.value = mockHomeData.recommended_posts;
    recommendedServices.value = mockHomeData.recommended_services;
    recommendedProducts.value = mockHomeData.recommended_products;
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
  justify-content: space-between;
  align-items: center;
  font-size: 13px;
  color: var(--muted);
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
