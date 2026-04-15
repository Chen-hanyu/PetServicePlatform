<template>
  <section class="shop-page">
    <!-- 淘宝式顶栏：左标识 + 中置搜索（宝贝下拉 + 输入 + 搜索按钮） -->
    <header class="tb-top card">
      <div class="tb-top-row">
        <RouterLink to="/shop" class="tb-brand">
          <span class="tb-brand-orb" aria-hidden="true">宠</span>
          <div class="tb-brand-text">
            <strong>宠物商城</strong>
            <span>热卖好物</span>
          </div>
        </RouterLink>
        <div class="tb-search-wrap">
          <div class="tb-search-pill" @keydown.enter.prevent="loadProducts">
            <label class="visually-hidden" for="shop-q">搜索商品</label>
            <select v-model="searchScope" class="tb-scope" aria-label="搜索范围">
              <option value="item">宝贝</option>
              <option value="shop">店铺</option>
            </select>
            <input
              id="shop-q"
              v-model="keyword"
              type="search"
              class="tb-input"
              placeholder="搜索猫粮、玩具、用品…"
              autocomplete="off"
            />
            <button type="button" class="tb-search-btn" @click="loadProducts">搜索</button>
          </div>
          <div class="tb-guess" aria-label="猜你想搜">
            <span class="tb-guess-label">猜你想搜：</span>
            <button
              v-for="kw in hotKeywords"
              :key="kw"
              type="button"
              class="tb-guess-link"
              @click="
                keyword = kw;
                loadProducts();
              "
            >
              {{ kw }}
            </button>
          </div>
        </div>
      </div>
    </header>

    <p class="tb-promo-strip">
      <span class="tb-strip-ico" aria-hidden="true">🎁</span>
      点击商品进入详情页可加入购物车或立即购买；右侧竖条打开购物车结算（演示数据）
    </p>

    <nav class="breadcrumb" aria-label="面包屑导航">
      <RouterLink to="/home">首页</RouterLink>
      <span class="sep">/</span>
      <span class="current">宠物商城</span>
    </nav>

    <!-- 商城首页三栏：分类 | 轮播 | 右侧活动卡（参考淘宝首页） -->
    <div class="showcase">
      <aside class="category-pane card" aria-label="商品分类">
        <h2 class="pane-title">分类</h2>
        <ul class="category-list">
          <li>
            <button type="button" :class="['cat-link', { active: selectedCategory === null }]" @click="selectedCategory = null">
              全部商品
            </button>
          </li>
          <li v-for="cat in categories" :key="cat.id">
            <button
              type="button"
              :class="['cat-link', { active: selectedCategory === cat.id }]"
              @click="selectedCategory = cat.id"
            >
              <span class="cat-ico">{{ categoryIcons[cat.name] || "📦" }}</span>
              {{ cat.name }}
            </button>
          </li>
        </ul>
      </aside>

      <div class="hero-pane card">
        <div class="hero-viewport">
          <div class="hero-track" :style="{ transform: `translateX(-${slideIndex * 100}%)` }">
            <article v-for="(s, i) in heroSlides" :key="i" class="hero-slide" :style="{ background: s.tint }">
              <img :src="s.img" class="hero-img" alt="" loading="lazy" />
              <div class="hero-caption">
                <h3>{{ s.title }}</h3>
                <p>{{ s.sub }}</p>
              </div>
            </article>
          </div>
        </div>
        <div class="hero-dots" role="tablist" aria-label="轮播">
          <button
            v-for="(_, i) in heroSlides"
            :key="'d' + i"
            type="button"
            role="tab"
            :class="['hero-dot', { on: i === slideIndex }]"
            :aria-selected="i === slideIndex"
            @click="slideIndex = i"
          />
        </div>
      </div>

      <aside class="promo-pane" aria-label="活动入口">
        <button
          v-for="p in promoTiles"
          :key="p.title"
          type="button"
          class="promo-card"
          :style="{ background: p.grad }"
          @click="onPromoClick(p.catId)"
        >
          <div class="promo-text">
            <strong>{{ p.title }}</strong>
            <span>{{ p.sub }}</span>
          </div>
          <img :src="p.img" class="promo-thumb" alt="" />
        </button>
      </aside>
    </div>

    <!-- 搜索结果式列表区（参考淘宝搜索页） -->
    <div id="shop-results" class="results-block">
      <div class="list-panel card">
        <div class="category-rail">
          <div class="rail-scroll">
            <button type="button" :class="['rail-item', { active: selectedCategory === null }]" @click="selectedCategory = null">
              <span class="rail-ico">🏠</span>
              全部
            </button>
            <button
              v-for="cat in categories"
              :key="'r-' + cat.id"
              type="button"
              :class="['rail-item', { active: selectedCategory === cat.id }]"
              @click="selectedCategory = cat.id"
            >
              <span class="rail-ico">{{ categoryIcons[cat.name] || "📦" }}</span>
              {{ cat.name }}
            </button>
          </div>
        </div>

        <div class="toolbar">
          <div class="toolbar-left">
            <span class="result-count">共 <strong>{{ filteredProducts.length }}</strong> 件宝贝</span>
          </div>
          <div class="sort-bar" role="tablist" aria-label="排序">
            <button type="button" role="tab" :class="['sort-item', { active: sortBy === 'default' }]" @click="sortBy = 'default'">
              综合
            </button>
            <button type="button" role="tab" :class="['sort-item', { active: sortBy === 'sales' }]" @click="sortBy = 'sales'">
              销量
            </button>
            <button type="button" role="tab" :class="['sort-item', 'sort-price', { active: sortBy === 'price' }]" @click="togglePriceSort">
              价格
              <span v-if="sortBy === 'price'" class="price-arrows" aria-hidden="true">
                <span :class="{ up: true, on: priceOrder === 'asc' }">▲</span>
                <span :class="{ down: true, on: priceOrder === 'desc' }">▼</span>
              </span>
              <span v-else class="price-arrows muted">▲▼</span>
            </button>
            <button type="button" role="tab" :class="['sort-item', { active: sortBy === 'new' }]" @click="sortBy = 'new'">
              上新
            </button>
          </div>
        </div>

        <DataState
          class="shop-state"
          :loading="loading"
          :error="error"
          :empty="filteredProducts.length === 0"
          empty-text="没有找到相关宝贝，换个词试试"
        >
          <div class="products-grid">
            <RouterLink
              v-for="product in filteredProducts"
              :key="product.id"
              class="product-card-link"
              :to="`/shop/product/${product.id}`"
            >
              <article class="product-card">
                <div class="product-media">
                  <img :src="product.image_url" :alt="product.name" loading="lazy" />
                  <div class="media-badges">
                    <span v-if="product.original_price > product.price" class="badge-discount">
                      -{{ Math.round((1 - product.price / product.original_price) * 100) }}%
                    </span>
                    <span v-if="(product.sales || 0) >= 500" class="badge-hot">热销</span>
                  </div>
                </div>
                <div class="product-body">
                  <h3 class="product-title" :title="product.name">
                    <span class="title-tag">宠物之家</span>
                    <span class="title-text" v-html="highlightTitle(product.name)"></span>
                  </h3>
                  <p class="product-sub">{{ product.subtitle }}</p>
                  <div class="product-tags">
                    <span class="tag">包邮</span>
                    <span v-if="product.stock && product.stock < 30" class="tag warn">库存紧张</span>
                  </div>
                  <div class="product-price-row">
                    <div class="prices">
                      <span class="currency">¥</span>
                      <span class="sale-price">{{ formatPrice(product.price) }}</span>
                      <span v-if="product.original_price > product.price" class="orig">¥{{ formatPrice(product.original_price) }}</span>
                    </div>
                    <span class="loc-text">浙江 · 杭州</span>
                  </div>
                  <p class="shop-line">宠物之家自营 · {{ formatSales(product.sales) }}</p>
                  <span class="detail-cta">查看详情</span>
                </div>
              </article>
            </RouterLink>
          </div>
        </DataState>
      </div>
    </div>
  </section>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onBeforeUnmount } from "vue";
import DataState from "@/components/DataState.vue";
import { fetchProducts } from "@/api/modules/shop";
import { mockProducts } from "@/mocks/shop";

const loading = ref(false);
const error = ref("");
const categories = ref<{ id: number; name: string }[]>([]);
const products = ref<any[]>([]);
const selectedCategory = ref<number | null>(null);
const keyword = ref("");
const searchScope = ref("item");
const sortBy = ref<"default" | "sales" | "price" | "new">("default");
const priceOrder = ref<"asc" | "desc">("asc");
const slideIndex = ref(0);

const hotKeywords = ["猫粮", "狗粮", "玩具", "零食", "饮水机"];

const categoryIcons: Record<string, string> = {
  主粮: "🍖",
  零食: "🦴",
  玩具: "🎾",
  用品: "🧴",
  保健: "💊"
};

const heroSlides = [
  {
    title: "无谷主粮节",
    sub: "满 299 减 30 · 正品保障",
    img: "https://images.unsplash.com/photo-1589924691995-400dc9ecc119?auto=format&fit=crop&w=960&q=80",
    tint: "linear-gradient(135deg, #e8f4fc 0%, #d4e8f7 100%)"
  },
  {
    title: "智能用品上新",
    sub: "饮水机 · 食盆 · 省心养宠",
    img: "https://images.unsplash.com/photo-1583337130417-3346a1be7dee?auto=format&fit=crop&w=960&q=80",
    tint: "linear-gradient(135deg, #fff1e5 0%, #ffe4cc 100%)"
  },
  {
    title: "零食狂欢",
    sub: "罐头冻干 · 狗狗猫咪都爱",
    img: "https://images.unsplash.com/photo-1568640347023-a616a30bc3bd?auto=format&fit=crop&w=960&q=80",
    tint: "linear-gradient(135deg, #fce8ef 0%, #ffd6e5 100%)"
  }
];

const promoTiles = [
  {
    title: "主粮特惠",
    sub: "进口配方",
    catId: 1,
    grad: "linear-gradient(135deg, #ffb089 0%, #ff9d7a 55%, #f17c53 100%)",
    img: "https://images.unsplash.com/photo-1589924691995-400dc9ecc119?auto=format&fit=crop&w=200&q=80"
  },
  {
    title: "零食铲货",
    sub: "肉粒冻干",
    catId: 2,
    grad: "linear-gradient(135deg, #ffc9d9 0%, #ff9db8 100%)",
    img: "https://images.unsplash.com/photo-1568640347023-a616a30bc3bd?auto=format&fit=crop&w=200&q=80"
  },
  {
    title: "玩具上新",
    sub: "解闷神器",
    catId: 3,
    grad: "linear-gradient(135deg, #d4a574 0%, #b8835a 100%)",
    img: "https://images.unsplash.com/photo-1545249390-6bdfa286032f?auto=format&fit=crop&w=200&q=80"
  },
  {
    title: "健康养护",
    sub: "营养保健",
    catId: 5,
    grad: "linear-gradient(135deg, #a8c9d8 0%, #7aa8bd 100%)",
    img: "https://images.unsplash.com/photo-1519098901909-b1553a1190af?auto=format&fit=crop&w=200&q=80"
  }
];

let slideTimer: ReturnType<typeof setInterval> | undefined;

const formatPrice = (n: number) => (Number.isInteger(n) ? String(n) : n.toFixed(2));

const formatSales = (n: number | undefined) => {
  const s = n ?? 0;
  if (s >= 10000) return `${(s / 10000).toFixed(1)}万+人付款`;
  return `${s}+人付款`;
};

const highlightTitle = (name: string) => {
  const q = keyword.value.trim();
  if (!q) return escapeHtml(name);
  const lower = name.toLowerCase();
  const qi = q.toLowerCase();
  const i = lower.indexOf(qi);
  if (i < 0) return escapeHtml(name);
  const a = escapeHtml(name.slice(0, i));
  const b = escapeHtml(name.slice(i, i + q.length));
  const c = escapeHtml(name.slice(i + q.length));
  return `${a}<em class="hl">${b}</em>${c}`;
};

const escapeHtml = (s: string) =>
  s.replace(/&/g, "&amp;").replace(/</g, "&lt;").replace(/>/g, "&gt;").replace(/"/g, "&quot;");

const productCategoryId = (p: any): number | null => {
  if (p.category_id != null) return p.category_id;
  const c = categories.value.find((x) => x.name === p.category);
  return c?.id ?? null;
};

const filteredProducts = computed(() => {
  let result = [...products.value];
  if (selectedCategory.value != null) {
    result = result.filter((p) => productCategoryId(p) === selectedCategory.value);
  }
  const kw = keyword.value.trim().toLowerCase();
  if (kw) {
    result = result.filter(
      (p) =>
        p.name.toLowerCase().includes(kw) ||
        (p.subtitle && String(p.subtitle).toLowerCase().includes(kw)) ||
        (p.category && String(p.category).toLowerCase().includes(kw))
    );
  }
  if (sortBy.value === "sales") {
    result.sort((a, b) => (b.sales ?? 0) - (a.sales ?? 0));
  } else if (sortBy.value === "price") {
    result.sort((a, b) => (priceOrder.value === "asc" ? a.price - b.price : b.price - a.price));
  } else if (sortBy.value === "new") {
    result.sort((a, b) => b.id - a.id);
  } else {
    result.sort((a, b) => (b.sales ?? 0) - (a.sales ?? 0));
  }
  return result;
});

const togglePriceSort = () => {
  if (sortBy.value === "price") {
    priceOrder.value = priceOrder.value === "asc" ? "desc" : "asc";
  } else {
    sortBy.value = "price";
    priceOrder.value = "asc";
  }
};

const loadProducts = async () => {
  loading.value = true;
  error.value = "";
  try {
    const data = await fetchProducts({ page: 1, page_size: 20 });
    products.value = data.list || [];
  } catch {
    products.value = mockProducts;
  } finally {
    loading.value = false;
  }
};

const onPromoClick = (catId: number) => {
  selectedCategory.value = catId;
  document.getElementById("shop-results")?.scrollIntoView({ behavior: "smooth", block: "start" });
};

onMounted(async () => {
  categories.value = [
    { id: 1, name: "主粮" },
    { id: 2, name: "零食" },
    { id: 3, name: "玩具" },
    { id: 4, name: "用品" },
    { id: 5, name: "保健" }
  ];
  await loadProducts();
  slideTimer = window.setInterval(() => {
    slideIndex.value = (slideIndex.value + 1) % heroSlides.length;
  }, 5200);
});

onBeforeUnmount(() => {
  if (slideTimer) clearInterval(slideTimer);
});
</script>

<style scoped lang="scss">
.visually-hidden {
  position: absolute;
  width: 1px;
  height: 1px;
  padding: 0;
  margin: -1px;
  overflow: hidden;
  clip: rect(0, 0, 0, 0);
  white-space: nowrap;
  border: 0;
}

.shop-page {
  position: relative;
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 8px 40px;
  padding-right: max(8px, env(safe-area-inset-right));
  font-size: 16px;
  line-height: 1.55;
}

/* —— 顶栏搜索（淘宝结构，暖色主题） —— */
.tb-top {
  padding: 18px 22px 16px;
  margin-bottom: 12px;
}

.tb-top-row {
  display: flex;
  align-items: flex-start;
  gap: 20px;
  flex-wrap: wrap;
}

.tb-brand {
  display: flex;
  align-items: center;
  gap: 10px;
  text-decoration: none;
  color: inherit;
  flex-shrink: 0;
}

.tb-brand-orb {
  width: 48px;
  height: 48px;
  border-radius: 14px;
  background: linear-gradient(135deg, var(--primary) 0%, var(--primary-strong) 100%);
  color: #fff;
  font-size: 22px;
  font-weight: 900;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 6px 16px rgba(241, 124, 83, 0.35);
}

.tb-brand-text {
  display: flex;
  flex-direction: column;
  gap: 2px;

  strong {
    font-size: 20px;
    font-weight: 900;
    color: var(--text-heading-soft);
    letter-spacing: -0.02em;
  }

  span {
    font-size: 14px;
    color: var(--muted);
    font-weight: 600;
  }
}

.tb-search-wrap {
  flex: 1;
  min-width: min(100%, 320px);
}

.tb-search-pill {
  display: flex;
  align-items: stretch;
  background: var(--surface);
  border: 2px solid var(--primary);
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 2px 10px rgba(102, 72, 48, 0.06);

  &:focus-within {
    box-shadow: 0 0 0 3px rgba(255, 157, 122, 0.25);
  }
}

.tb-scope {
  flex-shrink: 0;
  width: 80px;
  padding: 0 10px;
  border: none;
  border-right: 1px solid var(--border-warm);
  background: var(--surface-muted);
  font-size: 15px;
  font-weight: 700;
  color: var(--text-heading-soft);
  cursor: pointer;
}

.tb-input {
  flex: 1;
  min-width: 0;
  border: none;
  outline: none;
  padding: 14px 16px;
  font-size: 16px;
  color: var(--text);

  &::placeholder {
    color: var(--muted-soft);
  }
}

.tb-search-btn {
  flex-shrink: 0;
  padding: 0 32px;
  border: none;
  background: linear-gradient(135deg, #ff6a3c 0%, var(--primary-strong) 100%);
  color: #fff;
  font-size: 17px;
  font-weight: 800;
  cursor: pointer;
  transition: filter 0.15s;

  &:hover {
    filter: brightness(1.05);
  }
}

.tb-guess {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 6px 12px;
  margin-top: 10px;
  padding-left: 2px;
}

.tb-guess-label {
  font-size: 14px;
  color: var(--muted);
}

.tb-guess-link {
  padding: 0;
  border: none;
  background: none;
  font-size: 14px;
  color: var(--text-heading-soft);
  cursor: pointer;
  font-weight: 600;

  &:hover {
    color: var(--primary-strong);
    text-decoration: underline;
  }
}

.tb-promo-strip {
  display: flex;
  align-items: center;
  gap: 8px;
  margin: 0 0 12px;
  padding: 12px 16px;
  border-radius: var(--radius-md);
  background: linear-gradient(90deg, #fff1e5 0%, #ffe9d9 100%);
  border: 1px solid var(--chip-border);
  font-size: 14px;
  color: var(--text-subheading);
  font-weight: 600;
}

.tb-strip-ico {
  font-size: 18px;
}

.breadcrumb {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 15px;
  color: var(--muted);
  margin-bottom: 12px;

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
  }
}

/* —— 首页三栏 —— */
.showcase {
  display: grid;
  grid-template-columns: 208px minmax(0, 1fr) 248px;
  gap: 12px;
  align-items: stretch;
  margin-bottom: 14px;
}

.category-pane {
  padding: 0;
  overflow: hidden;
  background: var(--surface-muted);
}

.pane-title {
  margin: 0;
  padding: 14px 16px;
  font-size: 17px;
  font-weight: 900;
  color: var(--text);
  border-bottom: 1px solid var(--border-warm);
  background: var(--surface);
}

.category-list {
  list-style: none;
  margin: 0;
  padding: 6px 0 10px;
}

.cat-link {
  display: flex;
  align-items: center;
  gap: 8px;
  width: 100%;
  padding: 12px 16px;
  border: none;
  background: transparent;
  font-size: 15px;
  font-weight: 600;
  color: var(--muted);
  cursor: pointer;
  text-align: left;
  transition: background 0.15s, color 0.15s;

  .cat-ico {
    width: 24px;
    text-align: center;
    font-size: 18px;
  }

  &:hover {
    background: var(--chip-bg);
    color: var(--text-heading-soft);
  }

  &.active {
    background: var(--surface);
    color: var(--primary-strong);
    box-shadow: inset 3px 0 0 var(--primary-strong);
  }
}

.hero-pane {
  padding: 0;
  overflow: hidden;
  display: flex;
  flex-direction: column;
  min-width: 0;
}

.hero-viewport {
  overflow: hidden;
  aspect-ratio: 16 / 9;
  max-height: 300px;
  background: var(--surface-muted);
}

.hero-track {
  display: flex;
  height: 100%;
  width: 100%;
  transition: transform 0.45s cubic-bezier(0.33, 1, 0.68, 1);
}

.hero-slide {
  position: relative;
  flex: 0 0 100%;
  height: 100%;
  overflow: hidden;
}

.hero-img {
  position: absolute;
  inset: 0;
  width: 100%;
  height: 100%;
  object-fit: cover;
  opacity: 0.92;
}

.hero-caption {
  position: absolute;
  left: 16px;
  bottom: 16px;
  right: 16px;
  padding: 12px 14px;
  border-radius: 10px;
  background: rgba(61, 47, 40, 0.55);
  color: #fff;
  backdrop-filter: blur(6px);

  h3 {
    margin: 0 0 4px;
    font-size: 22px;
    font-weight: 900;
  }

  p {
    margin: 0;
    font-size: 15px;
    opacity: 0.95;
  }
}

.hero-dots {
  display: flex;
  justify-content: center;
  gap: 8px;
  padding: 10px;
  background: var(--surface);
  border-top: 1px solid var(--border-warm);
}

.hero-dot {
  width: 8px;
  height: 8px;
  padding: 0;
  border: none;
  border-radius: 50%;
  background: var(--border-warm-mid);
  cursor: pointer;
  transition: transform 0.2s, background 0.2s;

  &.on {
    background: var(--primary-strong);
    transform: scale(1.15);
  }
}

.promo-pane {
  display: flex;
  flex-direction: column;
  gap: 8px;
  min-width: 0;
}

.promo-card {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 8px;
  padding: 10px 12px;
  border: none;
  border-radius: 12px;
  cursor: pointer;
  text-align: left;
  color: #fff;
  min-height: 0;
  flex: 1;
  box-shadow: 0 4px 12px rgba(102, 72, 48, 0.12);
  transition: transform 0.2s, filter 0.2s;

  &:hover {
    transform: translateY(-1px);
    filter: brightness(1.04);
  }
}

.promo-text {
  display: flex;
  flex-direction: column;
  gap: 2px;
  min-width: 0;

  strong {
    font-size: 16px;
    font-weight: 900;
    text-shadow: 0 1px 2px rgba(0, 0, 0, 0.12);
  }

  span {
    font-size: 13px;
    opacity: 0.95;
    font-weight: 600;
  }
}

.promo-thumb {
  width: 52px;
  height: 52px;
  border-radius: 8px;
  object-fit: cover;
  flex-shrink: 0;
  border: 2px solid rgba(255, 255, 255, 0.35);
}

.results-block {
  min-width: 0;
}

.list-panel {
  padding: 0;
  overflow: hidden;
  display: flex;
  flex-direction: column;
  min-width: 0;
}

.category-rail {
  display: none;
  padding: 10px 12px;
  border-bottom: 1px solid var(--border-warm);
  background: var(--surface-tint);
}

.rail-scroll {
  display: flex;
  gap: 8px;
  overflow-x: auto;
  padding-bottom: 4px;
  scrollbar-width: thin;
}

.rail-item {
  flex-shrink: 0;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 2px;
  padding: 10px 16px;
  border-radius: 12px;
  border: 1px solid var(--border-warm);
  background: var(--surface);
  font-size: 13px;
  font-weight: 700;
  color: var(--muted);
  cursor: pointer;

  &.active {
    border-color: var(--primary);
    background: var(--chip-active-bg);
    color: var(--primary-strong);
  }
}

.toolbar {
  display: flex;
  flex-wrap: wrap;
  justify-content: space-between;
  align-items: center;
  gap: 10px;
  padding: 12px 16px;
  border-bottom: 1px solid var(--border-warm);
  background: var(--surface);
}

.result-count {
  font-size: 15px;
  color: var(--muted);

  strong {
    color: var(--primary-strong);
    font-weight: 800;
  }
}

.sort-bar {
  display: flex;
  flex-wrap: wrap;
  gap: 4px;
  background: var(--surface-muted);
  padding: 4px;
  border-radius: 10px;
}

.sort-item {
  padding: 8px 16px;
  border: none;
  border-radius: 8px;
  background: transparent;
  font-size: 15px;
  font-weight: 700;
  color: var(--muted);
  cursor: pointer;
  display: inline-flex;
  align-items: center;
  gap: 4px;

  &.active {
    background: var(--surface);
    color: var(--primary-strong);
    box-shadow: 0 1px 6px rgba(102, 72, 48, 0.08);
  }
}

.sort-price .price-arrows {
  display: inline-flex;
  flex-direction: column;
  font-size: 8px;
  line-height: 1;

  .up,
  .down {
    opacity: 0.25;
  }

  .on {
    opacity: 1;
    color: var(--primary-strong);
  }

  &.muted {
    opacity: 0.35;
    font-size: 9px;
  }
}

:deep(.shop-state) {
  min-width: 0;
  width: 100%;
  padding: 12px 12px 16px;
  background: var(--surface-muted);
}

:deep(.shop-state .muted),
:deep(.shop-state .error) {
  margin: 24px 0;
  text-align: center;
  font-size: 16px;
}

.products-grid {
  display: grid;
  grid-template-columns: repeat(5, minmax(0, 1fr));
  gap: 10px;
  align-items: stretch;
}

.product-card-link {
  text-decoration: none;
  color: inherit;
  display: block;
  min-width: 0;
  border-radius: 10px;
  outline: none;

  &:focus-visible {
    box-shadow: 0 0 0 3px rgba(255, 157, 122, 0.45);
  }
}

.product-card {
  display: flex;
  flex-direction: column;
  min-width: 0;
  min-height: 100%;
  background: var(--surface);
  border-radius: 10px;
  overflow: hidden;
  border: 1px solid var(--border-warm);
  transition: box-shadow 0.2s, transform 0.2s;

  &:hover {
    transform: translateY(-2px);
    box-shadow: 0 6px 20px rgba(102, 72, 48, 0.1);
  }
}

.product-media {
  position: relative;
  aspect-ratio: 1;
  min-height: 132px;
  background: var(--surface-muted);
  overflow: hidden;

  img {
    width: 100%;
    height: 100%;
    object-fit: cover;
    display: block;
  }
}

.media-badges {
  position: absolute;
  top: 8px;
  left: 8px;
  display: flex;
  flex-direction: column;
  gap: 4px;
  z-index: 1;
}

.badge-discount {
  padding: 3px 7px;
  border-radius: 4px;
  background: linear-gradient(135deg, #ff6b4a, var(--primary-strong));
  color: #fff;
  font-size: 12px;
  font-weight: 800;
}

.badge-hot {
  padding: 3px 7px;
  border-radius: 4px;
  background: rgba(0, 0, 0, 0.55);
  color: #fff;
  font-size: 12px;
  font-weight: 700;
}

.product-body {
  flex: 1;
  display: flex;
  flex-direction: column;
  padding: 10px 12px 12px;
  text-align: left;
}

.product-title {
  margin: 0 0 6px;
  font-size: 15px;
  font-weight: 600;
  line-height: 1.5;
  color: #1a1a1a;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  min-height: 3em;
}

.title-tag {
  display: inline-block;
  margin-right: 4px;
  padding: 1px 5px;
  border-radius: 3px;
  background: linear-gradient(135deg, #ff6a3c, var(--primary-strong));
  color: #fff;
  font-size: 11px;
  font-weight: 800;
  vertical-align: 1px;
}

.title-text :deep(.hl) {
  font-style: normal;
  color: #ff5000;
  font-weight: 800;
}

.product-sub {
  margin: 0 0 6px;
  font-size: 13px;
  color: var(--muted);
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.product-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 4px;
  margin-bottom: 6px;

  .tag {
    padding: 2px 7px;
    border-radius: 4px;
    font-size: 12px;
    font-weight: 700;
    background: #fff1e5;
    color: var(--primary-strong);
    border: 1px solid #ffd5b8;

    &.warn {
      background: #fff8e6;
      color: #c47f00;
      border-color: #ffe0a3;
    }
  }
}

.product-price-row {
  display: flex;
  justify-content: space-between;
  align-items: flex-end;
  gap: 6px;
  margin-top: auto;
  margin-bottom: 4px;
}

.prices {
  display: flex;
  align-items: baseline;
  gap: 1px;
}

.currency {
  font-size: 13px;
  font-weight: 800;
  color: #ff5000;
}

.sale-price {
  font-size: 20px;
  font-weight: 800;
  color: #ff5000;
}

.orig {
  margin-left: 4px;
  font-size: 13px;
  color: #b5a89e;
  text-decoration: line-through;
}

.loc-text {
  font-size: 13px;
  color: var(--muted-soft);
  flex-shrink: 0;
}

.shop-line {
  margin: 0 0 8px;
  font-size: 13px;
  color: var(--muted);
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.detail-cta {
  display: block;
  margin-top: 2px;
  padding: 10px;
  text-align: center;
  border-radius: 10px;
  font-size: 15px;
  font-weight: 800;
  color: var(--primary-strong);
  background: linear-gradient(135deg, #fff1e5 0%, #ffe4cc 100%);
  border: 1px solid var(--chip-border);
  transition: background 0.15s, border-color 0.15s;
}

.product-card-link:hover .detail-cta {
  border-color: var(--primary);
  background: var(--chip-active-bg);
}

@media (max-width: 1320px) {
  .products-grid {
    grid-template-columns: repeat(4, minmax(0, 1fr));
  }
}

@media (max-width: 1024px) {
  .showcase {
    grid-template-columns: 188px minmax(0, 1fr) 220px;
  }
}

@media (max-width: 900px) {
  .showcase {
    grid-template-columns: 1fr;
  }

  .category-pane {
    display: none;
  }

  .promo-pane {
    flex-direction: row;
    flex-wrap: wrap;
  }

  .promo-card {
    flex: 1 1 calc(50% - 4px);
    min-height: 72px;
  }

  .hero-viewport {
    max-height: 220px;
  }

  .category-rail {
    display: block;
  }

  .products-grid {
    grid-template-columns: repeat(3, minmax(0, 1fr));
  }
}

@media (max-width: 640px) {
  .products-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
    gap: 8px;
  }

  .promo-card {
    flex: 1 1 100%;
  }
}
</style>
