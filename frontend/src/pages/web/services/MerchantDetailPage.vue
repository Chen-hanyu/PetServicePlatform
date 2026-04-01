<template>
  <div class="merchant-detail">
    <nav class="breadcrumb card" aria-label="面包屑导航">
      <RouterLink to="/home">首页</RouterLink>
      <span class="sep">/</span>
      <RouterLink to="/services">宠物服务</RouterLink>
      <span class="sep">/</span>
      <span class="current">{{ merchant?.name || "商家详情" }}</span>
    </nav>

    <DataState :loading="loading" :error="error" :empty="!merchant && !loading" empty-text="未找到该商家">
      <template v-if="merchant">
        <div class="detail-grid card">
            <div class="gallery">
            <div class="gallery-main">
              <img :src="merchant.cover_url" :alt="merchant.name" />
            </div>
          </div>

          <div class="info">
            <p class="tag-row">
              <span class="brand-tag">宠物之家</span>
              <span class="cat-pill">{{ merchant.district }}</span>
              <span class="cat-pill open">{{ merchant.status }}</span>
            </p>
            <h1 class="title">{{ merchant.name }}</h1>
            <p class="subtitle">{{ merchant.description }}</p>

            <div class="meta-box">
              <p><span class="ico">📍</span>{{ merchant.address }}</p>
              <p><span class="ico">📞</span>{{ merchant.phone }}</p>
              <p><span class="ico">🕘</span>{{ merchant.business_hours }}</p>
              <p class="rating-line"><span class="ico">⭐</span>评分 {{ merchant.rating }}</p>
            </div>

            <p class="hint">
              可在预约页<strong>多选主服务</strong>，并查看店铺<strong>优惠套餐</strong>；选定后补充日期、时段与可选加项即可。
            </p>
            <div class="info-actions">
              <button type="button" class="btn-book" @click="goBooking">点击预约</button>
            </div>
          </div>
        </div>
      </template>
    </DataState>

    <ServiceBookingDock />
  </div>
</template>

<script setup lang="ts">
import { ref, watch } from "vue";
import { useRoute, useRouter } from "vue-router";
import DataState from "@/components/DataState.vue";
import ServiceBookingDock from "@/components/services/ServiceBookingDock.vue";
import { fetchMerchantDetail } from "@/api/modules/services";
import { getMockMerchantById } from "@/mocks/services";

type MerchantVm = {
  id: number;
  name: string;
  district: string;
  address: string;
  phone: string;
  business_hours: string;
  status: string;
  rating: number;
  cover_url: string;
  description: string;
  services: Array<{ id: number; name: string; price: number; duration: string }>;
};

const route = useRoute();
const router = useRouter();
const loading = ref(true);
const error = ref("");
const merchant = ref<MerchantVm | null>(null);

const formatPrice = (n: number) => (Number.isInteger(n) ? String(n) : n.toFixed(2));

function mergeMerchant(api: Record<string, unknown>, mock: MerchantVm | null): MerchantVm | null {
  if (!api && !mock) return null;
  const m = mock || ({} as MerchantVm);
  const services = (api?.services as MerchantVm["services"]) || m.services || [];
  const a = api as {
    cover_url?: string;
    description?: string;
    rating?: number;
  };
  return {
    id: Number(api?.id ?? m.id),
    name: String(api?.name ?? m.name),
    district: String(api?.district ?? m.district ?? ""),
    address: String(api?.address ?? m.address ?? ""),
    phone: String(api?.phone ?? m.phone ?? ""),
    business_hours: String(api?.business_hours ?? m.business_hours ?? ""),
    status: String(api?.status ?? m.status ?? "营业中"),
    rating: Number(a.rating ?? m.rating ?? 0),
    cover_url: String(a.cover_url ?? m.cover_url ?? ""),
    description: String(a.description ?? m.description ?? ""),
    services: services.map((s: { id: number; name: string; price: number; duration?: string }) => ({
      id: s.id,
      name: s.name,
      price: s.price,
      duration: s.duration || m.services?.find((x) => x.id === s.id)?.duration || "—"
    }))
  };
}

async function load() {
  loading.value = true;
  error.value = "";
  merchant.value = null;

  const id = Number(route.params.id);
  if (!Number.isFinite(id) || id < 1) {
    loading.value = false;
    router.replace("/services");
    return;
  }

  const mock = getMockMerchantById(id);
  try {
    const data = await fetchMerchantDetail(id);
    merchant.value = mergeMerchant(data as unknown as Record<string, unknown>, mock as MerchantVm | null);
  } catch {
    merchant.value = mock as MerchantVm | null;
  } finally {
    loading.value = false;
  }

  if (!merchant.value) {
    error.value = "";
  }
}

/** 详情页只负责进入预约页；具体服务与套餐在预约页多选 */
function goBooking() {
  if (!merchant.value) return;
  router.push({ path: `/services/book/${merchant.value.id}` });
}

watch(
  () => route.params.id,
  () => load(),
  { immediate: true }
);
</script>

<style scoped lang="scss">
.merchant-detail {
  max-width: 1100px;
  margin: 0 auto;
  padding-bottom: 32px;
}

.breadcrumb {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 8px;
  padding: 12px 16px;
  margin-bottom: 14px;
  font-size: 15px;
  color: var(--muted);

  a {
    color: var(--primary-strong);
    font-weight: 600;
    text-decoration: none;

    &:hover {
      text-decoration: underline;
    }
  }

  .sep {
    opacity: 0.45;
  }

  .current {
    color: var(--text);
    font-weight: 600;
    max-width: 100%;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }
}

.detail-grid {
  display: grid;
  grid-template-columns: minmax(0, 1fr) minmax(280px, 400px);
  gap: 24px;
  padding: 20px;
  margin-bottom: 16px;
}

.gallery-main {
  border-radius: var(--radius-md);
  overflow: hidden;
  background: var(--surface-muted);
  aspect-ratio: 1;
  max-height: 420px;

  img {
    width: 100%;
    height: 100%;
    object-fit: cover;
    display: block;
  }
}

.info {
  display: flex;
  flex-direction: column;
  min-height: 0;

  .tag-row {
    display: flex;
    flex-wrap: wrap;
    gap: 8px;
    margin: 0 0 10px;
  }

  .brand-tag {
    display: inline-block;
    padding: 4px 10px;
    border-radius: 8px;
    background: var(--chip-bg);
    font-size: 13px;
    font-weight: 800;
    color: var(--primary-strong);
  }

  .cat-pill {
    display: inline-block;
    padding: 4px 10px;
    border-radius: 8px;
    background: var(--surface-muted);
    font-size: 13px;
    font-weight: 700;
    color: var(--muted);

    &.open {
      background: #e8f5e9;
      color: #2e7d32;
    }
  }

  .title {
    margin: 0 0 8px;
    font-size: 26px;
    font-weight: 900;
    color: var(--text);
    line-height: 1.25;
  }

  .subtitle {
    margin: 0 0 16px;
    font-size: 15px;
    line-height: 1.55;
    color: var(--muted);
  }

  .meta-box {
    padding: 14px 16px;
    border-radius: var(--radius-md);
    background: var(--surface-tint);
    border: 1px solid var(--border-warm);
    font-size: 14px;
    color: var(--text);
    margin-bottom: 14px;

    p {
      margin: 0 0 8px;
      display: flex;
      align-items: flex-start;
      gap: 8px;

      &:last-child {
        margin-bottom: 0;
      }
    }

    .ico {
      flex-shrink: 0;
    }
  }

  .rating-line {
    font-weight: 700;
    color: #e6a23c;
  }

  .hint {
    margin: 0 0 16px;
    font-size: 13px;
    color: var(--muted);
    line-height: 1.5;
  }
}

.info-actions {
  display: flex;
  justify-content: flex-end;
  margin-top: auto;
}

.btn-book {
  min-width: 236px;
  min-height: 90px;
  padding: 18px 44px;
  border-radius: 22px;
  border: none;
  background: linear-gradient(135deg, #ff6b4a 0%, var(--primary-strong) 100%);
  font-size: 20px;
  font-weight: 900;
  color: #fff;
  cursor: pointer;
  white-space: nowrap;
  line-height: 1;

  &:hover {
    filter: brightness(1.05);
  }
}

@media (max-width: 900px) {
  .detail-grid {
    grid-template-columns: 1fr;
  }
}
</style>
