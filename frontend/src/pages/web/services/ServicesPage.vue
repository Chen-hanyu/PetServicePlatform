<template>
  <section class="services-page">
    <!-- Hero Search -->
    <div class="services-hero">
      <h1>寻找最贴心的宠物服务</h1>
      <p>汇集周边优质医院、美容、寄养与训练，给TA最好的关怀</p>
      <div class="search-bar">
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
          <circle cx="11" cy="11" r="8"/><path d="m21 21-4.35-4.35"/>
        </svg>
        <input type="text" placeholder="搜索附近的服务或商家..." v-model="searchQuery" @keydown.enter.prevent="loadMerchants" />
        <button class="search-btn" @click="loadMerchants">搜索商家</button>
      </div>
      <RouterLink to="/profile/bookings" class="booking-link">查看我的预约</RouterLink>
    </div>

    <!-- Service Categories -->
    <div class="category-grid">
      <div 
        v-for="cat in categories" 
        :key="cat.id" 
        :class="['category-card', { active: selectedCategory === cat.apiName }]"
        @click="selectCategory(cat.apiName)"
      >
        <div class="category-icon" :style="{ background: cat.bgColor }">
          <span v-html="cat.icon"></span>
        </div>
        <h3>{{ cat.name }}</h3>
        <p>{{ cat.desc }}</p>
      </div>
    </div>

    <!-- Merchant List -->
    <main class="merchant-list">
      <div class="list-header">
        <h2>
          <span class="title-indicator"></span>
          {{ selectedCategory || "全部" }}商家
        </h2>
        <div class="list-controls">
          <select v-model="sortBy" class="sort-select">
            <option value="default">综合排序</option>
            <option value="distance">距离最近</option>
            <option value="rating">评分最高</option>
          </select>
          <button class="filter-btn">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <polygon points="22 3 2 3 10 12.46 10 19 14 21 14 12.46 22 3"/>
            </svg>
            更多筛选
          </button>
        </div>
      </div>

      <DataState :loading="loading" :error="error" :empty="filteredMerchants.length === 0" empty-text="暂无商家">
        <div class="merchants-grid">
          <article
            v-for="m in filteredMerchants"
            :key="m.id"
            class="merchant-card"
            @click="goDetail(m.id)"
          >
            <div class="merchant-image">
              <img :src="getMerchantCover(m)" :alt="m.name" />
              <div class="merchant-rating">
                <svg viewBox="0 0 24 24" fill="currentColor">
                  <polygon points="12 2 15.09 8.26 22 9.27 17 14.14 18.18 21.02 12 17.77 5.82 21.02 7 14.14 2 9.27 8.91 8.26 12 2"/>
                </svg>
                {{ m.score || m.rating }}
              </div>
            </div>
            <div class="merchant-info">
              <h3>{{ m.name }}</h3>
              <p class="merchant-desc">{{ m.description || m.address }}</p>
              <div class="merchant-tags">
                <span class="tag">营业时间: {{ m.business_hours }}</span>
              </div>
              <div class="merchant-footer">
                <span class="merchant-location">
                  <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <path d="M21 10c0 7-9 13-9 13s-9-6-9-13a9 9 0 0 1 18 0z"/><circle cx="12" cy="10" r="3"/>
                  </svg>
                  {{ m.district }}
                </span>
                <button class="btn-book">立即预约</button>
              </div>
            </div>
          </article>
        </div>
      </DataState>
    </main>
  </section>
</template>

<script setup lang="ts">
import { onMounted, ref, computed, watch } from "vue";
import { useRouter } from "vue-router";
import DataState from "@/components/DataState.vue";
import { fetchMerchants } from "@/api/modules/services";
import { toErrorMessage } from "@/api/http";

const router = useRouter();

const merchantCoverById: Record<string, string> = {
  1: "/static/images/merchant-grooming.png",
  2: "/static/images/merchant-clinic.png",
  3: "/static/images/merchant-training.png",
  4: "/static/images/merchant-boarding.png",
  5: "/static/images/merchant-home-care.png"
};

const loading = ref(false);
const error = ref("");
const searchQuery = ref("");
const sortBy = ref("default");
const selectedCategory = ref("");
const categoryIcons = {
  clinic: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M9 3h6v6h6v6h-6v6H9v-6H3V9h6z"/></svg>',
  grooming: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="6" cy="6" r="3"/><circle cx="6" cy="18" r="3"/><path d="M20 4L8.12 15.88M14.47 14.48L20 20M8.12 8.12L12 12"/></svg>',
  boarding: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M3 11l9-7 9 7"/><path d="M5 10v10h14V10"/><path d="M9 20v-6h6v6"/></svg>',
  training: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="12" cy="12" r="8"/><circle cx="12" cy="12" r="3"/><path d="M12 2v3M12 19v3M2 12h3M19 12h3"/></svg>'
};
const categories = ref<Array<{ id: number; name: string; apiName: string; desc: string; icon: string; bgColor: string }>>([
  { id: 1, name: "宠物医院", apiName: "体检", desc: "医疗体检/急诊", icon: categoryIcons.clinic, bgColor: "rgba(231, 76, 60, 0.1)" },
  { id: 2, name: "洗护美容", apiName: "洗护", desc: "洗澡/剪毛/SPA", icon: categoryIcons.grooming, bgColor: "rgba(52, 152, 219, 0.1)" },
  { id: 3, name: "宠物寄养", apiName: "寄养", desc: "家庭寄养/酒店", icon: categoryIcons.boarding, bgColor: "rgba(243, 156, 18, 0.1)" },
  { id: 4, name: "宠物训练", apiName: "训练", desc: "行为纠正/技能", icon: categoryIcons.training, bgColor: "rgba(46, 204, 113, 0.1)" }
]);

const merchants = ref<
  Array<{
    id: string | number;
    name: string;
    district: string;
    address?: string;
    description?: string;
    cover_url?: string;
    score?: number;
    rating: number;
    business_hours?: string;
    status: string;
    category?: string;
    tags?: string[];
  }>
>([]);

const getMerchantCover = (merchant: { id: string | number; cover_url?: string }) => {
  return merchant.cover_url || merchantCoverById[merchant.id] || "/static/images/merchant-grooming.png";
};

const filteredMerchants = computed(() => {
  let result = merchants.value;
  if (searchQuery.value.trim()) {
    const q = searchQuery.value.trim().toLowerCase();
    result = result.filter(m =>
      m.name.toLowerCase().includes(q) ||
      (m.description && m.description.toLowerCase().includes(q)) ||
      (m.tags && m.tags.some(t => t.toLowerCase().includes(q)))
    );
  }
  return result;
});

const loadMerchants = async () => {
  loading.value = true;
  error.value = "";
  try {
    const data = await fetchMerchants({ category: selectedCategory.value || undefined, sort: sortBy.value === "rating" ? "score_desc" : undefined, page: 1, page_size: 20 });
    merchants.value = (data.list || []) as typeof merchants.value;
  } catch (e) {
    error.value = toErrorMessage(e);
    merchants.value = [];
  } finally {
    loading.value = false;
  }
};

const selectCategory = (name: string) => {
  selectedCategory.value = selectedCategory.value === name ? "" : name;
};

const goDetail = (id: string | number) => {
  router.push(`/services/merchant/${id}`);
};

onMounted(async () => {
  await loadMerchants();
});

watch([selectedCategory, sortBy], () => {
  void loadMerchants();
});
</script>

<style scoped lang="scss">
.services-page {
  display: flex;
  flex-direction: column;
  gap: 32px;
  max-width: 1200px;
  margin: 0 auto;
  padding-bottom: 40px;
}

// Hero Search
.services-hero {
  text-align: center;
  padding: 48px 24px;
  background: var(--surface);
  border-radius: 20px;
  box-shadow: 0 8px 24px rgba(34, 60, 52, 0.08);
  
  h1 {
    font-size: 32px;
    font-weight: 800;
    color: var(--text-heading);
    margin: 0 0 8px;
  }
  
  p {
    font-size: 16px;
    color: var(--muted);
    margin: 0 0 32px;
  }
}

.search-bar {
  max-width: 700px;
  margin: 0 auto;
  display: flex;
  align-items: center;
  background: var(--surface-muted);
  border: 2px solid var(--border-warm);
  border-radius: 16px;
  padding: 6px;
  transition: all 0.2s ease;
  
  &:focus-within {
    border-color: var(--primary);
    box-shadow: 0 0 0 4px rgba(255, 155, 122, 0.2);
  }
  
  svg {
    width: 24px;
    height: 24px;
    color: var(--muted);
    margin-left: 12px;
  }
  
  input {
    flex: 1;
    border: none;
    background: transparent;
    padding: 12px 16px;
    font-size: 16px;
    color: var(--text);
    outline: none;
    
    &::placeholder {
      color: var(--muted);
    }
  }
}

.search-btn {
  padding: 12px 24px;
  background: var(--primary);
  color: #fff;
  border: none;
  border-radius: 12px;
  font-size: 15px;
  font-weight: 700;
  cursor: pointer;
  transition: all 0.2s ease;
  
  &:hover {
    opacity: 0.9;
    transform: translateY(-1px);
  }
}

.booking-link {
  display: inline-flex;
  margin-top: 18px;
  color: var(--primary);
  font-size: 14px;
  font-weight: 700;
  text-decoration: none;

  &:hover {
    color: var(--primary-strong);
  }
}

// Categories
.category-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
}

.category-card {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 24px 16px;
  background: var(--surface);
  border-radius: 16px;
  box-shadow: 0 4px 16px rgba(34, 60, 52, 0.06);
  cursor: pointer;
  transition: all 0.3s ease;
  
  &:hover {
    transform: translateY(-4px);
    box-shadow: 0 12px 30px rgba(34, 60, 52, 0.12);
  }
  
  &.active {
    background: var(--primary);
    
    h3, p {
      color: #fff;
    }
    
    .category-icon {
      background: rgba(255, 255, 255, 0.2);
    }
  }
}

.category-icon {
  width: 72px;
  height: 72px;
  border-radius: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 16px;
  font-size: 32px;
  transition: all 0.3s ease;

  :deep(svg) {
    width: 34px;
    height: 34px;
    color: var(--text-heading);
  }
}

.category-card h3 {
  font-size: 16px;
  font-weight: 700;
  color: var(--text-heading);
  margin: 0 0 4px;
}

.category-card p {
  font-size: 12px;
  color: var(--muted);
  margin: 0;
}

// Merchant List
.merchant-list {
  width: 100%;
}

.list-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  
  h2 {
    display: flex;
    align-items: center;
    gap: 12px;
    margin: 0;
    font-size: 22px;
    font-weight: 700;
    color: var(--text-heading);
  }
}

.title-indicator {
  width: 6px;
  height: 24px;
  background: var(--primary);
  border-radius: 3px;
}

.list-controls {
  display: flex;
  gap: 12px;
}

.sort-select {
  padding: 10px 16px;
  border: 1px solid var(--border-warm);
  border-radius: 10px;
  background: var(--surface);
  color: var(--muted);
  font-size: 14px;
  cursor: pointer;
  
  &:focus {
    outline: none;
    border-color: var(--primary);
  }
}

.filter-btn {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 16px;
  background: var(--surface);
  border: 1px solid var(--border-warm);
  border-radius: 10px;
  color: var(--muted);
  font-size: 14px;
  cursor: pointer;
  transition: all 0.2s ease;
  
  svg {
    width: 16px;
    height: 16px;
  }
  
  &:hover {
    background: var(--chip-bg);
    color: var(--text-heading);
  }
}

.merchants-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 24px;
}

.merchant-card {
  display: flex;
  background: var(--surface);
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 4px 16px rgba(34, 60, 52, 0.06);
  cursor: pointer;
  transition: all 0.3s ease;
  
  &:hover {
    transform: translateY(-4px);
    box-shadow: 0 12px 30px rgba(34, 60, 52, 0.12);
  }
}

.merchant-image {
  width: 200px;
  height: 180px;
  position: relative;
  flex-shrink: 0;
  
  img {
    width: 100%;
    height: 100%;
    object-fit: cover;
  }
}

.merchant-rating {
  position: absolute;
  top: 12px;
  right: 12px;
  display: flex;
  align-items: center;
  gap: 4px;
  padding: 6px 12px;
  background: rgba(255, 255, 255, 0.95);
  border-radius: 10px;
  font-size: 14px;
  font-weight: 700;
  color: var(--rating);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  
  svg {
    width: 14px;
    height: 14px;
  }
}

.merchant-info {
  flex: 1;
  padding: 20px;
  display: flex;
  flex-direction: column;
  
  h3 {
    font-size: 18px;
    font-weight: 700;
    color: var(--text-heading);
    margin: 0 0 8px;
  }
}

.merchant-desc {
  font-size: 14px;
  color: var(--muted);
  margin: 0 0 12px;
  line-height: 1.5;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.merchant-tags {
  display: flex;
  gap: 8px;
  margin-bottom: 16px;
  
  .tag {
    padding: 4px 10px;
    background: var(--chip-bg);
    color: var(--muted);
    border-radius: 8px;
    font-size: 12px;
  }
  
  .tag-primary {
    background: rgba(255, 155, 122, 0.15);
    color: var(--primary);
  }
  
  .tag-success {
    background: rgba(91, 185, 140, 0.15);
    color: var(--success);
  }
}

.merchant-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: auto;
  padding-top: 16px;
  border-top: 1px dashed var(--border-warm);
}

.merchant-location {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 13px;
  color: var(--muted);
  
  svg {
    width: 16px;
    height: 16px;
  }
}

.btn-book {
  padding: 10px 20px;
  background: var(--primary);
  color: #fff;
  border: none;
  border-radius: 10px;
  font-size: 14px;
  font-weight: 700;
  cursor: pointer;
  transition: all 0.2s ease;
  
  &:hover {
    opacity: 0.9;
    transform: translateY(-1px);
  }
}

@media (max-width: 1024px) {
  .category-grid {
    grid-template-columns: repeat(2, 1fr);
  }
  
  .merchants-grid {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 768px) {
  .services-hero {
    padding: 32px 16px;
    
    h1 {
      font-size: 24px;
    }
  }
  
  .category-grid {
    grid-template-columns: repeat(2, 1fr);
    gap: 12px;
  }
  
  .merchant-card {
    flex-direction: column;
  }
  
  .merchant-image {
    width: 100%;
    height: 160px;
  }
}
</style>
