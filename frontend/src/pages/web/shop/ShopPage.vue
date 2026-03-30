<template>
  <section class="shop-page">
    <div class="card page-hero">
      <h1>宠物商城</h1>
      <p>品质好物，守护爱宠每一天</p>
    </div>

    <div class="filter-section card">
      <div class="category-tabs">
        <button 
          v-for="cat in categories" 
          :key="cat.id"
          :class="['cat-tab', { active: selectedCategory === cat.id }]"
          @click="selectCategory(cat.id)"
        >
          {{ cat.name }}
        </button>
      </div>
      <div class="search-bar">
        <input v-model="keyword" class="search-input" placeholder="搜索商品..." />
        <button class="search-btn" @click="loadProducts">搜索</button>
      </div>
    </div>

    <div class="content-grid">
      <main class="products-section">
        <div class="products-header">
          <span>共 {{ filteredProducts.length }} 件商品</span>
          <div class="sort-options">
            <button 
              v-for="opt in sortOptions" 
              :key="opt.value"
              :class="['sort-btn', { active: sortBy === opt.value }]"
              @click="sortBy = opt.value"
            >
              {{ opt.label }}
            </button>
          </div>
        </div>

        <DataState :loading="loading" :error="error" :empty="filteredProducts.length === 0" empty-text="暂无商品">
          <div class="products-grid">
            <article v-for="product in filteredProducts" :key="product.id" class="product-card">
              <div class="product-image">
                <img :src="product.image_url" :alt="product.name" />
                <div class="discount-tag" v-if="product.original_price > product.price">
                  -{{ Math.round((1 - product.price / product.original_price) * 100) }}%
                </div>
              </div>
              <div class="product-info">
                <h3>{{ product.name }}</h3>
                <p class="subtitle">{{ product.subtitle }}</p>
                <div class="product-meta">
                  <div class="price-section">
                    <span class="price">¥{{ product.price }}</span>
                    <span class="original-price" v-if="product.original_price > product.price">
                      ¥{{ product.original_price }}
                    </span>
                  </div>
                  <span class="sales">已售 {{ product.sales }}+</span>
                </div>
                <div class="product-actions">
                  <button class="btn btn-primary add-cart-btn" @click="addToCart(product)">
                    加入购物车
                  </button>
                </div>
              </div>
            </article>
          </div>
        </DataState>
      </main>

      <!-- Cart Sidebar -->
      <aside class="cart-sidebar card">
        <div class="cart-header">
          <h3>🛒 购物车</h3>
          <span class="item-count">{{ cartItems.length }} 件</span>
        </div>

        <div class="cart-items" v-if="cartItems.length > 0">
          <div v-for="item in cartItems" :key="item.id" class="cart-item">
            <img :src="item.image" class="item-image" />
            <div class="item-info">
              <span class="item-name">{{ item.name }}</span>
              <span class="item-price">¥{{ item.price }}</span>
            </div>
            <div class="item-qty">
              <button @click="decreaseQty(item)">-</button>
              <span>{{ item.quantity }}</span>
              <button @click="increaseQty(item)">+</button>
            </div>
            <button class="remove-btn" @click="removeItem(item)">×</button>
          </div>
        </div>

        <div class="cart-empty" v-else>
          <p>购物车是空的</p>
          <RouterLink to="/shop" class="link">去逛逛</RouterLink>
        </div>

        <div class="cart-footer" v-if="cartItems.length > 0">
          <div class="cart-summary">
            <div class="summary-row">
              <span>商品总价</span>
              <span>¥{{ cartTotal }}</span>
            </div>
            <div class="summary-row total">
              <span>合计</span>
              <span class="total-price">¥{{ cartTotal }}</span>
            </div>
          </div>
          <button class="btn btn-primary checkout-btn" @click="goCheckout">
            去结算
          </button>
        </div>
      </aside>
    </div>
  </section>
</template>

<script setup lang="ts">
import { ref, computed, reactive, onMounted } from "vue";
import DataState from "@/components/DataState.vue";
import { fetchProducts, fetchShopCategories, addCartItem } from "@/services/modules/shop";
import { mockProducts } from "@/mocks/shop";
import { toErrorMessage } from "@/services/http";

interface CartItem {
  id: number;
  name: string;
  price: number;
  quantity: number;
  image: string;
}

const loading = ref(false);
const error = ref("");
const categories = ref<any[]>([]);
const products = ref<any[]>([]);
const selectedCategory = ref<number | null>(null);
const keyword = ref("");
const sortBy = ref("sales");
const cartItems = ref<CartItem[]>([]);

const sortOptions = [
  { label: "热销", value: "sales" },
  { label: "价格", value: "price" },
  { label: "新品", value: "new" }
];

const filteredProducts = computed(() => {
  let result = [...products.value];
  
  if (selectedCategory.value) {
    result = result.filter(p => p.category_id === selectedCategory.value);
  }
  
  if (keyword.value) {
    result = result.filter(p => 
      p.name.toLowerCase().includes(keyword.value.toLowerCase())
    );
  }
  
  if (sortBy.value === "price") {
    result.sort((a, b) => a.price - b.price);
  }
  
  return result;
});

const cartTotal = computed(() => {
  return cartItems.value.reduce((sum, item) => sum + item.price * item.quantity, 0);
});

const loadProducts = async () => {
  loading.value = true;
  error.value = "";
  try {
    const data = await fetchProducts({ page: 1, page_size: 20 });
    products.value = data.list || [];
  } catch (e) {
    console.warn("Failed to fetch products, using mock data", e);
    products.value = mockProducts;
  } finally {
    loading.value = false;
  }
};

const selectCategory = (id: number) => {
  selectedCategory.value = selectedCategory.value === id ? null : id;
};

const addToCart = (product: any) => {
  const existing = cartItems.value.find(item => item.id === product.id);
  if (existing) {
    existing.quantity++;
  } else {
    cartItems.value.push({
      id: product.id,
      name: product.name,
      price: product.price,
      quantity: 1,
      image: product.image_url
    });
  }
};

const increaseQty = (item: CartItem) => {
  item.quantity++;
};

const decreaseQty = (item: CartItem) => {
  if (item.quantity > 1) {
    item.quantity--;
  } else {
    removeItem(item);
  }
};

const removeItem = (item: CartItem) => {
  cartItems.value = cartItems.value.filter(i => i.id !== item.id);
};

const goCheckout = () => {
  alert(`订单提交成功！\n合计金额：¥${cartTotal.value}\n感谢您的购买！`);
  cartItems.value = [];
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
});
</script>

<style scoped lang="scss">
.shop-page {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.filter-section {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 24px;
  border-radius: 16px;
  gap: 20px;
}

.category-tabs {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.cat-tab {
  padding: 8px 16px;
  background: var(--surface-tint);
  border: 1px solid var(--border-warm-mid);
  border-radius: 20px;
  cursor: pointer;
  font-size: 14px;
  color: var(--muted);
  transition: all 0.2s;
  
  &:hover {
    background: var(--chip-active-bg);
    border-color: var(--chip-border);
  }
  
  &.active {
    background: var(--primary);
    color: #fff;
    border-color: var(--primary-strong);
  }
}

.search-bar {
  display: flex;
  gap: 8px;
}

.search-input {
  padding: 8px 16px;
  border: 1px solid #eddacc;
  border-radius: 20px;
  width: 200px;
  outline: none;
  
  &:focus {
    border-color: #ff9d7a;
  }
}

.search-btn {
  padding: 8px 20px;
  background: linear-gradient(135deg, var(--primary) 0%, var(--primary-strong) 100%);
  border: none;
  border-radius: 20px;
  color: #fff;
  cursor: pointer;
  box-shadow: 0 6px 14px rgba(241, 124, 83, 0.22);
  
  &:hover {
    filter: brightness(1.03);
  }
}

.content-grid {
  display: grid;
  grid-template-columns: 1fr 300px;
  gap: 24px;
  align-items: start;
}

.products-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
  font-size: 14px;
  color: #7d7068;
}

.sort-options {
  display: flex;
  gap: 8px;
}

.sort-btn {
  padding: 4px 12px;
  background: none;
  border: 1px solid #f0dccb;
  border-radius: 12px;
  cursor: pointer;
  font-size: 13px;
  color: #7d7068;
  
  &:hover, &.active {
    background: #fff1e5;
    color: #8a4f33;
    border-color: #ffd5b8;
  }
}

.products-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(220px, 1fr));
  gap: 20px;
}

.product-card {
  background: #fff;
  border-radius: 16px;
  overflow: hidden;
  border: 1px solid #f0dccb;
  transition: all 0.3s ease;
  
  &:hover {
    transform: translateY(-4px);
    box-shadow: 0 12px 24px rgba(128, 84, 52, 0.1);
  }
}

.product-image {
  height: 180px;
  position: relative;
  overflow: hidden;
  
  img {
    width: 100%;
    height: 100%;
    object-fit: cover;
  }
}

.discount-tag {
  position: absolute;
  top: 12px;
  left: 12px;
  background: var(--primary-strong);
  color: #fff;
  padding: 4px 8px;
  border-radius: 8px;
  font-size: 12px;
  font-weight: 700;
  box-shadow: 0 2px 8px rgba(241, 124, 83, 0.35);
}

.product-info {
  padding: 16px;
}

.product-info h3 {
  margin: 0 0 8px;
  font-size: 16px;
  color: #2f2a26;
  display: -webkit-box;
  -webkit-line-clamp: 1;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.subtitle {
  margin: 0 0 12px;
  font-size: 13px;
  color: var(--on-white-text);
}

.product-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.price-section {
  display: flex;
  align-items: baseline;
  gap: 8px;
}

.price {
  font-size: 20px;
  font-weight: 700;
  color: var(--primary-strong);
}

.original-price {
  font-size: 13px;
  color: var(--on-white-text);
  text-decoration: line-through;
}

.sales {
  font-size: 12px;
  color: var(--on-white-text);
}

.product-actions {
  .add-cart-btn {
    width: 100%;
  }
}

.cart-sidebar {
  position: sticky;
  top: 20px;
  display: flex;
  flex-direction: column;
  max-height: 600px;
}

.cart-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-bottom: 16px;
  border-bottom: 1px solid #f0dccb;
  
  h3 {
    margin: 0;
    font-size: 18px;
    color: #2f2a26;
  }
  
  .item-count {
    background: #ff9d7a;
    color: #fff;
    padding: 2px 8px;
    border-radius: 10px;
    font-size: 12px;
  }
}

.cart-items {
  flex: 1;
  overflow-y: auto;
  padding: 12px 0;
}

.cart-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 10px 0;
  border-bottom: 1px solid #f5f0eb;
  
  &:last-child {
    border-bottom: none;
  }
}

.item-image {
  width: 50px;
  height: 50px;
  border-radius: 8px;
  object-fit: cover;
}

.item-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  
  .item-name {
    font-size: 13px;
    color: #2f2a26;
    display: -webkit-box;
    -webkit-line-clamp: 1;
    -webkit-box-orient: vertical;
    overflow: hidden;
  }
  
  .item-price {
    font-size: 14px;
    font-weight: 700;
    color: var(--primary-strong);
  }
}

.item-qty {
  display: flex;
  align-items: center;
  gap: 8px;
  
  button {
    width: 24px;
    height: 24px;
    border: 1px solid #f0dccb;
    border-radius: 6px;
    background: #fff;
    cursor: pointer;
    display: flex;
    align-items: center;
    justify-content: center;
    
    &:hover {
      background: #fff8f5;
    }
  }
  
  span {
    min-width: 20px;
    text-align: center;
    font-size: 14px;
  }
}

.remove-btn {
  background: none;
  border: none;
  color: var(--on-white-text);
  cursor: pointer;
  font-size: 18px;
  
  &:hover {
    color: var(--primary-strong);
  }
}

.cart-empty {
  padding: 40px 0;
  text-align: center;
  
  p {
    color: var(--on-white-text);
    margin: 0 0 12px;
  }
  
  .link {
    color: #ff9d7a;
    font-weight: 600;
  }
}

.cart-footer {
  border-top: 1px solid #f0dccb;
  padding-top: 16px;
}

.cart-summary {
  margin-bottom: 16px;
}

.summary-row {
  display: flex;
  justify-content: space-between;
  font-size: 14px;
  color: #7d7068;
  margin-bottom: 8px;
  
  &.total {
    font-size: 16px;
    font-weight: 700;
    color: #2f2a26;
    margin-top: 8px;
    padding-top: 8px;
    border-top: 1px dashed #f0dccb;
  }
}

.total-price {
  color: var(--primary-strong);
  font-size: 20px;
}

.checkout-btn {
  width: 100%;
}

@media (max-width: 1024px) {
  .content-grid {
    grid-template-columns: 1fr;
  }
  
  .cart-sidebar {
    position: fixed;
    bottom: 0;
    left: 0;
    right: 0;
    max-height: 50vh;
    border-radius: 20px 20px 0 0;
    box-shadow: 0 -4px 20px rgba(102, 72, 48, 0.12);
  }
  
  .filter-section {
    flex-direction: column;
    align-items: flex-start;
  }
  
  .category-tabs {
    width: 100%;
    overflow-x: auto;
  }
}
</style>