<template>
  <div class="checkout-page">
    <nav class="breadcrumb card" aria-label="面包屑导航">
      <RouterLink to="/home">首页</RouterLink>
      <span class="sep">/</span>
      <RouterLink to="/shop">宠物商城</RouterLink>
      <span class="sep">/</span>
      <span class="current">确认订单</span>
    </nav>

    <div v-if="paid" class="paid-card card">
      <p class="paid-icon" aria-hidden="true">✓</p>
      <h1>支付成功</h1>
      <p class="paid-sub">感谢您的购买（演示环境，未真实扣款）</p>
      <div class="paid-actions">
        <RouterLink to="/shop" class="btn-outline">继续逛逛</RouterLink>
        <RouterLink to="/profile/orders" class="btn-primary">查看订单</RouterLink>
      </div>
    </div>

    <template v-else>
      <section v-if="isBuyNow && buyNowProduct" class="order-card card">
        <h2 class="section-title">立即购买</h2>
        <div class="line-item">
          <img :src="buyNowProduct.image_url || buyNowProduct.images[0]" class="thumb" alt="" />
          <div class="meta">
            <span class="name">{{ buyNowProduct.name }}</span>
            <span class="sub">× {{ buyNowQty }}</span>
          </div>
          <span class="line-price">¥{{ formatPrice(buyNowProduct.price * buyNowQty) }}</span>
        </div>
      </section>

      <section v-else-if="!isBuyNow && cart.items.length > 0" class="order-card card">
        <h2 class="section-title">购物车结算</h2>
        <div v-for="item in cart.items" :key="item.id" class="line-item">
          <img :src="item.image" class="thumb" alt="" />
          <div class="meta">
            <span class="name">{{ item.name }}</span>
            <span class="sub">× {{ item.quantity }}</span>
          </div>
          <span class="line-price">¥{{ formatPrice(item.price * item.quantity) }}</span>
        </div>
      </section>

      <div v-else class="empty-card card">
        <p>没有可结算的商品</p>
        <RouterLink to="/shop" class="link-shop">返回商城</RouterLink>
      </div>

      <div v-if="canPay" class="pay-bar card">
        <div class="pay-total">
          <span>应付金额</span>
          <strong>¥{{ formatPrice(payAmount) }}</strong>
        </div>
        <p class="pay-tip">演示收银台 · 支持微信 / 支付宝（模拟）</p>
        <button type="button" class="btn-pay" @click="submitPay">确认支付</button>
      </div>
    </template>

    <CommerceDock />
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch } from "vue";
import { useRoute, useRouter } from "vue-router";
import CommerceDock from "@/components/shop/CommerceDock.vue";
import { useShopCartStore } from "@/store/shopCart";
import { getMockProductById } from "@/mocks/shop";
import { fetchProduct } from "@/services/modules/shop";

const route = useRoute();
const router = useRouter();
const cart = useShopCartStore();

const paid = ref(false);
const buyNowProduct = ref<any>(null);

const isBuyNow = computed(() => route.query.buyNow === "1");
const buyNowId = computed(() => Number(route.query.id));
const buyNowQty = computed(() => {
  const q = Number(route.query.qty);
  return Number.isFinite(q) && q >= 1 ? Math.floor(q) : 1;
});

const formatPrice = (n: number) => (Number.isInteger(n) ? String(n) : n.toFixed(2));

const payAmount = computed(() => {
  if (isBuyNow.value && buyNowProduct.value) {
    return buyNowProduct.value.price * buyNowQty.value;
  }
  return cart.totalAmount;
});

const canPay = computed(() => {
  if (paid.value) return false;
  if (isBuyNow.value) return buyNowProduct.value != null;
  return cart.items.length > 0;
});

async function loadBuyNow() {
  buyNowProduct.value = null;
  if (!isBuyNow.value) return;
  const id = buyNowId.value;
  if (!Number.isFinite(id) || id < 1) {
    router.replace("/shop");
    return;
  }
  const mock = getMockProductById(id);
  try {
    const data = await fetchProduct(id);
    const imgs =
      data.images && data.images.length > 0 ? data.images : data.image_url ? [data.image_url] : mock?.images || [];
    buyNowProduct.value = {
      id: data.id,
      name: data.name,
      price: data.price,
      image_url: data.image_url || mock?.image_url,
      images: imgs.length ? imgs : mock?.images || []
    };
  } catch {
    buyNowProduct.value = mock;
  }
  if (!buyNowProduct.value) router.replace("/shop");
}

watch(
  () => route.query,
  () => {
    paid.value = false;
    loadBuyNow();
  },
  { immediate: true }
);

function submitPay() {
  if (isBuyNow.value) {
    paid.value = true;
    return;
  }
  cart.clearCart();
  paid.value = true;
}
</script>

<style scoped lang="scss">
.checkout-page {
  max-width: 720px;
  margin: 0 auto;
  padding: 0 12px 48px;
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
  }
}

.section-title {
  margin: 0 0 14px;
  font-size: 17px;
  font-weight: 900;
  color: var(--text-heading-soft);
}

.order-card {
  padding: 18px 20px;
  margin-bottom: 14px;
}

.line-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 0;
  border-bottom: 1px solid var(--border-warm);

  &:last-child {
    border-bottom: none;
  }
}

.thumb {
  width: 64px;
  height: 64px;
  border-radius: 10px;
  object-fit: cover;
  border: 1px solid var(--border-warm);
}

.meta {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.name {
  font-size: 15px;
  font-weight: 700;
  color: var(--text);
}

.sub {
  font-size: 13px;
  color: var(--muted);
}

.line-price {
  font-size: 17px;
  font-weight: 900;
  color: #ff5000;
}

.pay-bar {
  padding: 20px;
  position: sticky;
  bottom: 16px;
}

.pay-total {
  display: flex;
  justify-content: space-between;
  align-items: baseline;
  margin-bottom: 8px;
  font-size: 15px;
  color: var(--muted);

  strong {
    font-size: 28px;
    color: #ff5000;
  }
}

.pay-tip {
  margin: 0 0 16px;
  font-size: 12px;
  color: var(--muted-soft);
}

.btn-pay {
  width: 100%;
  padding: 16px;
  border: none;
  border-radius: 14px;
  font-size: 17px;
  font-weight: 900;
  color: #fff;
  cursor: pointer;
  background: linear-gradient(135deg, #ff6a3c 0%, #ff5000 100%);
  box-shadow: 0 8px 24px rgba(255, 80, 0, 0.25);

  &:hover {
    filter: brightness(1.05);
  }
}

.empty-card {
  padding: 40px 20px;
  text-align: center;
  color: var(--muted);
  font-weight: 700;

  .link-shop {
    display: inline-block;
    margin-top: 12px;
    color: var(--primary-strong);
    font-weight: 800;
  }
}

.paid-card {
  padding: 48px 24px;
  text-align: center;
}

.paid-icon {
  width: 64px;
  height: 64px;
  margin: 0 auto 16px;
  border-radius: 50%;
  background: linear-gradient(135deg, #8fd4a8 0%, #6bbf8b 100%);
  color: #fff;
  font-size: 36px;
  line-height: 64px;
  font-weight: 900;
}

.paid-card h1 {
  margin: 0 0 8px;
  font-size: 22px;
  color: var(--text);
}

.paid-sub {
  margin: 0 0 24px;
  color: var(--muted);
  font-size: 14px;
}

.paid-actions {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  justify-content: center;
}

.btn-outline,
.btn-primary {
  padding: 12px 24px;
  border-radius: 12px;
  font-size: 15px;
  font-weight: 800;
  text-decoration: none;
}

.btn-outline {
  border: 2px solid var(--border-warm);
  color: var(--text-heading-soft);
  background: var(--surface);
}

.btn-primary {
  background: linear-gradient(135deg, var(--primary) 0%, var(--primary-strong) 100%);
  color: #fff;
  border: none;
}
</style>
