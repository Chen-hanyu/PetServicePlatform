<template>
  <section class="card page">
    <div class="top-row">
      <h2 class="section-title">宠物商城</h2>
      <div class="top-actions">
        <button class="btn btn-secondary" @click="loadCart">刷新购物车</button>
        <button class="btn btn-primary" @click="createOrderNow" :disabled="ordering || cart.items.length === 0">{{ ordering ? "提交中..." : "一键下单" }}</button>
      </div>
    </div>

    <div class="filter-row">
      <select v-model.number="categoryId" class="input" @change="loadProducts">
        <option :value="0">全部分类</option>
        <option v-for="item in categories" :key="item.id" :value="item.id">{{ item.name }}</option>
      </select>
      <input v-model.trim="keyword" class="input" placeholder="搜索商品" />
      <button class="btn btn-secondary" @click="loadProducts">搜索</button>
    </div>

    <DataState :loading="loading" :error="error" :empty="products.length === 0" empty-text="暂无商品">
      <div class="grid">
        <article v-for="product in products" :key="product.id" class="product-card">
          <img v-if="product.image_url" :src="product.image_url" :alt="product.name" />
          <h3>{{ product.name }}</h3>
          <p class="muted">{{ product.subtitle || "宠物友好用品" }}</p>
          <div class="price">¥{{ product.price }}</div>
          <button class="btn btn-primary" @click="addToCart(product.id)">加入购物车</button>
        </article>
      </div>
    </DataState>

    <section class="card inner">
      <h3>购物车</h3>
      <p class="muted">总价：¥{{ cart.total_amount || 0 }}</p>
      <ul>
        <li v-for="item in cart.items" :key="item.id">{{ item.product?.name || `商品#${item.product_id}` }} × {{ item.quantity }}</li>
      </ul>
    </section>
  </section>
</template>

<script setup lang="ts">
import { onMounted, ref } from "vue";
import DataState from "@/components/DataState.vue";
import { addCartItem, createOrder, fetchCart, fetchProducts, fetchShopCategories } from "@/services/modules/shop";
import { toErrorMessage } from "@/services/http";
import type { CartData, ProductCategory, ProductSummary } from "@/types/shop";

const loading = ref(false);
const ordering = ref(false);
const error = ref("");
const categories = ref<ProductCategory[]>([]);
const products = ref<ProductSummary[]>([]);
const cart = ref<CartData>({ items: [], total_amount: 0 });
const categoryId = ref(0);
const keyword = ref("");

const loadProducts = async () => {
  loading.value = true;
  error.value = "";
  try {
    const data = await fetchProducts({ category: categoryId.value || undefined, keyword: keyword.value || undefined, page: 1, page_size: 10 });
    products.value = data.list || [];
  } catch (e) {
    error.value = toErrorMessage(e);
  } finally {
    loading.value = false;
  }
};

const loadCart = async () => {
  try { cart.value = await fetchCart(); } catch { cart.value = { items: [], total_amount: 0 }; }
};

const addToCart = async (productId: number) => {
  try { cart.value = await addCartItem(productId, 1); } catch (e) { error.value = toErrorMessage(e); }
};

const createOrderNow = async () => {
  if (cart.value.items.length === 0) return;
  ordering.value = true;
  try {
    await createOrder({ item_ids: cart.value.items.map((x) => x.id), receiver_name: "测试用户", receiver_phone: "13800000000", receiver_address: "上海市浦东新区测试路 100 号", remark: "前端演示下单" });
    await loadCart();
  } catch (e) {
    error.value = toErrorMessage(e);
  } finally {
    ordering.value = false;
  }
};

onMounted(async () => {
  try { categories.value = await fetchShopCategories(); } catch { categories.value = []; }
  await loadProducts();
  await loadCart();
});
</script>

<style scoped lang="scss">
.page { display: grid; gap: 14px; }
.top-row { display: flex; justify-content: space-between; gap: 10px; }
.top-actions { display: flex; gap: 8px; }
.filter-row { display: grid; grid-template-columns: 160px 1fr auto; gap: 8px; }
.grid { display: grid; gap: 12px; grid-template-columns: repeat(auto-fit, minmax(200px, 1fr)); }
.product-card { border: 1px solid #e4eeea; border-radius: 14px; padding: 12px; display: grid; gap: 8px; background: #fff; }
.product-card img { width: 100%; height: 130px; object-fit: cover; border-radius: 10px; }
.product-card h3 { margin: 0; }
.price { font-size: 20px; font-weight: 700; color: #2d5a4d; }
.inner { border: 1px dashed #d8ebe3; }
@media (max-width: 768px) { .top-row { flex-direction: column; align-items: flex-start; } .filter-row { grid-template-columns: 1fr; }}
</style>
