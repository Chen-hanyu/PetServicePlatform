<template>
  <div class="detail-page">
    <nav class="breadcrumb card" aria-label="面包屑导航">
      <RouterLink to="/home">首页</RouterLink>
      <span class="sep">/</span>
      <RouterLink to="/shop">宠物商城</RouterLink>
      <span class="sep">/</span>
      <span class="current">{{ product?.name || "商品详情" }}</span>
    </nav>

    <DataState :loading="loading" :error="error" :empty="!product && !loading" empty-text="商品不存在或已下架">
      <template v-if="product">
        <div class="detail-grid card">
          <div class="gallery">
            <div ref="galleryMainRef" class="gallery-main">
              <img :src="galleryImages[activeImg]" :alt="product.name" />
            </div>
            <div v-if="galleryImages.length > 1" class="gallery-thumbs">
              <button
                v-for="(src, i) in galleryImages"
                :key="i"
                type="button"
                :class="['thumb', { on: i === activeImg }]"
                @click="activeImg = i"
              >
                <img :src="src" alt="" />
              </button>
            </div>
          </div>

          <div class="info">
            <p class="tag-row">
              <span class="brand-tag">宠物之家</span>
              <span v-if="product.category" class="cat-pill">{{ product.category }}</span>
            </p>
            <h1 class="title">{{ product.name }}</h1>
            <p class="subtitle">{{ product.subtitle }}</p>

            <div class="price-box">
              <div class="price-line">
                <span class="currency">¥</span>
                <span class="price">{{ formatPrice(product.price) }}</span>
                <span v-if="product.original_price && product.original_price > product.price" class="orig">
                  ¥{{ formatPrice(product.original_price) }}
                </span>
              </div>
              <p class="meta-line">
                <span v-if="product.sales != null">已售 {{ product.sales }}+</span>
                <span>库存 {{ product.stock }} 件</span>
                <span>浙江 · 杭州发货</span>
              </p>
            </div>

            <div class="qty-row">
              <span class="qty-label">数量</span>
              <div class="qty-ctrl">
                <button type="button" aria-label="减少" :disabled="qty <= 1" @click="qty = Math.max(1, qty - 1)">−</button>
                <input v-model.number="qty" type="number" min="1" :max="product.stock" class="qty-input" @change="clampQty" />
                <button type="button" aria-label="增加" :disabled="qty >= product.stock" @click="qty = Math.min(product.stock, qty + 1)">
                  +
                </button>
              </div>
            </div>

            <div class="actions">
              <button type="button" class="btn btn-cart" @click="onAddCart">加入购物车</button>
              <button type="button" class="btn btn-buy" @click="onBuyNow">立即购买</button>
            </div>

            <p class="ship-note">包邮 · 宠物之家自营 · 演示环境不发起真实支付</p>
          </div>
        </div>

        <section class="detail-section card">
          <h2>商品详情</h2>
          <div class="detail-body">
            <p v-for="(para, i) in descriptionParagraphs" :key="i">{{ para }}</p>
          </div>
        </section>
      </template>
    </DataState>

    <CommerceDock />
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch } from "vue";
import { useRoute, useRouter } from "vue-router";
import DataState from "@/components/DataState.vue";
import CommerceDock from "@/components/shop/CommerceDock.vue";
import { fetchProduct } from "@/services/modules/shop";
import { getMockProductById } from "@/mocks/shop";
import type { ProductDetail } from "@/types/shop";
import { useShopCartStore } from "@/store/shopCart";
import { flyImageToCart } from "@/composables/useFlyToCart";

type DisplayProduct = {
  id: number;
  name: string;
  subtitle?: string;
  price: number;
  original_price?: number;
  image_url?: string;
  images: string[];
  stock: number;
  sales?: number;
  category?: string;
  description?: string;
};

const route = useRoute();
const router = useRouter();
const cart = useShopCartStore();

const loading = ref(true);
const error = ref("");
const product = ref<DisplayProduct | null>(null);
const activeImg = ref(0);
const qty = ref(1);
const galleryMainRef = ref<HTMLElement | null>(null);

const galleryImages = computed(() => product.value?.images?.filter(Boolean) || []);

const descriptionParagraphs = computed(() => {
  const text = product.value?.description?.trim() || product.value?.subtitle || "暂无详细说明。";
  return text.split(/\n+/).filter(Boolean);
});

const formatPrice = (n: number) => (Number.isInteger(n) ? String(n) : n.toFixed(2));

function clampQty() {
  if (!product.value) return;
  let n = Number(qty.value);
  if (!Number.isFinite(n) || n < 1) n = 1;
  if (n > product.value.stock) n = product.value.stock;
  qty.value = n;
}

function mergeDisplay(api: ProductDetail, mock: ReturnType<typeof getMockProductById>): DisplayProduct {
  const fromApiImgs = api.images?.filter(Boolean) ?? [];
  const imgs =
    fromApiImgs.length > 0
      ? fromApiImgs
      : api.image_url
        ? [api.image_url]
        : mock?.images?.length
          ? mock.images
          : [];
  return {
    id: api.id,
    name: api.name,
    subtitle: api.subtitle || mock?.subtitle,
    price: api.price,
    original_price: mock?.original_price,
    image_url: api.image_url || mock?.image_url,
    images: imgs.length ? imgs : mock?.images || [],
    stock: api.stock,
    sales: mock?.sales,
    category: mock?.category,
    description: api.description || mock?.description || api.subtitle
  };
}

async function load() {
  loading.value = true;
  error.value = "";
  product.value = null;
  activeImg.value = 0;
  qty.value = 1;

  const id = Number(route.params.id);
  if (!Number.isFinite(id) || id < 1) {
    loading.value = false;
    router.replace("/shop");
    return;
  }

  const mock = getMockProductById(id);

  try {
    const data = await fetchProduct(id);
    product.value = mergeDisplay(data, mock);
  } catch {
    if (mock) {
      product.value = {
        id: mock.id,
        name: mock.name,
        subtitle: mock.subtitle,
        price: mock.price,
        original_price: mock.original_price,
        image_url: mock.image_url,
        images: mock.images,
        stock: mock.stock,
        sales: mock.sales,
        category: mock.category,
        description: mock.description
      };
    } else {
      error.value = "";
      product.value = null;
    }
  } finally {
    loading.value = false;
  }

  if (product.value && qty.value > product.value.stock) qty.value = Math.max(1, product.value.stock);
}

watch(
  () => route.params.id,
  () => load(),
  { immediate: true }
);

watch(product, (p) => {
  if (p && activeImg.value >= p.images.length) activeImg.value = 0;
});

function onAddCart() {
  if (!product.value) return;
  clampQty();
  cart.add(
    {
      id: product.value.id,
      name: product.value.name,
      price: product.value.price,
      image_url: product.value.image_url || galleryImages.value[0]
    },
    qty.value
  );
  const imgEl = galleryMainRef.value?.querySelector("img");
  flyImageToCart(product.value.image_url || galleryImages.value[0], imgEl as HTMLElement | null);
}

function onBuyNow() {
  if (!product.value) return;
  clampQty();
  router.push({
    path: "/shop/checkout",
    query: { buyNow: "1", id: String(product.value.id), qty: String(qty.value) }
  });
}
</script>

<style scoped lang="scss">
.detail-page {
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

.gallery-thumbs {
  display: flex;
  gap: 10px;
  margin-top: 12px;
  flex-wrap: wrap;
}

.thumb {
  width: 72px;
  height: 72px;
  padding: 0;
  border: 2px solid transparent;
  border-radius: 10px;
  overflow: hidden;
  cursor: pointer;
  background: var(--surface-muted);

  img {
    width: 100%;
    height: 100%;
    object-fit: cover;
    display: block;
  }

  &.on {
    border-color: var(--primary-strong);
  }
}

.info {
  display: flex;
  flex-direction: column;
  gap: 12px;
  min-width: 0;
}

.tag-row {
  margin: 0;
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  align-items: center;
}

.brand-tag {
  display: inline-block;
  padding: 2px 8px;
  border-radius: 4px;
  background: linear-gradient(135deg, #ff6a3c, var(--primary-strong));
  color: #fff;
  font-size: 12px;
  font-weight: 800;
}

.cat-pill {
  padding: 2px 10px;
  border-radius: 999px;
  background: var(--chip-bg);
  border: 1px solid var(--chip-border);
  font-size: 13px;
  font-weight: 700;
  color: var(--text-heading-soft);
}

.title {
  margin: 0;
  font-size: 22px;
  font-weight: 900;
  color: var(--text);
  line-height: 1.35;
}

.subtitle {
  margin: 0;
  font-size: 15px;
  color: var(--muted);
  line-height: 1.5;
}

.price-box {
  padding: 16px;
  border-radius: var(--radius-md);
  background: linear-gradient(135deg, #fff5ed 0%, #ffe8d6 100%);
  border: 1px solid var(--chip-border);
}

.price-line {
  display: flex;
  align-items: baseline;
  gap: 4px;
  flex-wrap: wrap;
}

.currency {
  font-size: 18px;
  font-weight: 800;
  color: #ff5000;
}

.price {
  font-size: 28px;
  font-weight: 900;
  color: #ff5000;
}

.orig {
  font-size: 15px;
  color: var(--on-white-text);
  text-decoration: line-through;
}

.meta-line {
  margin: 10px 0 0;
  display: flex;
  flex-wrap: wrap;
  gap: 12px 16px;
  font-size: 14px;
  color: var(--muted);
}

.qty-row {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-top: 4px;
}

.qty-label {
  font-size: 15px;
  font-weight: 700;
  color: var(--text);
}

.qty-ctrl {
  display: flex;
  align-items: center;
  border: 1px solid var(--border-warm);
  border-radius: 10px;
  overflow: hidden;
  background: var(--surface);

  button {
    width: 40px;
    height: 40px;
    border: none;
    background: var(--surface-muted);
    font-size: 20px;
    cursor: pointer;
    color: var(--text);

    &:disabled {
      opacity: 0.4;
      cursor: not-allowed;
    }

    &:hover:not(:disabled) {
      background: var(--chip-bg);
    }
  }
}

.qty-input {
  width: 48px;
  height: 40px;
  border: none;
  border-left: 1px solid var(--border-warm);
  border-right: 1px solid var(--border-warm);
  text-align: center;
  font-size: 16px;
  font-weight: 700;
  color: var(--text);

  /* hide spinners */
  appearance: textfield;
  &::-webkit-outer-spin-button,
  &::-webkit-inner-spin-button {
    appearance: none;
    margin: 0;
  }
}

.actions {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  margin-top: 8px;
}

.btn {
  flex: 1;
  min-width: 140px;
  padding: 14px 20px;
  border: none;
  border-radius: 12px;
  font-size: 16px;
  font-weight: 900;
  cursor: pointer;
  transition: filter 0.15s, transform 0.12s;

  &:active {
    transform: scale(0.99);
  }
}

.btn-cart {
  background: linear-gradient(135deg, #fff1e5 0%, #ffe4cc 100%);
  color: var(--primary-strong);
  border: 2px solid var(--primary);

  &:hover {
    filter: brightness(1.02);
  }
}

.btn-buy {
  background: linear-gradient(135deg, #ff6a3c 0%, #ff5000 100%);
  color: #fff;
  box-shadow: 0 6px 18px rgba(255, 80, 0, 0.25);

  &:hover {
    filter: brightness(1.05);
  }
}

.ship-note {
  margin: 0;
  font-size: 13px;
  color: var(--muted-soft);
}

.detail-section {
  padding: 20px 22px 24px;

  h2 {
    margin: 0 0 14px;
    font-size: 18px;
    font-weight: 900;
    color: var(--text-heading-soft);
    padding-bottom: 10px;
    border-bottom: 1px solid var(--border-warm);
  }
}

.detail-body {
  font-size: 16px;
  line-height: 1.75;
  color: var(--text);

  p {
    margin: 0 0 12px;

    &:last-child {
      margin-bottom: 0;
    }
  }
}

@media (max-width: 840px) {
  .detail-grid {
    grid-template-columns: 1fr;
    padding: 16px;
  }

  .gallery-main {
    max-height: none;
  }
}
</style>
