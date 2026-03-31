<template>
  <section class="home-page">
    <!-- Hero Section -->
    <section class="hero-section">
      <div class="hero-content card">
        <div class="hero-text">
          <h1>发现爱与陪伴</h1>
          <p>全球最温暖的宠物服务平台</p>
          <div class="hero-actions">
            <RouterLink to="/adoption" class="btn btn-primary">领养萌宠</RouterLink>
            <RouterLink to="/community" class="btn btn-secondary">进入社区</RouterLink>
          </div>
        </div>
        <div class="hero-image">
          <img src="https://images.unsplash.com/photo-1450778869180-41d0601e046e?auto=format&fit=crop&w=600&q=80" alt="Pet" />
        </div>
      </div>
      <div class="hero-badge">
        <span class="badge-icon">✨</span>
        <span>{{ tips[0]?.title || "今日推荐" }}</span>
        <div class="badge-content">{{ tips[0]?.content || "春天到了，记得给宠物做好体内外驱虫。" }}</div>
      </div>
    </section>

    <!-- Quick Entries -->
    <section class="section">
      <h2 class="section-title">快捷入口</h2>
      <DataState :loading="loading" :error="error" :empty="entries.length === 0" empty-text="暂无入口数据">
        <div class="entry-grid">
          <RouterLink v-for="entry in entries" :key="entry.code" :to="entry.path" class="entry-card">
            <div class="entry-icon">
              <img v-if="entry.code === 'community'" src="https://img.icons8.com/ios-filled/50/ff9d7a/chat.png" alt="Community" />
              <img v-else-if="entry.code === 'adoption'" src="https://img.icons8.com/ios-filled/50/ff9d7a/dog.png" alt="Adoption" />
              <img v-else-if="entry.code === 'services'" src="https://img.icons8.com/ios-filled/50/ff9d7a/wrench.png" alt="Services" />
              <img v-else-if="entry.code === 'shop'" src="https://img.icons8.com/ios-filled/50/ff9d7a/shopping-bag.png" alt="Shop" />
            </div>
            <strong>{{ entry.title }}</strong>
          </RouterLink>
        </div>
      </DataState>
    </section>

    <!-- Banners -->
    <section class="section" v-if="banners.length">
      <div class="banner-carousel">
        <article v-for="banner in banners" :key="banner.id" class="banner-card">
          <img :src="banner.image_url" :alt="banner.title" />
          <div class="banner-overlay">
            <h3>{{ banner.title }}</h3>
          </div>
        </article>
      </div>
    </section>

    <!-- Recommended Posts -->
    <section class="section">
      <div class="section-header">
        <h2 class="section-title">热门社区</h2>
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
        <h2 class="section-title">推荐服务</h2>
        <RouterLink to="/services" class="view-all">查看全部 →</RouterLink>
      </div>
      <div class="service-grid">
        <article v-for="service in recommendedServices" :key="service.id" class="service-card">
          <div class="service-image">
            <img :src="service.image" :alt="service.name" />
            <div class="service-rating">⭐ {{ service.rating }}</div>
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
        <h2 class="section-title">热门商品</h2>
        <RouterLink to="/shop" class="view-all">查看全部 →</RouterLink>
      </div>
      <div class="product-grid">
        <article v-for="product in recommendedProducts" :key="product.id" class="product-card">
          <div class="product-image">
            <img :src="product.image" :alt="product.name" />
            <div class="product-badge">热卖</div>
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
import { fetchHomeData } from "@/api/modules/home";
import { mockHomeData } from "@/mocks/home";
import type { HomeBanner, HomeQuickEntry, HomeTip } from "@/types/home";
import { toErrorMessage } from "@/api/http";

const loading = ref(false);
const error = ref("");
const entries = ref<HomeQuickEntry[]>([]);
const banners = ref<HomeBanner[]>([]);
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
    // Fallback to mock data
    entries.value = mockHomeData.quick_entries;
    banners.value = mockHomeData.banners;
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
  gap: 24px;
}

.hero-section {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.hero-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0;
  overflow: hidden;
  min-height: 320px;
}

.hero-text {
  padding: 40px;
  flex: 1;
}

.hero-text h1 {
  font-size: 42px;
  font-weight: 800;
  color: var(--text-heading);
  margin: 0 0 12px;
  line-height: 1.2;
}

.hero-text p {
  font-size: 18px;
  color: var(--text-subheading);
  margin: 0 0 24px;
}

.hero-actions {
  display: flex;
  gap: 16px;
}

.hero-image {
  width: 45%;
  height: 100%;
  min-height: 320px;
  position: relative;
}

.hero-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.hero-badge {
  background: var(--banner-soft);
  border: 1px solid var(--chip-border);
  border-radius: var(--radius-lg);
  padding: 16px 24px;
  display: flex;
  align-items: center;
  gap: 12px;
  color: var(--text-heading-soft);
}

.badge-icon {
  font-size: 20px;
}

.badge-content {
  font-size: 14px;
  color: var(--text-subheading);
  margin-left: auto;
  max-width: 60%;
  text-align: right;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.view-all {
  font-size: 14px;
  color: var(--primary);
  font-weight: 600;
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
  gap: 12px;
  padding: 24px 16px;
  border-radius: 16px;
  background: linear-gradient(180deg, #fffefb 0%, var(--surface-tint) 100%);
  border: 1px solid var(--border-warm-mid);
  transition: all 0.3s ease;
}

.entry-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 12px 24px rgba(128, 84, 52, 0.15);
  border-color: var(--chip-border);
}

.entry-icon img {
  width: 48px;
  height: 48px;
}

.banner-carousel {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 16px;
}

.banner-card {
  position: relative;
  border-radius: 16px;
  overflow: hidden;
  aspect-ratio: 16/9;
  cursor: pointer;
}

.banner-card img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.5s ease;
}

.banner-card:hover img {
  transform: scale(1.05);
}

.banner-overlay {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  background: var(--image-overlay-gradient);
  padding: 20px;
  color: #fff;
}

.banner-overlay h3 {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
}

.post-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20px;
}

.post-card {
  border-radius: 16px;
  overflow: hidden;
  border: 1px solid #f0dccb;
  background: #fff;
  transition: all 0.3s ease;
}

.post-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 12px 24px rgba(128, 84, 52, 0.1);
}

.post-image {
  height: 160px;
  overflow: hidden;
}

.post-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.post-content {
  padding: 16px;
}

.post-content h3 {
  margin: 0 0 12px;
  font-size: 16px;
  line-height: 1.4;
  color: #2f2a26;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.post-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 12px;
  color: #7d7068;
}

.author {
  display: flex;
  align-items: center;
  gap: 8px;
}

.author img {
  width: 24px;
  height: 24px;
  border-radius: 50%;
}

.stats {
  display: flex;
  gap: 12px;
}

.service-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 20px;
}

.service-card {
  display: flex;
  gap: 16px;
  padding: 16px;
  border-radius: 16px;
  border: 1px solid #f0dccb;
  background: #fff;
  transition: all 0.3s ease;
}

.service-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 16px rgba(128, 84, 52, 0.08);
}

.service-image {
  width: 120px;
  height: 120px;
  border-radius: 12px;
  overflow: hidden;
  position: relative;
  flex-shrink: 0;
}

.service-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.service-rating {
  position: absolute;
  top: 8px;
  right: 8px;
  background: rgba(255, 255, 255, 0.9);
  padding: 4px 8px;
  border-radius: 8px;
  font-size: 12px;
  font-weight: 600;
  color: #f3b64f;
}

.service-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.service-info h3 {
  margin: 0 0 8px;
  font-size: 18px;
  color: #2f2a26;
}

.service-location {
  margin: 0 0 12px;
  font-size: 14px;
  color: #7d7068;
}

.service-tags {
  display: flex;
  gap: 8px;
}

.tag {
  padding: 4px 10px;
  background: #fff1e5;
  color: #8a4f33;
  border-radius: 20px;
  font-size: 12px;
}

.product-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20px;
}

.product-card {
  border-radius: 16px;
  overflow: hidden;
  border: 1px solid #f0dccb;
  background: #fff;
  transition: all 0.3s ease;
}

.product-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 12px 24px rgba(128, 84, 52, 0.1);
}

.product-image {
  height: 200px;
  position: relative;
  overflow: hidden;
}

.product-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.product-badge {
  position: absolute;
  top: 12px;
  left: 12px;
  background: #ff9d7a;
  color: white;
  padding: 4px 12px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 600;
}

.product-info {
  padding: 16px;
}

.product-info h3 {
  margin: 0 0 12px;
  font-size: 16px;
  color: #2f2a26;
  line-height: 1.4;
  height: 44px;
  overflow: hidden;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.product-bottom {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.price {
  font-size: 20px;
  font-weight: 700;
  color: #ff9d7a;
}

.sales {
  font-size: 12px;
  color: var(--on-white-text);
}

@media (max-width: 1024px) {
  .entry-grid {
    grid-template-columns: repeat(2, 1fr);
  }
  
  .banner-carousel {
    grid-template-columns: repeat(2, 1fr);
  }
  
  .post-grid {
    grid-template-columns: repeat(2, 1fr);
  }
  
  .product-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 768px) {
  .hero-content {
    flex-direction: column-reverse;
    min-height: auto;
  }
  
  .hero-image {
    width: 100%;
    height: 200px;
    min-height: 200px;
  }
  
  .hero-text {
    padding: 24px;
  }
  
  .hero-text h1 {
    font-size: 28px;
  }
  
  .entry-grid {
    grid-template-columns: repeat(2, 1fr);
  }
  
  .banner-carousel {
    grid-template-columns: 1fr;
  }
  
  .post-grid, .service-grid, .product-grid {
    grid-template-columns: 1fr;
  }
  
  .badge-content {
    display: none;
  }
}
</style>
