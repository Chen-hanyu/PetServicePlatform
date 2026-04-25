<template>
  <div class="checkout-page">
    <div v-if="paid" class="success-card">
      <div class="success-icon">✓</div>
      <h1>支付成功</h1>
      <p>感谢您的购买（演示环境，未真实扣款）</p>
      <div class="success-actions">
        <RouterLink to="/shop" class="btn-outline">继续逛逛</RouterLink>
        <RouterLink to="/profile/orders" class="btn-primary">查看订单</RouterLink>
      </div>
    </div>

    <template v-else>
      <div class="checkout-grid">
        <!-- Left Column -->
        <div class="checkout-main">
          <!-- Address Section -->
          <div class="section-card">
            <div class="section-header">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M21 10c0 7-9 13-9 13s-9-6-9-13a9 9 0 0 1 18 0z"/><circle cx="12" cy="10" r="3"/>
              </svg>
              <h2>收货人信息</h2>
              <button class="change-btn">选择其他地址</button>
            </div>
            <div class="address-card">
              <div class="address-info">
                <div class="address-user">
                  <span class="user-name">小萌主 (柚子)</span>
                  <span class="default-badge">默认</span>
                </div>
                <p class="user-phone">138 **** 8888</p>
                <p class="user-address">上海市 浦东新区 樱花路 1234号 萌物大厦 A座 808室</p>
              </div>
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"/><path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z"/>
              </svg>
            </div>
          </div>

          <!-- Products Section -->
          <div class="section-card" v-if="isBuyNow && buyNowProduct">
            <div class="section-header">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M21 16V8a2 2 0 0 0-1-1.73l-7-4a2 2 0 0 0-2 0l-7 4A2 2 0 0 0 3 8v8a2 2 0 0 0 1 1.73l7 4a2 2 0 0 0 2 0l7-4A2 2 0 0 0 21 16z"/>
              </svg>
              <h2>商品清单</h2>
            </div>
            <div class="product-item">
              <div class="product-image">
                <img :src="buyNowProduct.image_url || buyNowProduct.images[0]" :alt="buyNowProduct.name" />
              </div>
              <div class="product-info">
                <h3>{{ buyNowProduct.name }}</h3>
                <p>规格：默认</p>
                <div class="product-price">
                  <span class="price">¥{{ formatPrice(buyNowProduct.price * buyNowQty) }}</span>
                  <span class="qty">× {{ buyNowQty }}</span>
                </div>
              </div>
            </div>
          </div>

          <div class="section-card" v-else-if="!isBuyNow && cart.items.length > 0">
            <div class="section-header">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M21 16V8a2 2 0 0 0-1-1.73l-7-4a2 2 0 0 0-2 0l-7 4A2 2 0 0 0 3 8v8a2 2 0 0 0 1 1.73l7 4a2 2 0 0 0 2 0l7-4A2 2 0 0 0 21 16z"/>
              </svg>
              <h2>商品清单</h2>
            </div>
            <div v-for="item in cart.items" :key="item.id" class="product-item">
              <div class="product-image">
                <img :src="item.image" :alt="item.name" />
              </div>
              <div class="product-info">
                <h3>{{ item.name }}</h3>
                <p>规格：默认</p>
                <div class="product-price">
                  <span class="price">¥{{ formatPrice(item.price * item.quantity) }}</span>
                  <span class="qty">× {{ item.quantity }}</span>
                </div>
              </div>
            </div>
          </div>

          <!-- Options Section -->
          <div class="section-card">
            <div class="option-item">
              <div class="option-left">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <path d="M20.59 13.41l-7.17 7.17a2 2 0 0 1-2.83 0L2 12V2h10l8.59 8.59a2 2 0 0 1 0 2.82z"/><line x1="7" y1="7" x2="7.01" y2="7"/>
                </svg>
                <span>优惠券</span>
              </div>
              <div class="option-right">
                <span class="coupon-badge">2张可用</span>
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <polyline points="9 18 15 12 9 6"/>
                </svg>
              </div>
            </div>

            <div class="option-item">
              <div class="option-left">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <polygon points="12 2 15.09 8.26 22 9.27 17 14.14 18.18 21.02 12 17.77 5.82 21.02 7 14.14 2 9.27 8.91 8.26 12 2"/>
                </svg>
                <div class="option-info">
                  <span>积分抵扣</span>
                  <p>可用 2000 积分，可抵扣 ¥20.00</p>
                </div>
              </div>
              <label class="switch">
                <input type="checkbox" v-model="usePoints" />
                <span class="slider"></span>
              </label>
            </div>

            <div class="remark-section">
              <label>订单备注</label>
              <textarea v-model="remark" placeholder="有什么想对店主说的话吗？" rows="2"></textarea>
            </div>
          </div>
        </div>

        <!-- Right Column - Summary -->
        <div class="checkout-summary">
          <div class="summary-card">
            <h2>费用明细</h2>
            <div class="summary-list">
              <div class="summary-item">
                <span>商品总额</span>
                <span>¥{{ formatPrice(subtotal) }}</span>
              </div>
              <div class="summary-item">
                <span>运费</span>
                <span>¥0.00</span>
              </div>
              <div class="summary-item" v-if="discount > 0">
                <span>优惠金额</span>
                <span class="discount">-¥{{ formatPrice(discount) }}</span>
              </div>
            </div>
            <div class="summary-total">
              <span>应付合计</span>
              <span class="total-price">¥{{ formatPrice(total) }}</span>
            </div>

            <h3 class="payment-title">支付方式</h3>
            <div class="payment-options">
              <label :class="['payment-option', { active: paymentMethod === 'wechat' }]">
                <input type="radio" v-model="paymentMethod" value="wechat" />
                <svg viewBox="0 0 24 24" fill="#07C160">
                  <path d="M8.691 2.188C3.891 2.188 0 5.476 0 9.53c0 2.212 1.17 4.203 3.002 5.55a.59.59 0 0 1 .213.665l-.39 1.48c-.019.07-.048.141-.048.213 0 .163.13.295.29.295a.326.326 0 0 0 .167-.054l1.903-1.114a.864.864 0 0 1 .717-.098 10.16 10.16 0 0 0 2.837.403c.276 0 .543-.027.811-.05-.857-2.577.157-4.125.157-4.125-3.319 1.221-4.882 2.71-4.882 2.71-1.335 2.125-.498 3.22-.498 3.22-.96 1.426-1.252 2.373-1.252 2.373-.038.166-.08.332-.115.498-.622-.085-1.252-.154-1.883-.216a.723.723 0 0 0-.485.074C1.023 19.224 0 20.556 0 21.964c0 .776.25 1.481.686 2.066A.585.585 0 0 0 1.5 24a.584.584 0 0 0 .448-.215c.45-.598.74-1.325.82-2.108.155.02.314.03.476.03 2.8 0 5.19-1.17 5.19-1.17a5.173 5.173 0 0 0-.24-1.833s2.142-.66 4.706-.91a17.416 17.416 0 0 0-.48-1.978c-.04-.12-.078-.24-.115-.36l.03-.02c-.08-.01-.16-.03-.24-.05l-.03.02a11.112 11.112 0 0 0-.24.05c-.12.03-.24.06-.36.1a11.11 11.11 0 0 0-.34.11 7.61 7.61 0 0 0-.33.13l-.03.01a9.697 9.697 0 0 0-.29.14l-.01.01a10.46 10.46 0 0 0-.26.16l-.01.01a8.863 8.863 0 0 0-.23.18l-.01.01a10.09 10.09 0 0 0-.21.2l-.01.01a7.84 7.84 0 0 0-.17.21l-.02.02c-.05.07-.1.14-.15.22-.04.06-.08.12-.11.18a7.45 7.45 0 0 0-.09.18 7.23 7.23 0 0 0-.07.18c-.02.06-.04.12-.05.18l-.03.18c-.01.06-.01.12-.02.18-.01.06-.01.12-.01.18v.37c0 .06 0 .12.01.18v.17c0 .06.01.12.02.18l.03.17c.01.06.03.12.05.18.02.06.03.12.05.17l.06.17c.02.06.04.11.07.17l.06.16c.02.05.05.11.08.16.03.05.05.1.08.15l.09.15c.03.05.06.1.1.15l.09.14.11.14.1.13c.04.04.07.09.11.13l.11.12c.04.04.08.08.12.12l.12.11c.04.04.08.08.13.11l.12.11.14.1c.04.03.09.07.13.1l.15.09c.05.03.09.06.14.09l.15.08c.05.03.1.05.16.08l.15.07.16.07c.05.02.11.04.16.06l.17.06c.06.02.11.04.17.05l.17.04.18.04c.06.01.12.03.18.04l.18.03c.06.01.12.02.18.02l.19.02c.06 0 .12.01.19.01.06 0 .12 0 .19-.01l.18-.02.18-.03c.06-.01.12-.02.18-.04l.18-.04.17-.05.17-.06.17-.06c.06-.02.11-.04.17-.06l.16-.07.16-.08.15-.08c.05-.03.1-.05.16-.08l.15-.09.14-.09.14-.1c.05-.03.09-.07.14-.1l.12-.11.12-.11c.04-.04.08-.08.12-.12l.11-.12c.04-.04.07-.09.11-.13l.1-.14c.03-.05.07-.09.1-.14l.09-.15.09-.15.08-.16c.03-.05.05-.11.08-.16l.06-.17c.02-.06.05-.11.07-.17l.06-.17.05-.17.05-.18c.01-.06.03-.12.04-.18l.03-.18.02-.18.01-.18v-.18-.37c0-.06-.01-.12-.01-.18l-.01-.18c-.01-.06-.02-.12-.02-.18l-.03-.18c-.01-.06-.03-.12-.05-.18-.02-.06-.03-.12-.05-.17-.02-.06-.04-.12-.06-.17-.02-.06-.04-.11-.06-.17-.02-.05-.05-.11-.07-.16l-.06-.16-.07-.15-.08-.15-.09-.15c-.03-.05-.06-.1-.09-.15l-.09-.14-.1-.14-.11-.13-.12-.12-.12-.11-.13-.11-.13-.1-.14-.1-.15-.09-.15-.08-.16-.08-.16-.07-.17-.06c-.06-.02-.11-.04-.17-.06l-.17-.05-.17-.04-.18-.04-.18-.03-.18-.02-.19-.02c-.06 0-.12-.01-.19-.01-.06 0-.12 0-.19.01-.06 0-.12.01-.18.02l-.18.03-.18.04-.18.04-.17.05-.17.06-.17.06-.16.07-.16.08-.15.08-.15.09-.14.09-.14.1-.13.1-.12.11-.12.11c-.04.04-.08.08-.12.12l-.11.12-.11.13-.1.14-.09.14-.09.15-.08.15-.08.16-.06.16-.06.17-.05.17-.05.17-.04.18-.03.18-.02.18-.01.18v.18.37c0 .06 0 .12.01.18.01.06.02.12.02.18.01.06.03.12.03.18.02.06.03.12.05.18.02.06.04.12.05.17l.06.17.06.16c.02.05.05.11.07.16l.08.16.09.15c.03.05.06.1.09.15l.09.14.1.14.11.13.12.12.12.11.13.11.13.1.14.1.15.09.15.08.16.08.16.07.17.06.17.05.17.04.18.04.18.03.18.02c.06.01.12.01.19.01Z"/>
                </svg>
                <span>微信支付</span>
              </label>
              <label :class="['payment-option', { active: paymentMethod === 'alipay' }]">
                <input type="radio" v-model="paymentMethod" value="alipay" />
                <svg viewBox="0 0 24 24" fill="#1677FF">
                  <path d="M21.59 11.58c-.16-.08-.31-.17-.44-.27-.01 0-.02-.01-.04-.02-.13-.09-.27-.17-.4-.24-.02 0-.03-.01-.05-.02-.13-.06-.27-.12-.41-.16-.02 0-.04-.01-.06-.02-.14-.04-.29-.07-.44-.09h-.04c-.02 0-.04 0-.05-.01-.16-.02-.32-.02-.48-.02h-.36c-.01 0-.02 0-.03.01H18.6c-.13.02-.25.04-.38.07-.03 0-.06.01-.09.02-.13.03-.26.07-.39.11-.04.01-.08.03-.12.04-.13.05-.25.1-.38.16-.04.02-.08.03-.12.05-.12.06-.24.12-.35.19-.05.03-.1.05-.15.08-.11.06-.21.13-.31.2-.04.03-.08.05-.12.08-.1.07-.19.15-.28.23-.04.03-.08.07-.12.1-.09.08-.17.16-.25.25-.04.04-.07.07-.11.11-.08.09-.15.18-.22.27-.03.04-.06.08-.09.12-.06.08-.12.17-.17.26-.03.04-.05.08-.08.13-.05.08-.1.17-.14.26-.03.05-.05.1-.07.15-.04.08-.08.17-.11.26-.02.05-.04.1-.05.15-.03.09-.05.18-.07.27-.01.05-.02.1-.03.15-.02.09-.03.19-.04.28 0 .05-.01.1-.01.15v.02c-.01.1-.01.2-.01.3 0 .1 0 .2.01.3v.02c0 .05.01.1.01.15.01.09.02.19.04.28.01.05.02.1.03.15.02.09.04.18.07.27.01.05.03.1.05.15.03.09.07.17.11.26.02.05.04.1.07.15.04.09.09.17.14.26.03.04.05.08.08.13.05.08.11.17.17.26.03.04.06.08.09.12.07.09.14.18.22.27.04.04.07.07.11.11.08.08.16.17.25.25.04.03.08.07.12.1.09.08.18.16.28.23.04.03.08.05.12.08.1.07.2.14.31.2.05.03.1.05.15.08.11.07.23.13.35.19.04.02.08.03.12.05.13.06.25.11.38.16.04.01.08.03.12.04.13.04.26.08.39.11.03.01.06.01.09.02.13.03.25.05.38.07.01 0 .02 0 .03.01h.04c.16.02.32.02.48.02h.36c.02 0 .03 0 .05-.01h.04c.15-.02.29-.05.44-.09.02 0 .04-.01.06-.02.14-.04.28-.1.41-.16.02-.01.03-.01.05-.02.14-.07.28-.15.4-.24.02-.01.03-.02.04-.02.13-.1.28-.19.44-.27.1-.06.21-.12.31-.17.11-.06.23-.1.35-.14.01 0 .02-.01.03-.01.13-.04.27-.07.4-.09.02 0 .03 0 .05-.01.14-.02.29-.02.44-.02h.36c.15 0 .29 0 .44.02.02 0 .03 0 .05.01.13.02.27.05.4.09.01 0 .02.01.03.01.12.04.24.08.35.14.1.05.21.11.31.17Z"/>
                </svg>
                <span>支付宝</span>
              </label>
            </div>

            <button class="btn-submit" @click="submitPay">提交订单</button>
            <div class="submit-tip">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z"/>
              </svg>
              安全支付保障
            </div>
          </div>

          <div class="promo-tip">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <polyline points="20 12 20 22 4 22 4 12"/>
              <rect x="2" y="7" width="20" height="5"/>
              <line x1="12" y1="22" x2="12" y2="7"/>
              <path d="M12 7H7.5a2.5 2.5 0 0 1 0-5C11 2 12 7 12 7z"/>
              <path d="M12 7h4.5a2.5 2.5 0 0 0 0-5C13 2 12 7 12 7z"/>
            </svg>
            <div class="promo-text">
              <strong>温馨提示：</strong>
              现在下单可获得双倍积分奖励，预计发放 410 积分，可用于下次抵扣。
            </div>
          </div>
        </div>
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
import { fetchProduct } from "@/api/modules/shop";

const route = useRoute();
const router = useRouter();
const cart = useShopCartStore();

const paid = ref(false);
const buyNowProduct = ref<any>(null);
const remark = ref("");
const usePoints = ref(false);
const paymentMethod = ref("wechat");

const isBuyNow = computed(() => route.query.buyNow === "1");
const buyNowId = computed(() => Number(route.query.id));
const buyNowQty = computed(() => {
  const q = Number(route.query.qty);
  return Number.isFinite(q) && q >= 1 ? Math.floor(q) : 1;
});

const formatPrice = (n: number) => (Number.isInteger(n) ? String(n) : n.toFixed(2));

const subtotal = computed(() => {
  if (isBuyNow.value && buyNowProduct.value) {
    return buyNowProduct.value.price * buyNowQty.value;
  }
  return cart.totalAmount;
});

const discount = computed(() => {
  return usePoints.value ? 20 : 0;
});

const total = computed(() => {
  return subtotal.value - discount.value;
});

async function loadBuyNow() {
  buyNowProduct.value = null;
  if (!isBuyNow.value) return;
  const id = buyNowId.value;
  if (!Number.isFinite(id) || id < 1) {
    router.replace("/shop");
    return;
  }
  try {
    const data = await fetchProduct(id);
    const imgs =
      data.images && data.images.length > 0 ? data.images : data.image_url ? [data.image_url] : [];
    buyNowProduct.value = {
      id: data.id,
      name: data.name,
      price: data.price,
      image_url: data.image_url,
      images: imgs
    };
  } catch {
    buyNowProduct.value = null;
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
  max-width: 1100px;
  margin: 0 auto;
  padding: 24px;
  padding-bottom: 80px;
}

.success-card {
  max-width: 500px;
  margin: 80px auto;
  padding: 48px 32px;
  background: var(--surface);
  border-radius: 20px;
  box-shadow: 0 8px 24px rgba(34, 60, 52, 0.08);
  text-align: center;
}

.success-icon {
  width: 80px;
  height: 80px;
  margin: 0 auto 24px;
  border-radius: 50%;
  background: linear-gradient(135deg, var(--primary) 0%, var(--success) 100%);
  color: #fff;
  font-size: 48px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: bold;
}

.success-card h1 {
  font-size: 28px;
  font-weight: 800;
  color: var(--text-heading);
  margin: 0 0 12px;
}

.success-card p {
  font-size: 16px;
  color: var(--muted);
  margin: 0 0 32px;
}

.success-actions {
  display: flex;
  gap: 16px;
  justify-content: center;
}

.checkout-grid {
  display: grid;
  grid-template-columns: 1fr 380px;
  gap: 24px;
  align-items: flex-start;
}

.checkout-main {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.section-card {
  background: var(--surface);
  border-radius: 16px;
  padding: 20px;
  box-shadow: 0 4px 16px rgba(34, 60, 52, 0.06);
}

.section-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 16px;
  
  svg {
    width: 22px;
    height: 22px;
    color: var(--primary);
  }
  
  h2 {
    font-size: 17px;
    font-weight: 700;
    color: var(--text-heading);
    margin: 0;
    flex: 1;
  }
}

.change-btn {
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

.address-card {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 16px;
  background: var(--chip-bg);
  border: 1px dashed var(--primary);
  border-radius: 12px;
  
  svg {
    width: 20px;
    height: 20px;
    color: var(--muted);
  }
}

.address-info {
  flex: 1;
}

.address-user {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 8px;
}

.user-name {
  font-size: 16px;
  font-weight: 700;
  color: var(--text-heading);
}

.default-badge {
  padding: 2px 8px;
  background: var(--primary);
  color: #fff;
  border-radius: 10px;
  font-size: 11px;
  font-weight: 700;
}

.user-phone, .user-address {
  margin: 0;
  font-size: 14px;
  color: var(--muted);
}

.user-phone {
  margin-bottom: 4px;
}

.product-item {
  display: flex;
  gap: 16px;
  padding: 16px 0;
  border-bottom: 1px solid var(--border-warm);
  
  &:last-child {
    border-bottom: none;
  }
}

.product-image {
  width: 100px;
  height: 100px;
  border-radius: 12px;
  overflow: hidden;
  flex-shrink: 0;
  background: var(--surface-muted);
  
  img {
    width: 100%;
    height: 100%;
    object-fit: cover;
  }
}

.product-info {
  flex: 1;
  
  h3 {
    font-size: 16px;
    font-weight: 700;
    color: var(--text-heading);
    margin: 0 0 8px;
  }
  
  p {
    font-size: 13px;
    color: var(--muted);
    margin: 0 0 12px;
  }
}

.product-price {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.price {
  font-size: 18px;
  font-weight: 700;
  color: var(--primary);
}

.qty {
  font-size: 14px;
  color: var(--muted);
}

.option-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 14px 0;
  border-bottom: 1px solid var(--border-warm);
  
  &:last-child {
    border-bottom: none;
  }
}

.option-left {
  display: flex;
  align-items: center;
  gap: 12px;
  
  svg {
    width: 22px;
    height: 22px;
    color: var(--primary);
  }
  
  span {
    font-size: 15px;
    font-weight: 600;
    color: var(--text-heading);
  }
}

.option-info {
  span {
    font-size: 15px;
    font-weight: 600;
    color: var(--text-heading);
    display: block;
  }
  
  p {
    font-size: 12px;
    color: var(--muted);
    margin: 4px 0 0;
  }
}

.option-right {
  display: flex;
  align-items: center;
  gap: 8px;
  
  svg {
    width: 18px;
    height: 18px;
    color: var(--muted);
  }
}

.coupon-badge {
  padding: 4px 10px;
  background: rgba(233, 122, 122, 0.1);
  color: var(--danger);
  border-radius: 10px;
  font-size: 12px;
  font-weight: 700;
}

.switch {
  position: relative;
  display: inline-block;
  width: 48px;
  height: 26px;
  
  input {
    opacity: 0;
    width: 0;
    height: 0;
  }
}

.slider {
  position: absolute;
  cursor: pointer;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: var(--border-warm);
  transition: 0.3s;
  border-radius: 34px;
  
  &:before {
    position: absolute;
    content: "";
    height: 20px;
    width: 20px;
    left: 3px;
    bottom: 3px;
    background-color: white;
    transition: 0.3s;
    border-radius: 50%;
  }
}

input:checked + .slider {
  background-color: var(--primary);
}

input:checked + .slider:before {
  transform: translateX(22px);
}

.remark-section {
  padding-top: 16px;
  
  label {
    display: block;
    font-size: 14px;
    font-weight: 600;
    color: var(--muted);
    margin-bottom: 8px;
  }
  
  textarea {
    width: 100%;
    padding: 12px;
    border: 1px solid var(--border-warm);
    border-radius: 10px;
    font-size: 14px;
    resize: none;
    background: var(--surface-muted);
    
    &:focus {
      outline: none;
      border-color: var(--primary);
    }
  }
}

.checkout-summary {
  display: flex;
  flex-direction: column;
  gap: 16px;
  position: sticky;
  top: 100px;
}

.summary-card {
  background: var(--surface);
  border-radius: 16px;
  padding: 24px;
  box-shadow: 0 4px 16px rgba(34, 60, 52, 0.06);
  
  h2 {
    font-size: 18px;
    font-weight: 700;
    color: var(--text-heading);
    margin: 0 0 20px;
  }
}

.summary-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
  padding-bottom: 16px;
  border-bottom: 1px solid var(--border-warm);
}

.summary-item {
  display: flex;
  justify-content: space-between;
  font-size: 14px;
  color: var(--muted);
  
  .discount {
    color: var(--danger);
  }
}

.summary-total {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 0;
  
  span:first-child {
    font-size: 15px;
    font-weight: 600;
    color: var(--text-heading);
  }
  
  .total-price {
    font-size: 28px;
    font-weight: 800;
    color: var(--primary);
  }
}

.payment-title {
  font-size: 15px;
  font-weight: 700;
  color: var(--text-heading);
  margin: 0 0 12px;
}

.payment-options {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.payment-option {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 14px 16px;
  border: 1px solid var(--border-warm);
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.2s ease;
  
  input {
    display: none;
  }
  
  svg {
    width: 24px;
    height: 24px;
  }
  
  span {
    font-size: 15px;
    font-weight: 600;
    color: var(--text-heading);
  }
  
  &.active {
    background: rgba(255, 155, 122, 0.1);
    border-color: var(--primary);
  }
}

.btn-submit {
  width: 100%;
  padding: 16px;
  margin-top: 20px;
  background: var(--primary);
  color: #fff;
  border: none;
  border-radius: 12px;
  font-size: 17px;
  font-weight: 700;
  cursor: pointer;
  transition: all 0.2s ease;
  box-shadow: 0 4px 12px rgba(255, 155, 122, 0.3);
  
  &:hover {
    opacity: 0.9;
    transform: translateY(-2px);
  }
}

.submit-tip {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  margin-top: 16px;
  font-size: 12px;
  color: var(--muted);
  
  svg {
    width: 16px;
    height: 16px;
  }
}

.promo-tip {
  display: flex;
  align-items: flex-start;
  gap: 12px;
  padding: 16px;
  background: rgba(255, 214, 107, 0.1);
  border: 1px solid rgba(255, 214, 107, 0.3);
  border-radius: 12px;
  
  svg {
    width: 22px;
    height: 22px;
    color: var(--primary);
    flex-shrink: 0;
  }
}

.promo-text {
  font-size: 13px;
  color: var(--muted);
  line-height: 1.5;
  
  strong {
    color: var(--text-heading);
  }
}

@media (max-width: 1024px) {
  .checkout-grid {
    grid-template-columns: 1fr;
  }
  
  .checkout-summary {
    position: static;
  }
}

@media (max-width: 768px) {
  .checkout-page {
    padding: 16px;
  }
  
  .product-item {
    flex-direction: column;
  }
  
  .product-image {
    width: 100%;
    height: 120px;
  }
}
</style>
