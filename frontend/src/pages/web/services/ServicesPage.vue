<template>
  <section class="services-page">
    <p class="promo-strip">
      <span class="strip-ico" aria-hidden="true">💈</span>
      点击商家进入详情，再点右下角「点击预约」进入预约页；右侧「预约单」可查看当前草稿（与商城多页体验一致）
    </p>

    <div class="card page-hero">
      <h1>宠物服务</h1>
      <p>专业、可靠的宠物服务，守护爱宠健康成长</p>
    </div>

    <div class="category-nav">
      <button
        v-for="cat in categories"
        :key="cat.id"
        type="button"
        :class="['cat-btn', { active: selectedCategory === cat.name }]"
        @click="selectCategory(cat.name)"
      >
        <span class="cat-icon">🏷️</span>
        {{ cat.name }}
      </button>
    </div>

    <main class="merchant-list">
      <div class="list-header">
        <h2>{{ selectedCategory || "全部" }}商家</h2>
        <span class="count">共 {{ filteredMerchants.length }} 家</span>
      </div>

      <DataState :loading="loading" :error="error" :empty="filteredMerchants.length === 0" empty-text="暂无商家">
        <div class="merchants-grid">
          <article
            v-for="m in filteredMerchants"
            :key="m.id"
            class="merchant-card"
            role="link"
            tabindex="0"
            @click="goDetail(m.id)"
            @keydown.enter.prevent="goDetail(m.id)"
          >
            <div class="merchant-image">
              <img :src="m.cover_url" :alt="m.name" />
              <div class="rating-badge">⭐ {{ m.rating }}</div>
            </div>
            <div class="merchant-info">
              <h3>{{ m.name }}</h3>
              <p class="desc">{{ m.description }}</p>
              <div class="meta-row">
                <span class="location">📍 {{ m.district }}</span>
                <span class="status" :class="m.status === '营业中' ? 'open' : 'closed'">
                  {{ m.status }}
                </span>
              </div>
              <span class="enter-hint">进入详情 →</span>
            </div>
          </article>
        </div>
      </DataState>
    </main>

    <ServiceBookingDock />
  </section>
</template>

<script setup lang="ts">
import { onMounted, ref, computed } from "vue";
import { useRouter } from "vue-router";
import DataState from "@/components/DataState.vue";
import ServiceBookingDock from "@/components/services/ServiceBookingDock.vue";
import { fetchMerchants } from "@/services/modules/services";
import { mockMerchants } from "@/mocks/services";

const router = useRouter();

const loading = ref(false);
const error = ref("");
const categories = ref<Array<{ id: number; name: string }>>([]);
const merchants = ref<
  Array<{
    id: number;
    name: string;
    district: string;
    description?: string;
    cover_url: string;
    rating: number;
    status: string;
    category?: string;
  }>
>([]);
const selectedCategory = ref("");

const filteredMerchants = computed(() => {
  if (!selectedCategory.value) return merchants.value;
  return merchants.value.filter((m) => m.category === selectedCategory.value);
});

function enrichFromMock(
  list: Array<{
    id: number;
    name: string;
    district?: string;
    description?: string;
    cover_url?: string;
    rating?: number;
    status: string;
    category?: string;
  }>
) {
  return list.map((row) => {
    const mock = mockMerchants.find((x) => x.id === row.id);
    return {
      ...row,
      district: row.district || mock?.district || "",
      description: row.description ?? mock?.description,
      cover_url: row.cover_url || mock?.cover_url || "",
      rating: row.rating ?? mock?.rating ?? 0,
      category: row.category || mock?.category
    };
  });
}

const loadMerchants = async () => {
  loading.value = true;
  error.value = "";
  try {
    const data = await fetchMerchants({ page: 1, page_size: 20 });
    merchants.value = enrichFromMock((data.list || []) as typeof merchants.value);
  } catch (e) {
    console.warn("Failed to fetch merchants, using mock data", e);
    merchants.value = mockMerchants.map((m) => ({
      id: m.id,
      name: m.name,
      district: m.district,
      description: m.description,
      cover_url: m.cover_url,
      rating: m.rating,
      status: m.status,
      category: m.category
    }));
  } finally {
    loading.value = false;
  }
};

const selectCategory = (name: string) => {
  selectedCategory.value = selectedCategory.value === name ? "" : name;
};

const goDetail = (id: number) => {
  router.push(`/services/merchant/${id}`);
};

onMounted(async () => {
  categories.value = [
    { id: 1, name: "宠物美容" },
    { id: 2, name: "宠物医院" },
    { id: 3, name: "宠物训练" },
    { id: 4, name: "宠物寄养" }
  ];
  await loadMerchants();
});
</script>

<style scoped lang="scss">
.services-page {
  display: flex;
  flex-direction: column;
  gap: 24px;
  max-width: 1100px;
  margin: 0 auto;
  padding-bottom: 32px;
}

.promo-strip {
  margin: 0;
  padding: 10px 14px;
  border-radius: 12px;
  background: linear-gradient(90deg, #fff4e8 0%, #fff9f2 100%);
  border: 1px solid var(--border-warm);
  font-size: 14px;
  font-weight: 600;
  color: var(--text-heading-soft);
}

.strip-ico {
  margin-right: 8px;
}

.category-nav {
  display: flex;
  gap: 12px;
  overflow-x: auto;
  padding-bottom: 8px;
}

.cat-btn {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 20px;
  background: var(--surface-tint);
  border: 1px solid var(--border-warm-mid);
  border-radius: 30px;
  cursor: pointer;
  white-space: nowrap;
  transition: all 0.2s;
  color: var(--muted);
  font-weight: 600;

  &:hover {
    background: var(--chip-active-bg);
    border-color: var(--chip-border);
  }

  &.active {
    background: linear-gradient(135deg, var(--primary) 0%, var(--primary-strong) 100%);
    color: #fff;
    border-color: var(--primary-strong);
    box-shadow: 0 6px 14px rgba(241, 124, 83, 0.22);
  }
}

.merchant-list {
  width: 100%;
}

.list-header {
  display: flex;
  align-items: baseline;
  justify-content: space-between;
  margin-bottom: 16px;

  h2 {
    margin: 0;
    font-size: 22px;
    font-weight: 900;
    color: var(--text);
  }

  .count {
    font-size: 14px;
    color: var(--muted);
    font-weight: 600;
  }
}

.merchants-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 20px;
}

.merchant-card {
  background: #fff;
  border-radius: 16px;
  overflow: hidden;
  border: 1px solid #f0dccb;
  cursor: pointer;
  transition: all 0.3s ease;

  &:hover {
    transform: translateY(-4px);
    box-shadow: 0 12px 24px rgba(128, 84, 52, 0.12);
  }

  &:focus-visible {
    outline: 3px solid var(--primary);
    outline-offset: 2px;
  }
}

.merchant-image {
  height: 180px;
  position: relative;

  img {
    width: 100%;
    height: 100%;
    object-fit: cover;
  }
}

.rating-badge {
  position: absolute;
  top: 12px;
  right: 12px;
  background: rgba(255, 255, 255, 0.95);
  padding: 4px 10px;
  border-radius: 12px;
  font-size: 13px;
  font-weight: 700;
  color: #f3b64f;
}

.merchant-info {
  padding: 16px;

  h3 {
    margin: 0 0 8px;
    font-size: 18px;
    color: #2f2a26;
  }

  .desc {
    margin: 0 0 12px;
    font-size: 13px;
    color: #7d7068;
    line-height: 1.4;
    display: -webkit-box;
    -webkit-line-clamp: 2;
    -webkit-box-orient: vertical;
    overflow: hidden;
  }
}

.meta-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 13px;

  .location {
    color: var(--on-white-text);
  }

  .status {
    padding: 2px 8px;
    border-radius: 8px;
    font-size: 12px;
    font-weight: 600;

    &.open {
      background: #e8f5e9;
      color: #4caf50;
    }

    &.closed {
      background: #ffebee;
      color: #f44336;
    }
  }
}

.enter-hint {
  display: block;
  margin-top: 12px;
  font-size: 13px;
  font-weight: 800;
  color: var(--primary-strong);
}
</style>
