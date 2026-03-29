<template>
  <section class="home-page">
    <div class="hero card">
      <div>
        <h1>宠物综合服务平台</h1>
        <p class="muted">温暖、清新、可信赖的养宠服务体验。</p>
      </div>
      <div class="hero-badge">今日推荐 {{ tips[0]?.title || "宠物知识" }}</div>
    </div>

    <section class="card">
      <h2 class="section-title">快捷入口</h2>
      <DataState :loading="loading" :error="error" :empty="entries.length === 0" empty-text="暂无入口数据">
        <div class="entry-grid">
          <RouterLink v-for="entry in entries" :key="entry.code" :to="entry.path" class="entry-card">
            <strong>{{ entry.title }}</strong>
            <span class="muted">点击进入</span>
          </RouterLink>
        </div>
      </DataState>
    </section>

    <section class="card" v-if="banners.length">
      <h2 class="section-title">Banner</h2>
      <div class="banner-row">
        <article v-for="banner in banners" :key="banner.id" class="banner-card">
          <img :src="banner.image_url" :alt="banner.title" />
          <span>{{ banner.title }}</span>
        </article>
      </div>
    </section>
  </section>
</template>

<script setup lang="ts">
import { onMounted, ref } from "vue";
import DataState from "@/components/DataState.vue";
import { fetchHomeData } from "@/services/modules/home";
import type { HomeBanner, HomeQuickEntry, HomeTip } from "@/types/home";
import { toErrorMessage } from "@/services/http";

const loading = ref(false);
const error = ref("");
const entries = ref<HomeQuickEntry[]>([]);
const banners = ref<HomeBanner[]>([]);
const tips = ref<HomeTip[]>([]);

onMounted(async () => {
  loading.value = true;
  error.value = "";
  try {
    const data = await fetchHomeData();
    entries.value = data.quick_entries || [];
    banners.value = data.banners || [];
    tips.value = data.tips || [];
  } catch (e) {
    error.value = toErrorMessage(e);
  } finally {
    loading.value = false;
  }
});
</script>

<style scoped lang="scss">
.home-page {
  display: grid;
  gap: 16px;
}

.hero {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: linear-gradient(130deg, #ffe6d2 0%, #fff2e6 48%, #fff8f1 100%);
  border: 1px solid #ffd9bc;
}

.hero h1 {
  margin: 0;
  font-size: 32px;
  line-height: 1.25;
  color: #7f4026;
}

.hero-badge {
  background: #fff;
  border: 1px solid #ffd8bc;
  color: #9b5b3d;
  border-radius: 999px;
  padding: 8px 14px;
  font-weight: 700;
}

.entry-grid {
  display: grid;
  gap: 12px;
  grid-template-columns: repeat(auto-fit, minmax(170px, 1fr));
}

.entry-card {
  border: 1px solid #f0dccb;
  border-radius: 14px;
  padding: 14px;
  display: grid;
  gap: 8px;
  background: linear-gradient(180deg, #fffdfb 0%, #fff7f0 100%);
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}

.entry-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 10px 20px rgba(128, 84, 52, 0.14);
}

.banner-row {
  display: grid;
  gap: 12px;
  grid-template-columns: repeat(auto-fit, minmax(220px, 1fr));
}

.banner-card {
  border-radius: 14px;
  overflow: hidden;
  border: 1px solid #f0ddce;
  background: #fff;
}

.banner-card img {
  width: 100%;
  height: 120px;
  object-fit: cover;
  display: block;
}

.banner-card span {
  display: block;
  padding: 10px;
  font-weight: 700;
  color: #7a4a34;
}

@media (max-width: 768px) {
  .hero {
    flex-direction: column;
    align-items: flex-start;
    gap: 10px;
  }

  .hero h1 {
    font-size: 24px;
  }
}
</style>
