<template>
  <div class="detail-page">
    <DataState :loading="loading" :error="error" :empty="!product && !loading" empty-text="商品不存在或已下架">
      <template v-if="product">
        <div class="detail-grid">
          <!-- Gallery -->
          <div class="gallery-section">
            <div class="main-image">
              <img :src="galleryImages[activeImg]" :alt="product.name" />
              <span class="image-counter">{{ activeImg + 1 }}/{{ galleryImages.length }}</span>
            </div>
            <div class="thumbnail-row">
              <div 
                v-for="(src, i) in galleryImages" 
                :key="i"
                :class="['thumbnail', { active: i === activeImg }]"
                @click="activeImg = i"
              >
                <img :src="src" alt="" />
              </div>
            </div>
          </div>

          <!-- Product Info -->
          <div class="info-section">
            <div class="product-header">
              <span class="brand-badge">宠物之家</span>
              <span class="category-badge" v-if="product.category">{{ product.category }}</span>
            </div>
            
            <h1 class="product-title">{{ product.name }}</h1>
            <p class="product-subtitle">{{ product.subtitle }}</p>

            <div class="price-section">
              <div class="price-row">
                <span class="currency">¥</span>
                <span class="price">{{ formatPrice(product.price) }}</span>
                <span class="original-price" v-if="product.original_price && product.original_price > product.price">
                  ¥{{ formatPrice(product.original_price) }}
                </span>
              </div>
              <div class="meta-row">
                <span v-if="product.sales != null">已售 {{ product.sales }}+</span>
                <span>库存 {{ product.stock }} 件</span>
                <span>浙江 · 杭州发货</span>
              </div>
            </div>

            <div class="promotion-bar">
              <span class="promo-tag">优惠</span>
              <span class="promo-text">满99包邮，领券立减5元</span>
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <polyline points="9 18 15 12 9 6"/>
              </svg>
            </div>

            <div class="qty-section">
              <span class="qty-label">数量</span>
              <div class="qty-ctrl">
                <button type="button" @click="qty = Math.max(1, qty - 1)" :disabled="qty <= 1">−</button>
                <input v-model.number="qty" type="number" min="1" :max="product.stock" />
                <button type="button" @click="qty = Math.min(product.stock, qty + 1)" :disabled="qty >= product.stock">+</button>
              </div>
            </div>

            <div class="action-buttons">
              <button type="button" class="btn-cart" @click="onAddCart">加入购物车</button>
              <button type="button" class="btn-buy" @click="onBuyNow">立即购买</button>
            </div>

            <div class="service-btns">
              <button class="service-btn">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <path d="M3 9l9-7 9 7v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z"/>
                </svg>
                进店逛逛
              </button>
              <button class="service-btn">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <path d="M20.84 4.61a5.5 5.5 0 0 0-7.78 0L12 5.67l-1.06-1.06a5.5 5.5 0 0 0-7.78 7.78l1.06 1.06L12 21.23l7.78-7.78 1.06-1.06a5.5 5.5 0 0 0 0-7.78z"/>
                </svg>
                收藏商品
              </button>
              <button class="service-btn">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z"/>
                </svg>
                在线客服
              </button>
            </div>
          </div>
        </div>

        <div class="detail-sections">
          <!-- Reviews -->
          <div class="detail-section-card">
            <div class="section-header">
              <h2>宝贝评价 (1.2k+)</h2>
              <button class="view-all-btn">查看全部 →</button>
            </div>
            <div class="reviews-grid">
              <div class="review-item">
                <div class="review-user">
                  <img src="https://api.dicebear.com/7.x/avataaars/svg?seed=Review1" alt="" />
                  <div class="user-info">
                    <span class="username">小木屋的猫</span>
                    <div class="stars">★★★★★</div>
                  </div>
                </div>
                <p class="review-text">大福超级软糯！抹茶味道很正，一点都不甜腻，包装也太可爱了吧～ 顺丰快递很快，好评！</p>
              </div>
              <div class="review-item">
                <div class="review-user">
                  <img src="https://api.dicebear.com/7.x/avataaars/svg?seed=Review2" alt="" />
                  <div class="user-info">
                    <span class="username">J***n</span>
                    <div class="stars">★★★★★</div>
                  </div>
                </div>
                <p class="review-text">回购好几次了，抹茶控必入。口感真的没话说，像是在日本当地吃到的一样。每一颗都很大。</p>
              </div>
            </div>
          </div>

          <!-- Product Details -->
          <div class="detail-section-card">
            <div class="section-divider">
              <div class="divider-line"></div>
              <span class="divider-text">商品详情 Details</span>
              <div class="divider-line"></div>
            </div>
            <div class="detail-content">
              <p v-for="(para, i) in descriptionParagraphs" :key="i">{{ para }}</p>
            </div>
          </div>
        </div>
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

function mergeDisplay(api: ProductDetail, mock: ReturnType<typeof getMockProductById>): DisplayProduct {
  const fromApiImgs = api.images?.filter(Boolean) ?? [];
  const imgs = fromApiImgs.length > 0 ? fromApiImgs : api.image_url ? [api.image_url] : mock?.images?.length ? mock.images : [];
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

watch(() => route.params.id, () => load(), { immediate: true });

watch(product, (p) => {
  if (p && activeImg.value >= p.images.length) activeImg.value = 0;
});

function onAddCart() {
  if (!product.value) return;
  let n = Number(qty.value);
  if (!Number.isFinite(n) || n < 1) n = 1;
  if (n > product.value.stock) n = product.value.stock;
  qty.value = n;
  cart.add(
    {
      id: product.value.id,
      name: product.value.name,
      price: product.value.price,
      image_url: product.value.image_url || galleryImages.value[0]
    },
    qty.value
  );
  const imgEl = galleryMainRef.value?.querySelector("img") as HTMLElement | null;
  flyImageToCart(product.value.image_url || galleryImages.value[0], imgEl);
}

function onBuyNow() {
  if (!product.value) return;
  let n = Number(qty.value);
  if (!Number.isFinite(n) || n < 1) n = 1;
  if (n > product.value.stock) n = product.value.stock;
  qty.value = n;
  router.push({
    path: "/shop/checkout",
    query: { buyNow: "1", id: String(product.value.id), qty: String(qty.value) }
  });
}
</script>

<style scoped lang="scss">
.detail-page {
  max-width: 1200px;
  margin: 0 auto;
  padding: 24px;
  padding-bottom: 80px;
}

.detail-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 48px;
  margin-bottom: 48px;
}

.gallery-section {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.main-image {
  position: relative;
  border-radius: 20px;
  overflow: hidden;
  background: var(--surface-muted);
  aspect-ratio: 1;
  
  img {
    width: 100%;
    height: 100%;
    object-fit: cover;
  }
}

.image-counter {
  position: absolute;
  bottom: 16px;
  right: 16px;
  padding: 8px 16px;
  background: rgba(0, 0, 0, 0.3);
  color: #fff;
  border-radius: 20px;
  font-size: 13px;
  font-weight: 500;
  backdrop-filter: blur(4px);
}

.thumbnail-row {
  display: flex;
  gap: 12px;
}

.thumbnail {
  width: 80px;
  height: 80px;
  border-radius: 12px;
  overflow: hidden;
  cursor: pointer;
  border: 2px solid transparent;
  transition: all 0.2s ease;
  opacity: 0.6;
  
  &:hover {
    opacity: 1;
  }
  
  &.active {
    border-color: var(--primary);
    opacity: 1;
    box-shadow: 0 4px 12px rgba(255, 155, 122, 0.3);
  }
  
  img {
    width: 100%;
    height: 100%;
    object-fit: cover;
  }
}

.info-section {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.product-header {
  display: flex;
  gap: 8px;
}

.brand-badge {
  padding: 6px 14px;
  background: linear-gradient(135deg, var(--primary) 0%, var(--primary-strong) 100%);
  color: #fff;
  border-radius: 8px;
  font-size: 13px;
  font-weight: 800;
}

.category-badge {
  padding: 6px 14px;
  background: var(--chip-bg);
  color: var(--primary);
  border: 1px solid var(--chip-border);
  border-radius: 20px;
  font-size: 13px;
  font-weight: 700;
}

.product-title {
  font-size: 28px;
  font-weight: 800;
  color: var(--text-heading);
  margin: 0;
  line-height: 1.3;
}

.product-subtitle {
  font-size: 16px;
  color: var(--muted);
  margin: 0;
  line-height: 1.6;
}

.price-section {
  padding: 20px;
  background: rgba(255, 155, 122, 0.1);
  border: 1px solid rgba(255, 155, 122, 0.2);
  border-radius: 16px;
}

.price-row {
  display: flex;
  align-items: baseline;
  gap: 8px;
  margin-bottom: 12px;
}

.currency {
  font-size: 20px;
  font-weight: 800;
  color: var(--primary);
}

.price {
  font-size: 36px;
  font-weight: 900;
  color: var(--primary);
}

.original-price {
  font-size: 16px;
  color: var(--muted);
  text-decoration: line-through;
}

.meta-row {
  display: flex;
  gap: 16px;
  font-size: 14px;
  color: var(--muted);
}

.promotion-bar {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 14px 16px;
  background: rgba(255, 155, 122, 0.1);
  border: 1px solid rgba(255, 155, 122, 0.2);
  border-radius: 12px;
  
  svg {
    width: 18px;
    height: 18px;
    color: var(--primary);
    margin-left: auto;
  }
}

.promo-tag {
  padding: 4px 8px;
  background: var(--primary);
  color: #fff;
  border-radius: 6px;
  font-size: 11px;
  font-weight: 700;
}

.promo-text {
  font-size: 14px;
  color: var(--text-heading);
}

.qty-section {
  display: flex;
  align-items: center;
  gap: 20px;
}

.qty-label {
  font-size: 16px;
  font-weight: 700;
  color: var(--text-heading);
}

.qty-ctrl {
  display: flex;
  align-items: center;
  border: 1px solid var(--border-warm);
  border-radius: 12px;
  overflow: hidden;
  background: var(--surface);
  
  button {
    width: 44px;
    height: 44px;
    border: none;
    background: var(--surface-muted);
    font-size: 18px;
    cursor: pointer;
    transition: background 0.2s ease;
    
    &:hover:not(:disabled) {
      background: var(--chip-bg);
    }
    
    &:disabled {
      opacity: 0.4;
      cursor: not-allowed;
    }
  }
  
  input {
    width: 60px;
    height: 44px;
    border: none;
    border-left: 1px solid var(--border-warm);
    border-right: 1px solid var(--border-warm);
    text-align: center;
    font-size: 16px;
    font-weight: 700;
    background: var(--surface);
    
    &::-webkit-outer-spin-button,
    &::-webkit-inner-spin-button {
      -webkit-appearance: none;
      margin: 0;
    }
  }
}

.action-buttons {
  display: flex;
  gap: 16px;
  
  button {
    flex: 1;
    padding: 16px;
    border: none;
    border-radius: 14px;
    font-size: 16px;
    font-weight: 700;
    cursor: pointer;
    transition: all 0.2s ease;
  }
}

.btn-cart {
  background: rgba(255, 155, 122, 0.15);
  color: var(--primary);
  border: 2px solid var(--primary) !important;
  
  &:hover {
    background: rgba(255, 155, 122, 0.25);
  }
}

.btn-buy {
  background: var(--primary);
  color: #fff;
  box-shadow: 0 4px 16px rgba(255, 155, 122, 0.4);
  
  &:hover {
    opacity: 0.9;
    transform: translateY(-2px);
  }
}

.service-btns {
  display: flex;
  gap: 16px;
  padding-top: 8px;
}

.service-btn {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 6px;
  padding: 12px;
  background: none;
  border: none;
  color: var(--muted);
  font-size: 12px;
  cursor: pointer;
  transition: color 0.2s ease;
  
  svg {
    width: 24px;
    height: 24px;
  }
  
  &:hover {
    color: var(--primary);
  }
}

.detail-sections {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.detail-section-card {
  background: var(--surface);
  border-radius: 20px;
  padding: 32px;
  box-shadow: 0 4px 16px rgba(34, 60, 52, 0.06);
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  
  h2 {
    font-size: 20px;
    font-weight: 700;
    color: var(--text-heading);
    margin: 0;
  }
}

.view-all-btn {
  background: none;
  border: none;
  color: var(--primary);
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  
  &:hover {
    text-decoration: underline;
  }
}

.reviews-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 24px;
}

.review-item {
  padding-bottom: 24px;
  border-bottom: 1px solid var(--border-warm);
  
  &:last-child {
    border-bottom: none;
    padding-bottom: 0;
  }
}

.review-user {
  display: flex;
  gap: 12px;
  margin-bottom: 12px;
  
  img {
    width: 44px;
    height: 44px;
    border-radius: 50%;
    border: 2px solid var(--border-warm);
  }
}

.user-info {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.username {
  font-size: 14px;
  font-weight: 700;
  color: var(--text-heading);
}

.stars {
  color: var(--rating);
  font-size: 12px;
}

.review-text {
  font-size: 14px;
  color: var(--muted);
  line-height: 1.6;
  margin: 0;
}

.section-divider {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-bottom: 32px;
}

.divider-line {
  flex: 1;
  height: 1px;
  background: var(--border-warm);
}

.divider-text {
  font-size: 14px;
  font-weight: 700;
  color: var(--muted);
  text-transform: uppercase;
  letter-spacing: 0.1em;
}

.detail-content {
  p {
    font-size: 15px;
    color: var(--muted);
    line-height: 1.8;
    margin: 0 0 16px;
    
    &:last-child {
      margin-bottom: 0;
    }
  }
}

@media (max-width: 1024px) {
  .detail-grid {
    grid-template-columns: 1fr;
    gap: 24px;
  }
}

@media (max-width: 768px) {
  .detail-page {
    padding: 16px;
  }
  
  .reviews-grid {
    grid-template-columns: 1fr;
  }
  
  .action-buttons {
    flex-direction: column;
  }
}
</style>
